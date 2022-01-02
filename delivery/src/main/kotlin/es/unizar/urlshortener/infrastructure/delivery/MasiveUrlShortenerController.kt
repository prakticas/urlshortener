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
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.StringWriter
import java.util.*
import javax.servlet.http.HttpServletRequest


interface MasiveUrlShortenerController {
    fun handleFileUpload(@RequestParam("uuid") id: String, file: MultipartFile, request: HttpServletRequest, listener: ProgressListener): ResponseEntity<String>
}

interface ProgressListener {
    fun onProgress(url: String, shortedURL: String, qr: String)
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
    private val sseRepository: SseRepository,
    val createShortUrlUseCase: CreateShortUrlUseCase)
    :MasiveUrlShortenerController{

    @GetMapping
    fun index(model: Model): ModelAndView {
        model["uuid"] = UUID.randomUUID().toString()
        val modelAndView = ModelAndView()
        modelAndView.viewName = "index.html"
        System.out.println("Pase por aqui")
        return modelAndView
    }

    @PostMapping("/api/upload")
    override fun handleFileUpload(@RequestParam("uuid") id: String, file: MultipartFile, request: HttpServletRequest, listener: ProgressListener): ResponseEntity<String> {
        val listener = sseRepository.createProgressListener(id)
        System.out.println("Pase por aqui2")
        val sseEmitter = SseEmitter(Long.MAX_VALUE)
        sseRepository.put(id, sseEmitter)
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

                listener.onProgress(origin, uri, qr)
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