package es.unizar.urlshortener.infrastructure.repositories

import es.unizar.urlshortener.core.*

/**
 * Implementation of the port [ClickRepositoryService].
 */
class ClickRepositoryServiceImpl(
    private val clickEntityRepository: ClickEntityRepository
) : ClickRepositoryService {
    override fun save(cl: Click): Click = clickEntityRepository.save(cl.toEntity()).toDomain()
}

/**
 * Implementation of the port [ShortUrlRepositoryService].
 */
class ShortUrlRepositoryServiceImpl(
    private val shortUrlEntityRepository: ShortUrlEntityRepository
) : ShortUrlRepositoryService {
    override fun findByKey(id: String): ShortUrl? = shortUrlEntityRepository.findByHash(id)?.toDomain()

    override fun save(su: ShortUrl): ShortUrl = shortUrlEntityRepository.save(su.toEntity()).toDomain()
}

/**
 * Implementation of the port [QRRepositoryServiceImpl].
 */
class QRRepositoryServiceImpl(
    private val qrEntityRepository: QREntityRepository
) : QRRepositoryService {
    override fun findByKey(id: String): QRFromUrl? = qrEntityRepository.findByHash(id)?.toDomain()

    override fun save(su: QRFromUrl): QRFromUrl = qrEntityRepository.save(su.toEntity()).toDomain()
}

