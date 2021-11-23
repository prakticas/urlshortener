package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.UrlError
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class ValidatorServiceImplTest{
    private val testValidatorServiceImpl:ValidatorServiceImpl= ValidatorServiceImpl()


    @Test
    fun `Url is not  correct`(){
        val expected = UrlError.INCORRECT_URL
        val actual = testValidatorServiceImpl.isValid("ftp://example.com/")
        assertEquals(expected,actual)
    }

    @Test
    @Disabled
    fun `Url is not  available`(){
    assertEquals(1,1)
    }


    @Test
    @Disabled
    fun `Url is not secure`(){

    }

    @Test
    @Disabled
    fun `Url has the correct format and is either available and secure`(){

    }


}