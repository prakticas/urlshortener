package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.UrlError
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource

@TestPropertySource
internal class ValidatorServiceImplTest{
    @Autowired val externalData = ExternalData()
    private val testValidatorServiceImpl:ValidatorServiceImpl= ValidatorServiceImpl(externalData)


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
    @Disabled
    fun `Url is not secure`(){
        val expected = UrlError.NOT_SECURE
        val actual = testValidatorServiceImpl.isValid("ftp://example.com/")
        assertEquals(expected,actual)

    }

    @Test
    fun `Url has the correct format and is either available and secure`(){
        val expected = UrlError.NO_ERROR
        val actual = testValidatorServiceImpl.isValid("https://www.google.com/")
        assertEquals(expected,actual)

    }


}