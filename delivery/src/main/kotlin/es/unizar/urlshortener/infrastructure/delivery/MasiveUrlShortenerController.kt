package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.DataCSVIn
import es.unizar.urlshortener.core.ShortUrlProperties
import es.unizar.urlshortener.core.usecases.CreateShortUrlUseCase
import es.unizar.urlshortener.core.usecases.LogClickUseCase
import es.unizar.urlshortener.core.usecases.QRUseCase
import es.unizar.urlshortener.core.usecases.RedirectUseCase
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.core.io.InputStreamResource
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.StringWriter
import javax.servlet.http.HttpServletRequest

interface MasiveUrlShortenerController {
    fun handleFileUpload( file: MultipartFile, request: HttpServletRequest): ResponseEntity<String>
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

    @PostMapping("/api/upload")
    override fun handleFileUpload(file: MultipartFile, request: HttpServletRequest): ResponseEntity<String> {
        var reader = file.inputStream.reader()
        val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(',') )
        val writer = StringWriter()
        var firstUri =""
        var data = ShortUrlProperties(
            ip = request.remoteAddr,
        )

        val lines: List<List<String>> = csvParser
            .map{DataCSVIn(url=it.get(0), qr= it.get(1))}
            .map{createShortUrlUseCase.create(url=it.url,data =data.copy(hasQR=it.hasQR()))}
            .map{
                val uri = linkTo<UrlShortenerControllerImpl> { redirectTo(it.hash, request) }.toUri().toString()
                val qr = if (it.properties.hasQR == true) linkTo<QRControllerImpl> { redirectTo(it.hash, request) }.toUri().toString() else ""
                listOf(it.redirection.target,uri,qr)
            }
        lines.forEach{ writer.write(it.joinToString(",", postfix = "\n"))}

        val resource = writer.toString()
        val url = linkTo<UrlShortenerControllerImpl> { redirectTo(firstUri, request) }.toUri()
        return ResponseEntity
            .created(url)
            .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
            .body(resource)

    }



}