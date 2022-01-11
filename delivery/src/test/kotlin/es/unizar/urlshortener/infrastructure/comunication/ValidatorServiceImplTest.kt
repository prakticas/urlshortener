package es.unizar.urlshortener.infrastructure.comunication

import es.unizar.urlshortener.core.UrlError
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class ValidatorServiceImplConfig {


    @Bean
    fun ExternalDataImpl(): ExternalData = ExternalData()


}


@SpringBootTest(classes = [ValidatorServiceImplConfig::class])
internal class ValidatorServiceImplTest{
    @Autowired private lateinit var ExternalDataImpl: ExternalData



    @Test
    @Disabled
    fun `Url is not  correct`(){
        val receiver = Receiver(ExternalDataImpl)
        val expected = UrlError.INCORRECT_URL
        val actual = receiver.receiveMessage("ftp://example.com/")
        assertEquals(expected,actual)
    }

    @Test
    @Disabled
    fun `Url is not  available`(){
        val receiver = Receiver(ExternalDataImpl)
        val expected = UrlError.NOT_AVAILABLE
        val actual = receiver.receiveMessage("http://www.ibetthispagedoesnotexist.com/")
        assertEquals(expected,actual)
    }


    @Test
    @Disabled
    fun `Url is not secure`(){
        val receiver = Receiver(ExternalDataImpl)
        val expected = UrlError.NOT_SECURE
        val actual = receiver.receiveMessage("https://testsafebrowsing.appspot.com/s/phishing.html")
        assertEquals(expected,actual)

    }

    @Test
    @Disabled
    fun `Url has the correct format and is either available and secure`(){
        val receiver = Receiver(ExternalDataImpl)
        val expected = UrlError.NO_ERROR
        val actual = receiver.receiveMessage("https://www.google.com/")
        assertEquals(expected,actual)

    }


}