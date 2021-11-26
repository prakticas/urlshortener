package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*


/**
 * Given an url hash returns the key that is used to create a qrL.
 *
 * **Note**: This is an example of functionality.
 */
interface QRUseCase {
    fun create(url: ShortUrl): QRFromUrl
}

class QRUseCaseImpl(private val qrRepositoryService: QRRepositoryService,
                    private val qrService: QRService) : QRUseCase {

    private fun createQR(url: ShortUrl, data: QRProperties = QRProperties()): ByteArray {
        return qrService.createQR(url, data).qr
    }

    override fun create(url: ShortUrl): QRFromUrl {
        return qrRepositoryService.findByKey(url.hash)
            ?: qrRepositoryService.save(QRFromUrl(url, createQR(url)))
    }

}