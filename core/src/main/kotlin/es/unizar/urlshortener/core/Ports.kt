package es.unizar.urlshortener.core

import com.google.zxing.common.BitMatrix
import org.jetbrains.annotations.TestOnly

/**
 * [ClickRepositoryService] is the port to the repository that provides persistence to [Clicks][Click].
 */
interface ClickRepositoryService {
    fun save(cl: Click): Click
}

/**
 * [ShortUrlRepositoryService] is the port to the repository that provides management to [ShortUrl][ShortUrl].
 */
interface ShortUrlRepositoryService {
    fun findByKey(id: String): ShortUrl?
    fun save(su: ShortUrl): ShortUrl
}

/**
 *
 * **Note** it could be refactor
 */

interface QRRepositoryService {
    fun findByKey(id: String): QRFromUrl?
    fun save(su: QRFromUrl): QRFromUrl
}

/**
 * [ValidatorService] is the port to the service that validates if an url can be shortened.
 *
 * **Note**: It is a design decision to create this port. It could be part of the core .
 */
interface ValidatorService {
    fun isValid(url: String): UrlError
}

/**
 * [HashService] is the port to the service that creates a hash from a URL.
 *
 * **Note**: It is a design decision to create this port. It could be part of the core .
 */
interface HashService {
    fun hasUrl(url: String): String
}

/**
 * [QRService] is the port to the service that creates a QR from a URL.
 */
interface QRService {
    fun createQR(url: ShortUrl, data: QRProperties = QRProperties()): QRFromUrl
    fun encodeQR(url:String, width: Int, height: Int): BitMatrix
    @TestOnly
    fun decodeQR(qrBitMatrix: BitMatrix): String
    fun QRToByteArray(bitMatrix: BitMatrix): ByteArray
}