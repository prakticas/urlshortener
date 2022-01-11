package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import java.util.Date
import java.util.concurrent.CompletableFuture

/**
 * Given an url returns the key that is used to create a short URL.
 * When the url is created optional data may be added.
 *
 * **Note**: This is an example of functionality.
 */
interface CreateShortUrlUseCase {
    fun create(url: String, data: ShortUrlProperties): ShortUrl
    fun createWithError(url: String, data: ShortUrlProperties): ShortUrlWithError
}

/**
 * Implementation of [CreateShortUrlUseCase].
 */
class CreateShortUrlUseCaseImpl(
    private val shortUrlRepository: ShortUrlRepositoryService,
    private val validatorService: ValidatorService,
    private val hashService: HashService
) : CreateShortUrlUseCase {


    override fun create(url: String, data: ShortUrlProperties): ShortUrl =
        if (validatorService.isValid(url)==UrlError.NO_ERROR) {
            val id: String = hashService.hasUrl(url)
            val su = ShortUrl(
                hash = id,
                redirection = Redirection(target = url),
                properties = ShortUrlProperties(
                    safe = data.safe,
                    ip = data.ip,
                    sponsor = data.sponsor,
                    hasQR= data.hasQR
                )
            )
            shortUrlRepository.save(su)
        } else {
            throw InvalidUrlException(url)
        }

    override fun createWithError(url: String, data: ShortUrlProperties): ShortUrlWithError {
        val err = validatorService.isValid(url)

        if (err == UrlError.NO_ERROR) {
            val id: String = hashService.hasUrl(url)
            val su = ShortUrl(
                hash = id,
                redirection = Redirection(target = url),
                properties = ShortUrlProperties(
                    safe = data.safe,
                    ip = data.ip,
                    sponsor = data.sponsor,
                    hasQR = data.hasQR
                )
            )

            return ShortUrlWithError(
                url = shortUrlRepository.save(su)
            )


        } else {
            return  ShortUrlWithError(
                origin = url,
                error = err
            )
        }
    }
}
