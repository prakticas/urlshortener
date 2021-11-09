package es.unizar.urlshortener.core.usecases

import com.google.zxing.client.j2se.MatrixToImageWriter

import com.google.zxing.BarcodeFormat

import com.google.zxing.common.BitMatrix

import com.google.zxing.qrcode.QRCodeWriter
import es.unizar.urlshortener.core.*
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream


/**
 * Given an url hash returns the key that is used to create a qrL.
 *
 * **Note**: This is an example of functionality.
 */
interface QRUseCase {
    fun createQR(url: ShortUrl, data: QRProperties = QRProperties()): ByteArray
    fun create(url: QRFromUrl): QRFromUrl
}

class QRUseCaseImpl(private val qrRepositoryService: QRRepositoryService,
                    private val qrService: QRService,
                    private val validatorService: ValidatorService) : QRUseCase {

    override fun createQR(url: ShortUrl, data: QRProperties): ByteArray {
        val urlName = url.redirection.target
        if(!validatorService.isValid(urlName)) throw InvalidUrlException(urlName)
        return qrService.createQR(url, data).qr
    }

    override fun create(url: QRFromUrl): QRFromUrl{
        return qrRepositoryService.save(url)
    }

}