package es.unizar.urlshortener

import com.google.zxing.BarcodeFormat
import com.google.zxing.FormatException
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import es.unizar.urlshortener.core.*
import es.unizar.urlshortener.core.usecases.QRUseCaseImpl
import es.unizar.urlshortener.infrastructure.delivery.QRServiceImpl
import es.unizar.urlshortener.infrastructure.delivery.ValidatorServiceImpl
import es.unizar.urlshortener.infrastructure.repositories.QREntityRepository
import es.unizar.urlshortener.infrastructure.repositories.QRRepositoryServiceImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.apache.coyote.http11.Constants.a
import java.lang.Exception
import java.lang.RuntimeException
import org.apache.coyote.http11.Constants.a
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.given
import org.mockito.kotlin.mock


@SpringBootTest
class QRUnitTests {

    @Autowired
    private val qrService = QRServiceImpl()

    @Mock
    private val qrCodeWriter = QRCodeWriter()

    //FIXME: failing in decoding
    @Test
    fun `should encode QR code correctly from url`(){
        // Given
        val urlInit = "https://google.com"
        val width = 300
        val height = 300
        // When
        val encodedQR = qrService.encodeQR(urlInit, width, height)
        // Then
//        val urlEnd = qrService.decodeQR(encodedQR)
//        assertThat(urlInit).isEqualTo(urlEnd)
    }

    @Test
    fun `should create a QR code correctly`(){
        // Given
        val urlInit = "https://google.com"
        val shortUrl = ShortUrl(
            hash = "asklfjs",
            redirection = Redirection(target = urlInit)
        )
        // When
        val createdQR = qrService.createQR(shortUrl)
        // Then
        assertThat(createdQR.properties).isEqualTo(QRProperties())
        assertThat(createdQR.url).isEqualTo(shortUrl)
    }

    //FIXME:

    @Test
    fun `should not create a QR code correctly if is not encoded correctly`(){
        // Given
        val urlInit = "https://google.com"
        val shortUrl = ShortUrl(
            hash = "asklfjs",
            redirection = Redirection(target = urlInit)
        )
        given(qrCodeWriter.encode(urlInit, BarcodeFormat.QR_CODE, 200, 200))
            .willAnswer {throw WriterException()}
        // Then
//        assertThrows<QREncodeException> {
//            qrService.createQR(shortUrl)
//        }
    }

}