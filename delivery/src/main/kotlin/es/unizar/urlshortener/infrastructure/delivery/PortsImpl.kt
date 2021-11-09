package es.unizar.urlshortener.infrastructure.delivery

import com.google.common.hash.Hashing
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.Decoder
import es.unizar.urlshortener.core.*
import org.apache.commons.validator.routines.UrlValidator
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

/**
 * Implementation of the port [ValidatorService].
 */
class ValidatorServiceImpl : ValidatorService {
    override fun isValid(url: String) = urlValidator.isValid(url)

    companion object {
        val urlValidator = UrlValidator(arrayOf("http", "https"))
    }
}

/**
 * Implementation of the port [HashService].
 */
@Suppress("UnstableApiUsage")
class HashServiceImpl : HashService {
    override fun hasUrl(url: String) = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString()
}

class QRServiceImpl: QRService {
    override fun createQR(url: ShortUrl, data: QRProperties): QRFromUrl {
        val qrByteArray = QRToByteArray(encodeQR(url.redirection.target, data.width, data.height))
        return QRFromUrl(
            url = url,
            qr = qrByteArray,
            properties = data
        );
    }

    override fun encodeQR(url: String, width: Int, height: Int): BitMatrix {
        try {
            val qrCodeWriter = QRCodeWriter()
            return qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, height, width)
        } catch (e: Exception){
            throw QREncodeException(url)
        }

    }

    override fun decodeQR(qrBitMatrix: BitMatrix): String {
            val decoder = Decoder()
            return decoder.decode(qrBitMatrix).text;
    }

    override fun QRToByteArray(bitMatrix: BitMatrix): ByteArray{
        val qrByteArray = ByteArrayOutputStream()
        MatrixToImageWriter.writeToStream(bitMatrix, "png", qrByteArray);
        return qrByteArray.toByteArray()
    }

}