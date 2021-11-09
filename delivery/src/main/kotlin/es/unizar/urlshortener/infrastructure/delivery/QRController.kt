package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.QRProperties
import es.unizar.urlshortener.core.Redirection
import es.unizar.urlshortener.core.ShortUrl
import es.unizar.urlshortener.core.ShortUrlProperties
import es.unizar.urlshortener.core.usecases.CreateShortUrlUseCase
import es.unizar.urlshortener.core.usecases.LogClickUseCase
import es.unizar.urlshortener.core.usecases.QRUseCase
import es.unizar.urlshortener.core.usecases.RedirectUseCase
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.awt.image.BufferedImage
import java.net.URI
import javax.servlet.http.HttpServletRequest

/**
 * The specification of the controller.
 */
interface QRController {

    /**
     * Redirects and logs a qr from url identified by its [id].
     *
     * **Note**: Delivery of use cases [RedirectUseCase] and [LogClickUseCase].
     */
    fun redirectTo(request: HttpServletRequest): ResponseEntity<ByteArray>

    /**
     * Creates a qr from url from details provided in [data].
     *
     * **Note**: Delivery of use case [CreateShortUrlUseCase].
     */
    fun qrGenerator(data: QRDataIn, request: HttpServletRequest): ResponseEntity<QRURIOut>
}

/**
 * Data required to create a qr from url.
 */
data class QRDataIn(
    val hash: String
)

/**
 * Data returned after the creation of a qr from url.
 */
data class QRDataOut(
    val qr: BufferedImage,
)

/**
 *
 */
data class QRURIOut(
    val url:URI? = null,
    val properties: Map<String, Any> = emptyMap()
)

@RestController
class QRControllerImpl(
    val qrUseCase: QRUseCase,
): QRController {
    @GetMapping("/qr", produces = [MediaType.IMAGE_PNG_VALUE])
    override fun redirectTo(
        request: HttpServletRequest
    ): ResponseEntity<ByteArray> {
        //TODO:
        val h = HttpHeaders()
        val response = qrUseCase.create(ShortUrl(hash = "kldwfhsuikdf", redirection = Redirection(target = "https://google.com")))
        return ResponseEntity(response, h, HttpStatus.CREATED)
    }

    @PostMapping("/qr", consumes = [MediaType.APPLICATION_JSON_VALUE])
    override fun qrGenerator(
        data: QRDataIn,
        request: HttpServletRequest
    ): ResponseEntity<QRURIOut> {
        TODO("Not yet implemented")
    }
}

