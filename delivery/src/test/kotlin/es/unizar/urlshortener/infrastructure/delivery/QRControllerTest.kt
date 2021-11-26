package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.*
import es.unizar.urlshortener.core.usecases.CreateShortUrlUseCase
import es.unizar.urlshortener.core.usecases.LogClickUseCase
import es.unizar.urlshortener.core.usecases.QRUseCase
import es.unizar.urlshortener.core.usecases.RedirectUseCase
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.never
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
@ContextConfiguration(classes = [
    QRControllerImpl::class,
    UrlShortenerControllerImpl::class,
    RestResponseEntityExceptionHandler::class])
class QRControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var redirectUseCase: RedirectUseCase

    @MockBean
    private lateinit var logClickUseCase: LogClickUseCase

    @MockBean
    private lateinit var qrUseCase: QRUseCase

    @MockBean
    private lateinit var createShortUrlUseCase: CreateShortUrlUseCase

    @Test
    fun `should return a redirect when the key exists`() {
        val url = "https://google.com"
        val shortUrl = ShortUrl(hash = "ud73hd74fg", redirection = Redirection(url))
        given(redirectUseCase.redirectTo("key")).willReturn(shortUrl)
        given(qrUseCase.getQR(shortUrl)).willReturn(QRFromUrl(shortUrl, ByteArray(2)))
        mockMvc.perform(get("/qr/{id}", "key")
            .contentType(MediaType.IMAGE_PNG_VALUE))
            .andExpect(status().isTemporaryRedirect)
            .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))

        verify(logClickUseCase).logClick("key", ClickProperties(ip = "127.0.0.1"))
    }

    @Test
    fun `should return an internal server error when create qr fails`() {
        val url = "https://google.com"
        val shortUrl = ShortUrl(hash = "", redirection = Redirection(url))
        given(redirectUseCase.redirectTo("key")).willReturn(shortUrl)
        given(qrUseCase.getQR(shortUrl)).willAnswer{throw QREncodeException(url)}
        mockMvc.perform(get("/qr/{id}", "key"))
            .andExpect(status().is5xxServerError)
            .andExpect(jsonPath("$.message").value(QREncodeException(url).message))

        verify(logClickUseCase).logClick("key", ClickProperties(ip = "127.0.0.1"))
    }

    @Test
    fun `should return a not found when the shortUrl key does not exist`() {
        given(redirectUseCase.redirectTo("key"))
            .willAnswer { throw RedirectionNotFound("key") }

        mockMvc.perform(get("/qr/{id}", "key"))
            .andDo(print())
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.statusCode").value(404))

        verify(logClickUseCase, never()).logClick("key", ClickProperties(ip = "127.0.0.1"))
    }

    @Test
    fun `creates returns a basic redirect if it can compute a hash`() {
        val url = "http://example.com/"
        val hash = "f684a3c4"
        given(createShortUrlUseCase.create(
            url = url,
            data = ShortUrlProperties(ip = "127.0.0.1")
        )).willReturn(ShortUrl(hash, Redirection(url)))

        mockMvc.perform(post("/api/link")
            .param("url", url)
            .param("withQR", true.toString())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(redirectedUrl("http://localhost/qr/$hash"))
            .andExpect(jsonPath("$.url").value("http://localhost/tiny-$hash"))
            .andExpect(jsonPath("$.qr").value("http://localhost/qr/$hash"))
    }
}