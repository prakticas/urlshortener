package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.*
import es.unizar.urlshortener.core.usecases.*
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.URI
import javax.servlet.http.HttpServletRequest

/**
 * The specification of the controller.
 */
interface UrlShortenerController {

    /**
     * Redirects and logs a short url identified by its [id].
     *
     * **Note**: Delivery of use cases [RedirectUseCase] and [LogClickUseCase].
     */
    fun redirectTo(id: String, request: HttpServletRequest): ResponseEntity<Void>

    /**
     * Creates a short url from details provided in [data].
     *
     * **Note**: Delivery of use case [CreateShortUrlUseCase].
     */
    fun shortener(data: ShortUrlDataIn, request: HttpServletRequest): ResponseEntity<ShortUrlDataOut>

}

/**
 * Data required to create a short url.
 */
data class ShortUrlDataIn(
    val url: String,
    val sponsor: String? = null,
    val withQR: Boolean?
)

/**
 * Data returned after the creation of a short url.
 */
data class ShortUrlDataOut(
    val url: URI? = null,
    val properties: Map<String, Any> = emptyMap(),
)


/**
 * The implementation of the controller.
 *
 * **Note**: Spring Boot is able to discover this [RestController] without further configuration.
 */
@RestController
class UrlShortenerControllerImpl(
    val redirectUseCase: RedirectUseCase,
    val logClickUseCase: LogClickUseCase,
    val createShortUrlUseCase: CreateShortUrlUseCase,
) : UrlShortenerController {

    @GetMapping("/tiny-{id:.*}")
    override fun redirectTo(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<Void> =
        redirectUseCase.redirectTo(id).let {
            logClickUseCase.logClick(id, ClickProperties(ip = request.remoteAddr))
            val h = HttpHeaders()
            h.location = URI.create(it.redirection.target)
            ResponseEntity<Void>(h, HttpStatus.valueOf(it.redirection.mode))
        }

    @PostMapping("/api/link", consumes = [ MediaType.APPLICATION_FORM_URLENCODED_VALUE ])
    override fun shortener(data: ShortUrlDataIn, request: HttpServletRequest): ResponseEntity<ShortUrlDataOut> =
        //guardar la ip en la base de datos
        createShortUrlUseCase.create(
            url = data.url,
            data = ShortUrlProperties(
                ip = request.remoteAddr,
                sponsor = data.sponsor
            )
            //lo devuelvo por create es usado por la word 'it'
        ).let {
            val h = HttpHeaders()
            val url: URI = if (data.withQR == true){
                linkTo<QRControllerImpl> { redirectTo( it.hash, request) }.toUri()
            } else {
                linkTo<UrlShortenerControllerImpl> { redirectTo(it.hash, request) }.toUri()
            }
            h.location = url
            val response = ShortUrlDataOut(
                url = url,
                properties = mapOf(
                    "safe" to it.properties.safe
                )
            )
            ResponseEntity<ShortUrlDataOut>(response, h, HttpStatus.CREATED)
        }

    @PostMapping("/api/upload")
    fun handleFileUpload(@RequestParam("file") file: MultipartFile,request: HttpServletRequest): ResponseEntity<Resource> {


        //guardar la ip en la base de datos
        val csvProfile = CreateShortUrlFromCsvUseCase.create(
            file = file,
            data = ShortUrlProperties(
                ip = request.remoteAddr,
                sponsor = null
            ))

        val h = HttpHeaders()
        //obetner la uri comrpimida de la uri inicial
        val url = linkTo<UrlShortenerControllerImpl> { redirectTo(csvProfile.firstUri, request) }.toUri()
        h.location = url
        h.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=uriFile.csv")
        h.set(HttpHeaders.CONTENT_TYPE, "text/csv")
        return ResponseEntity(
            csvProfile.file,
            h,
            HttpStatus.CREATED
        )



    }

    }



