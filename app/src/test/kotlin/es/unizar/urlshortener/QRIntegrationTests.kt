package es.unizar.urlshortener

import es.unizar.urlshortener.infrastructure.delivery.QRServiceImpl
import es.unizar.urlshortener.infrastructure.delivery.ShortUrlDataOut
import org.apache.http.impl.client.HttpClientBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.test.jdbc.JdbcTestUtils
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.util.concurrent.TimeUnit


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QRIntegrationTests {

    @LocalServerPort
    private val port = 0

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private val pool: ThreadPoolTaskExecutor? = null

    @Autowired
    private val qrServiceImpl = QRServiceImpl()

    @BeforeEach
    fun setup() {
        val httpClient = HttpClientBuilder.create()
            .disableRedirectHandling()
            .build()
        (restTemplate.restTemplate.requestFactory as HttpComponentsClientHttpRequestFactory).httpClient = httpClient

        JdbcTestUtils.deleteFromTables(jdbcTemplate, "QR", "click")
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "shorturl", "click")
    }

    @AfterEach
    fun tearDowns() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "QR", "click")
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "shorturl", "click")
    }

    @Test
    fun `redirectTo should return a redirect when url hash exists in creating qr`(){
        // Given
        val target = qrFromUrlCall("https://google.com", true)
        val url = target.headers.location
        assertTrue(url.toString().matches(Regex("http://.*/qr/[a-z0-9]+")))

        val awaitTermination = pool?.threadPoolExecutor?.awaitTermination(1, TimeUnit.SECONDS);
        assertThat(awaitTermination).isFalse
        // When
        val response = restTemplate.getForEntity(url, ByteArray::class.java)
        // Then
        assertThat(response.headers.contentType.toString()).isEqualTo(MediaType.IMAGE_PNG_VALUE)
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isEqualTo(qrServiceImpl.QRToByteArray(qrServiceImpl.encodeQR("https://google.com", 200, 200)))
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "click")).isEqualTo(1)
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "QR")).isEqualTo(1)
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "shorturl")).isEqualTo(1)
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

    private fun qrFromUrlCall(url: String, withQR: Boolean): ResponseEntity<ShortUrlDataOut> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val data: MultiValueMap<String, Any> = LinkedMultiValueMap()
        data["url"] = url
        data["withQR"] = withQR

        return restTemplate.postForEntity(
            "http://localhost:$port/api/link",
            HttpEntity(data, headers), ShortUrlDataOut::class.java
        )
    }


}