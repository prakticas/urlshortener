package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.QRFromUrl
import es.unizar.urlshortener.core.QRProperties
import com.google.zxing.client.j2se.MatrixToImageWriter

import com.google.zxing.BarcodeFormat

import com.google.zxing.common.BitMatrix

import com.google.zxing.qrcode.QRCodeWriter
import es.unizar.urlshortener.core.ShortUrl
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream


/**
 * Given an url hash returns the key that is used to create a qrL.
 *
 * **Note**: This is an example of functionality.
 */
interface QRUseCase {
    fun create(hash: String, data: QRProperties = QRProperties()): ByteArray
}

class QRUseCaseImpl : QRUseCase {
    override fun create(hash: String, data: QRProperties): ByteArray {
        //TODO:
        print("Hola")
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode("https://google.com", BarcodeFormat.QR_CODE, 250, 250)
        val bytearrayout: ByteArrayOutputStream = ByteArrayOutputStream()

        MatrixToImageWriter.writeToStream(bitMatrix, "png", bytearrayout);
        return bytearrayout.toByteArray()
    }

}