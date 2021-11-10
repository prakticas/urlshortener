package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.*
import es.unizar.urlshortener.core.usecases.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
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
    fun redirectTo(id: String, request: HttpServletRequest): ResponseEntity<ByteArray>

}
@RestController
class QRControllerImpl(
    val qrUseCase: QRUseCase,
    val redirectUseCase: RedirectUseCase,
    val logClickUseCase: LogClickUseCase
): QRController {
    @GetMapping("/qr/{id:.*}",  produces = [MediaType.IMAGE_PNG_VALUE])
    override fun redirectTo(
        @PathVariable id: String,
        request: HttpServletRequest
    ): ResponseEntity<ByteArray> =
        redirectUseCase.redirectTo(id).let {
            logClickUseCase.logClick(id, ClickProperties(ip = request.remoteAddr))
            val qrFromUrl = qrUseCase.create(it)
            val h = HttpHeaders()
            val response = qrFromUrl.qr
            return ResponseEntity(response, h, HttpStatus.CREATED)
        }
    }



