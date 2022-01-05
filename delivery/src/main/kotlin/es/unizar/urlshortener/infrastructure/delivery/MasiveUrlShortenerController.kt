package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.DataCSVIn
import es.unizar.urlshortener.core.ShortUrlProperties
import es.unizar.urlshortener.core.usecases.CreateShortUrlUseCase
import es.unizar.urlshortener.core.usecases.LogClickUseCase
import es.unizar.urlshortener.core.usecases.RedirectUseCase
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Flux
import java.io.StringWriter
import java.time.Duration
import javax.servlet.http.HttpServletRequest


interface MasiveUrlShortenerController {
    fun handleFileUpload( file: MultipartFile, request: HttpServletRequest): Flux<String>
}

/**
 * The implementation of the controller.
 *
 * **Note**: Spring Boot is able to discover this [RestController] without further configuration.
 */
@RestController
class MasiveUrlShortenerControllerImpl(
    val redirectUseCase: RedirectUseCase,
    val logClickUseCase: LogClickUseCase,
    val createShortUrlUseCase: CreateShortUrlUseCase)
    :MasiveUrlShortenerController{

    @PostMapping(path = ["/api/upload"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    override fun handleFileUpload(file: MultipartFile, request: HttpServletRequest): Flux<String> {
        return Flux.create<String?> { fluxSink ->
            val reader = file.inputStream.reader()
            val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(',') )
            csvParser
                .map{DataCSVIn(url=it.get(0), qr= it.get(1))}
                .map{
                    createShortUrlUseCase.createWithError(url=it.url,data =ShortUrlProperties(
                        ip = request.remoteAddr,
                        hasQR=it.hasQR()
                    ))}
                .map{
                    val uri:String
                    val qr:String
                    val origin:String
                    if (it.url!=null){
                        uri = linkTo<UrlShortenerControllerImpl> { redirectTo(it.url!!.hash, request) }.toUri().toString()
                        qr = if (it.url!!.properties.hasQR == true) linkTo<QRControllerImpl> { redirectTo(it.url!!.hash, request) }.toUri().toString() else ""
                        origin= it.url!!.redirection.target

                    }
                    else{
                        origin=it.origin
                        uri=it.error.msg
                        qr=""
                    }
                    fluxSink.next("$origin,$uri,$qr")
                }
            fluxSink.complete()
        }.delayElements(Duration.ofMillis(100))
    }
}