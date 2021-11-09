package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.*
import es.unizar.urlshortener.core.usecases.*
import org.springframework.hateoas.server.mvc.linkTo
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
    fun redirectTo(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<ByteArray>

@RestController
class QRControllerImpl(
    val qrUseCase: QRUseCase,
    val qrRedirectUseCase: QRRedirectUseCase
): QRController {
    @GetMapping("/qr/{id:.*}", produces = [MediaType.IMAGE_PNG_VALUE])
    override fun redirectTo(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<ByteArray> = qrRedirectUseCase.redirectTo(id).let {
        val h = HttpHeaders()
        val response =
            qrUseCase.createQR(ShortUrl(hash = id, redirection = Redirection(target = it.target)))
        return ResponseEntity(response, h, HttpStatus.CREATED)
    }
}
}

