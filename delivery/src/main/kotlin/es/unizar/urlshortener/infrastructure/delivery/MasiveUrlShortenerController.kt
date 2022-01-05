package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.infrastructure.delivery.PdfGenerator
import es.unizar.urlshortener.infrastructure.delivery.SseRepository
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
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import java.io.ByteArrayInputStream
import java.io.StringWriter
import java.util.*
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
        private val sseRepository: SseRepository,
        private val pdfGenerator: PdfGenerator,
        val redirectUseCase: RedirectUseCase,
        val logClickUseCase: LogClickUseCase,
        val createShortUrlUseCase: CreateShortUrlUseCase)
    :MasiveUrlShortenerController{

    @PostMapping("/si")
    fun generatePdf(@RequestParam("uuid") id: String): ModelAndView {
        val listener = sseRepository.createProgressListener(id)
        pdfGenerator.generatePdf(id, listener)
        val modelAndView = ModelAndView()
        modelAndView.viewName = "index.html"
        System.out.println("Pase por aqui2")
        return modelAndView
    }

    @PostMapping("/api/upload")
    override fun handleFileUpload(file: MultipartFile, request: HttpServletRequest): ResponseEntity<String> {
        var reader = file.inputStream.reader()
        val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(',') )
        val writer = StringWriter()
        var firstUri =""


        val lines: List<List<String>> = csvParser
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

                listOf(origin,uri,qr)
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