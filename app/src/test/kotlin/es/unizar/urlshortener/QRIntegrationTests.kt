package es.unizar.urlshortener

import org.apache.http.impl.client.HttpClientBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.jdbc.JdbcTestUtils

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QRIntegrationTests {

    @LocalServerPort
    private val port = 0

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @BeforeEach
    fun setup() {

    }

    @AfterEach
    fun tearDowns() {

    }

    @Test
    fun `redirectTo should return a redirect when url hash exists in creating qr`(){

    }

    @Test
    fun `redirectTo should return a not found when url hash doesn't exist`(){

    }

    @Test
    fun `redirectTo should return an internal server error when other error occurred`(){

    }

    @Test
    fun `should return an internal server error when encoding a url in a qr fails`(){

    }


}