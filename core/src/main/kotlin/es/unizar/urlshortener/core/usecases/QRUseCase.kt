package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.QRFromUrl
import es.unizar.urlshortener.core.QRProperties

/**
 * Given an url hash returns the key that is used to create a qrL.
 *
 * **Note**: This is an example of functionality.
 */
interface QRUseCase {
    fun create(hash: String, data: QRProperties): QRFromUrl
}

class QRUseCaseImpl : QRUseCase {
    override fun create(hash: String, data: QRProperties): QRFromUrl {
        TODO("Not yet implemented")
    }

}