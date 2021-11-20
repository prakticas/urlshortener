package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.ShortUrlProperties
import es.unizar.urlshortener.core.usecases.CreateShortUrlUseCase
import es.unizar.urlshortener.core.usecases.LogClickUseCase
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

        for (line in csvParser){
            //obtengo los valores de la linea
            var url = line.get(0)
            //var sponsor
            var qr = line.get(1)
            var data = ShortUrlProperties(
                ip = request.remoteAddr,
                sponsor = null
            )
            // calculo la URI del shortURL
            var shortUrl = createShortUrlUseCase.create(
                url = url,
                data = data

            )
            var uri = linkTo<UrlShortenerControllerImpl> { redirectTo(shortUrl.hash, request) }.toUri().toString()
            // calculo la URI del QR si fuera necesario
            var qrUri =""
            if(qr=="y"){
                qrUri="QR"//falta que el servicio delvuelva lo correcto
            }

            //Escribo la respuesta
            writer.write(uri+","+qrUri+"\n")

            if (firstUri == ""){
                firstUri= shortUrl.hash
            }
        }

        val resource = writer.toString()
        val url = linkTo<UrlShortenerControllerImpl> { redirectTo(firstUri, request) }.toUri()
        return ResponseEntity
            .created(url)
            .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
            .body(resource)

    }



}