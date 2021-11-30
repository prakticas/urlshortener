package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.InvalidUrlException
import es.unizar.urlshortener.core.Redirection
import es.unizar.urlshortener.core.ShortUrl
import es.unizar.urlshortener.core.ShortUrlProperties
import es.unizar.urlshortener.core.usecases.CreateShortUrlUseCase
import es.unizar.urlshortener.core.usecases.LogClickUseCase
import es.unizar.urlshortener.core.usecases.RedirectUseCase
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest
@ContextConfiguration(classes = [
    MasiveUrlShortenerControllerImpl::class,
    RestResponseEntityExceptionHandler::class])
class MasiveUrlShortenerControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var redirectUseCase: RedirectUseCase

    @MockBean
    private lateinit var logClickUseCase: LogClickUseCase

    @MockBean
    private lateinit var createShortUrlUseCase: CreateShortUrlUseCase



    @Test
    fun `handleFileUpload returns a file if the file given has a correct structure`() {
        given(
            createShortUrlUseCase.create(
                url = "http://example.com/",
                data = ShortUrlProperties(ip = "127.0.0.1", hasQR = true)
            )
        ).willReturn(ShortUrl("f684a3c4", Redirection("http://example.com/"),properties = ShortUrlProperties(ip= "127.0.0.1", hasQR = true)))
        val file = MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "http://example.com/,y".toByteArray())
        val response = "http://example.com/,http://localhost/tiny-f684a3c4,http://localhost/qr/f684a3c4\n"
        mockMvc.perform(
            multipart("/api/upload")
                .file(file))
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(content().string(response))



    }



}
