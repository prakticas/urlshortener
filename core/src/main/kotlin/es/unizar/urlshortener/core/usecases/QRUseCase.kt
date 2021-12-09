package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import org.springframework.scheduling.annotation.Async
/**
 * Given an url hash returns the key that is used to create a qrL.
 *
 * **Note**: This is an example of functionality.
 */
interface QRUseCase {
    fun getQR(url: ShortUrl): QRFromUrl
    @Async("QRExecutor")
    fun createQRAsync(url: ShortUrl, data: QRProperties = QRProperties())
}
class QRUseCaseImpl(private val qrRepositoryService: QRRepositoryService,
                    private val qrService: QRService) : QRUseCase {

    private fun createQR(url: ShortUrl, data: QRProperties = QRProperties()): ByteArray {
        return qrService.createQR(url, data).qr
    }
    override fun createQRAsync(url: ShortUrl, data: QRProperties) {
        qrRepositoryService.save(QRFromUrl(url, createQR(url)))
    }

    override fun getQR(url: ShortUrl): QRFromUrl {
        return qrRepositoryService.findByKey(url.hash)
            ?: qrRepositoryService.save(QRFromUrl(url, createQR(url)))
    }

}