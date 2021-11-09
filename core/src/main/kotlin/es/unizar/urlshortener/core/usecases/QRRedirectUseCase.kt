package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.QRRepositoryService
import es.unizar.urlshortener.core.Redirection
import es.unizar.urlshortener.core.RedirectionNotFound
import es.unizar.urlshortener.core.ShortUrlRepositoryService

/**
 * Given a key returns a [Redirection] that contains a [URI target][Redirection.target]
 * and an [HTTP redirection mode][Redirection.mode].
 *
 * **Note**: This is an example of functionality.
 */
interface QRRedirectUseCase {
    fun redirectTo(key: String): Redirection
}

/**
 * Implementation of [RedirectUseCase].
 */
class QRRedirectUseCaseImpl(
    private val qrRepositoryService: QRRepositoryService
) : QRRedirectUseCase {
    override fun redirectTo(key: String) = qrRepositoryService
        .findByKey(key)
        ?.url?.redirection
        ?: throw RedirectionNotFound(key)
}

