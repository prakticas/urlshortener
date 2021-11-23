package es.unizar.urlshortener.infrastructure.delivery

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class ValidatorServiceImplTest{
    private val testValidatorServiceImpl:ValidatorServiceImpl= ValidatorServiceImpl()


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
    fun `Url is available and secure`(){

    }


}