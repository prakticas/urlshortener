package es.unizar.urlshortener.infrastructure.delivery

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.hash.Hashing
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.Decoder
import es.unizar.urlshortener.core.*
import org.apache.commons.validator.routines.UrlValidator
import java.io.ByteArrayOutputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component


/**
 * Implementation of the port [ValidatorService].
 */

@Component
class ExternalData{
    @Value("\${APIKEY}")
    lateinit var  apiKey :String
    fun apiKey():String= apiKey



}


class ValidatorServiceImpl(
    private val externalData:ExternalData
) : ValidatorService {



    private fun checkSafety(url: String):Boolean{

        val urlAsked = threatInfoURL(url=url)
        val body = threatInfo( threatEntries = arrayOf(urlAsked))
        val threatInfoRqt = Gson().toJson(body)
        val requestBody = "{threatInfo: $threatInfoRqt}"

        val ak = externalData.apiKey()
        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://safebrowsing.googleapis.com/v4/threatMatches:find?key=${externalData.apiKey()}"))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body().toString()=="{}\n"
    }

    companion object {
        val urlValidator = UrlValidator(arrayOf("http", "https"))
    }
    override fun isValid(url: String) : UrlError{
        //checks the format
        if(!urlValidator.isValid(url))
            return UrlError.INCORRECT_URL

        //checks availability

        //checks if it is safe
       if ( !checkSafety(url))
           return UrlError.NOT_SECURE



        //no errors found
        return UrlError.NO_ERROR
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
        )
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
            return decoder.decode(qrBitMatrix).text
    }

    override fun QRToByteArray(bitMatrix: BitMatrix): ByteArray{
        val qrByteArray = ByteArrayOutputStream()
        MatrixToImageWriter.writeToStream(bitMatrix, "png", qrByteArray)
        return qrByteArray.toByteArray()
    }

}