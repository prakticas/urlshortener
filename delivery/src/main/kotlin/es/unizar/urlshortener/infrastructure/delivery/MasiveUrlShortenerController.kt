package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.ClickProperties
import es.unizar.urlshortener.core.DataCSVIn
import es.unizar.urlshortener.core.ShortUrlProperties
import es.unizar.urlshortener.core.usecases.CreateShortUrlUseCase
import es.unizar.urlshortener.core.usecases.LogClickUseCase
import es.unizar.urlshortener.core.usecases.RedirectUseCase
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.context.annotation.Profile
import org.springframework.core.io.InputStreamResource
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.function.ServerResponse.async
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import javax.servlet.http.HttpServletRequest
import java.util.concurrent.*


interface MasiveUrlShortenerController {
    fun handleFileUpload( file: MultipartFile, request: HttpServletRequest): ResponseEntity<Void>
}

/**
 * The implementation of the controller.
 *
 * **Note**: Spring Boot is able to discover this [RestController] without further configuration.
 */
@RestController
@Profile("MainNode")
class MasiveUrlShortenerControllerImpl(
    val createShortUrlUseCase: CreateShortUrlUseCase)
    :MasiveUrlShortenerController{

    private var emitter: SseEmitter? = null

    /// Inits sse connection and catches event from [handleFileUpload] thread and send to client
    @GetMapping(path = ["/fetchShortUrlList"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun createConnection(): SseEmitter? {
        emitter = SseEmitter(Long.MAX_VALUE)
        return emitter
    }

    @PostMapping(path = ["/api/upload"])
    override fun handleFileUpload(file: MultipartFile, request: HttpServletRequest): ResponseEntity<Void> {
            val reader = file.inputStream.reader()
            val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(','))
            csvParser
                .map{DataCSVIn(url = it.get(0), qr = it.get(1))}
                .forEach { dataCSVIn ->
                    createShortUrlUseCase.createWithError(
                        url = dataCSVIn.url, data = ShortUrlProperties(
                            ip = request.remoteAddr,
                            hasQR = dataCSVIn.hasQR()
                        )
                    ).let {
                        val uri: String
                        var qr = ""
                        val origin: String
                        if (it.url != null) {
                            uri = linkTo<UrlShortenerControllerImpl> { redirectTo(it.url!!.hash, request) }.toUri()
                                .toString()
                            qr = if (it.url!!.properties.hasQR == true)
                                linkTo<QRControllerImpl> { redirectTo(it.url!!.hash, request) }.toUri()
                                    .toString() else ""
                            origin = it.url!!.redirection.target
                        } else {
                            origin = it.origin
                            uri = it.error.msg
                        }
                        // Send event from this thread to [createConnection] thread
                        try {
                            emitter!!.send("$origin,$uri,$qr")
                        } catch (e: Exception) {
                            emitter!!.completeWithError(e)
                        }
                    }
                }
        // Stop sending events to client
        try {
            emitter!!.send("-end-")
            emitter!!.complete()
        } catch (e: Exception) {
            emitter!!.completeWithError(e)
        }
        return ResponseEntity(HttpHeaders(), HttpStatus.PROCESSING)
    }
}