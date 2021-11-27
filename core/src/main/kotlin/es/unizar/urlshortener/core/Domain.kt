package es.unizar.urlshortener.core


import java.time.OffsetDateTime


/**
 * A [Click] captures a request of redirection of a [ShortUrl] identified by its [hash].
 */
data class Click(
    val hash: String,
    val properties: ClickProperties = ClickProperties(),
    val created: OffsetDateTime = OffsetDateTime.now()
)

/**
 * A [ShortUrl] is the mapping between a remote url identified by [redirection] and a local short url identified by [hash].
 */
data class ShortUrl(
    val hash: String,
    val redirection: Redirection,
    val created: OffsetDateTime = OffsetDateTime.now(),
    val properties: ShortUrlProperties = ShortUrlProperties(),
)

/**
 * A [QRFromUrl] specifies the [qr] that is obtained from [url]
 */
data class QRFromUrl(
    val url: ShortUrl,
    val qr: ByteArray,
    val properties: QRProperties = QRProperties(),
    val created: OffsetDateTime = OffsetDateTime.now()
)

/**
 * A [Redirection] specifies the [target] and the [status code][mode] of a redirection.
 * By default, the [status code][mode] is 307 TEMPORARY REDIRECT.
 */
data class Redirection(
    val target: String,
    val mode: Int = 307
)

/**
 * A [ShortUrlProperties] is the bag of properties that a [ShortUrl] may have.
 */
data class ShortUrlProperties(
    val ip: String? = null,
    val sponsor: String? = null,
    val safe: Boolean = true,
    val owner: String? = null,
    val country: String? = null
)

/**
 * A [ClickProperties] is the bag of properties that a [Click] may have.
 */
data class ClickProperties(
    val ip: String? = null,
    val referrer: String? = null,
    val browser: String? = null,
    val platform: String? = null,
    val country: String? = null
)

/**
 * A [QRProperties] is the bag of properties that a [QRFromUrl] must have.
 */
data class QRProperties(
    val width: Int = 200,
    val height: Int = 200,
)
/*const val NO_ERROR = 0
const val NOT_SECURE = 1
const val NOT_AVAILABLE = 2
const val INCORRECT_URL =3*/
enum class UrlError(val msg: String) {
    NO_ERROR("url correct"),
    NOT_SECURE("the url is not secure"),
    NOT_AVAILABLE("the url is not available now"),
    INCORRECT_URL("the format of the url is not correct")
}


data class threatInfoURL(
    val url:String
)

data class threatInfo(
    val threatTypes: Array<String> = arrayOf("MALWARE", "SOCIAL_ENGINEERING"),
    val platformTypes:Array<String> = arrayOf("WINDOWS", "LINUX"),
    val threatEntryTypes:Array<String> = arrayOf("URL"),
    val threatEntries:Array<threatInfoURL>,
)
