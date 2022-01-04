package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.*
import es.unizar.urlshortener.core.usecases.*
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
    fun shortener(
        data: ShortUrlDataIn,
        request: HttpServletRequest
    ): ResponseEntity<ShortUrlDataOut>

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
    val qr: URI? = null,
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
    val createQRUseCase: QRUseCase
) : UrlShortenerController {

    @Autowired
    private val template: RabbitTemplate? = null

    @Autowired
    private val exchange: TopicExchange? = null

    @GetMapping("/tiny-{id:.*}")
    override fun redirectTo(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<Void> =
        redirectUseCase.redirectTo(id).let {
            //Encolar tarea generación QRCODE usando RabbitMQ

            logClickUseCase.logClick(id, ClickProperties(ip = request.remoteAddr))
            val h = HttpHeaders()
            h.location = URI.create(it.redirection.target)
            ResponseEntity<Void>(h, HttpStatus.valueOf(it.redirection.mode))
        }

    @PostMapping("/api/link", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    override fun shortener(
        data: ShortUrlDataIn,
        request: HttpServletRequest
    ): ResponseEntity<ShortUrlDataOut> =
        //guardar la ip en la base de datos
        createShortUrlUseCase.create(
            url = data.url,
            data = ShortUrlProperties(
                ip = request.remoteAddr,
                sponsor = data.sponsor
            )
            //lo devuelvo por create es usado por la word 'it'
        ).let {

            val rpc_reply =  template!!.convertSendAndReceive(exchange!!.name, "rpc", "Hector")  as Boolean
            val h = HttpHeaders()
            val url = linkTo<UrlShortenerControllerImpl> { redirectTo(it.hash, request) }.toUri()
            var qr: URI? = null
            h.location = url
            if (data.withQR == true) {
                createQRUseCase.createQRAsync(it)
                qr = linkTo<QRControllerImpl> { redirectTo(it.hash, request) }.toUri()
                h.location = qr
            }
            val response = ShortUrlDataOut(
                url = url,
                qr = qr,
                properties = mapOf(
                    "safe" to it.properties.safe
                )
            )
            ResponseEntity<ShortUrlDataOut>(response, h, HttpStatus.CREATED)
        }
}







