package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.ShortUrl
import es.unizar.urlshortener.core.usecases.CreateShortUrlUseCase
import es.unizar.urlshortener.core.usecases.LogClickUseCase
import es.unizar.urlshortener.core.usecases.RedirectUseCase
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.awt.image.BufferedImage
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
    fun redirectTo(id: String, request: HttpServletRequest): ResponseEntity<Void>

    /**
     * Creates a qr from url from details provided in [data].
     *
     * **Note**: Delivery of use case [CreateShortUrlUseCase].
     */
    fun qrGenerator(data: QRDataIn, request: HttpServletRequest): ResponseEntity<QRDataOut>

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

@RestController
class QRControllerImpl(
    val redirectUseCase: RedirectUseCase,
    val logClickUseCase: LogClickUseCase,
    //val qrUseCase: QRUseCase,
): QRController {
    @GetMapping("/qr/{id:.*}")
    override fun redirectTo(id: String, request: HttpServletRequest): ResponseEntity<Void> {
        TODO("Not yet implemented")
    }

    @PostMapping("/qr", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    override fun qrGenerator(
        data: QRDataIn,
        request: HttpServletRequest
    ): ResponseEntity<QRDataOut> {
        TODO("Not yet implemented")
    }

}