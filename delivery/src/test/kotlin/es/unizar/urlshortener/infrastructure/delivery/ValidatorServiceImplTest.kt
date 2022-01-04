package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.UrlError
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.TestPropertySource

@Configuration
internal class ValidatorServiceImplConfig {


    @Bean
    fun testValidatorServiceImpl(): ValidatorServiceImpl = ValidatorServiceImpl()
}


@SpringBootTest(classes = [ValidatorServiceImplConfig::class])
internal class ValidatorServiceImplTest{
    @Autowired private lateinit var testValidatorServiceImpl: ValidatorServiceImpl



    @Test
    fun `Url is not  correct`(){
        val expected = UrlError.INCORRECT_URL
        val actual = testValidatorServiceImpl.isValid("ftp://example.com/")
        assertEquals(expected,actual)
    }

    @Test
    @Disabled
    fun `Url is not  available`(){
        val expected = UrlError.NOT_AVAILABLE
        val actual = testValidatorServiceImpl.isValid("ftp://example.com/")
        assertEquals(expected,actual)

    }


    @Test
    fun `Url is not secure`(){
        val expected = UrlError.NOT_SECURE
        val actual = testValidatorServiceImpl.isValid("https://testsafebrowsing.appspot.com/s/phishing.html")
        assertEquals(expected,actual)

    }

    @Test
    fun `Url has the correct format and is either available and secure`(){
        val expected = UrlError.NO_ERROR
        val actual = testValidatorServiceImpl.isValid("https://www.google.com/")
        assertEquals(expected,actual)

    }


}