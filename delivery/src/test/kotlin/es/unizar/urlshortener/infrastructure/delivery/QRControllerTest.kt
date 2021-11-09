package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.*
import es.unizar.urlshortener.core.usecases.CreateShortUrlUseCase
import es.unizar.urlshortener.core.usecases.LogClickUseCase
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
//    UrlShortenerControllerImpl::class,
    RestResponseEntityExceptionHandler::class])
class QRControllerTest {

//    @Autowired
//    private lateinit var mockMvc: MockMvc
//
//    @MockBean
//    private lateinit var redirectUseCase: RedirectUseCase
//
//    @MockBean
//    private lateinit var logClickUseCase: LogClickUseCase
//
//    //@MockBean
//    //private lateinit var createQRUseCase: CreateQRUseCase
//
//    @Test
//    fun `redirectTo returns a redirect when the key exists`() {
//        //TODO: unimplemented method
//    }
//
//    @Test
//    fun `redirectTo returns a not found when the key does not exist`() {
//        //TODO: unimplemented method
//    }
//
//    @Test
//    fun `creates returns a basic redirect if it can compute a hash`() {
//        //TODO: unimplemented method
//    }
//
//    @Test
//    fun `creates returns bad request if it can compute a hash`() {
//        //TODO: unimplemented method
//    }
}