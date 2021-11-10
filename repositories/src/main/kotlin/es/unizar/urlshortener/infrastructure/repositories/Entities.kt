package es.unizar.urlshortener.infrastructure.repositories

import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*

/**
 * The [ClickEntity] entity logs clicks.
 */
@Entity
@Table(name = "click")
class ClickEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long?,
    val hash: String,
    val created: OffsetDateTime,
    val ip: String?,
    val referrer: String?,
    val browser: String?,
    val platform: String?,
    val country: String?
)

/**
 * The [ShortUrlEntity] entity stores short urls.
 */
@Entity
@Table(name = "shorturl")
class ShortUrlEntity(
    @Id
    val hash: String,
    val target: String,
    val sponsor: String?,
    val created: OffsetDateTime,
    val owner: String?,
    val mode: Int,
    val safe: Boolean,
    val ip: String?,
    val country: String?
)

@Entity
@Table(name = "QR")
class QREntity(
    @Id
    val hash: String,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "url_id", referencedColumnName = "hash")
    val url: ShortUrlEntity,
    @Lob
    val qr: ByteArray
)