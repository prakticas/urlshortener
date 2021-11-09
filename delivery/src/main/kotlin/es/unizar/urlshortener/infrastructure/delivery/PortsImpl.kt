package es.unizar.urlshortener.infrastructure.delivery

import com.google.common.hash.Hashing
import es.unizar.urlshortener.core.HashService
import es.unizar.urlshortener.core.ValidatorService
import org.apache.commons.validator.routines.UrlValidator
import java.nio.charset.StandardCharsets
import java.lang.Runtime
import java.util.concurrent.TimeUnit
import java.net.HttpURLConnection
import java.net.URLConnection
import java.net.URL

/**
 * Implementation of the port [ValidatorService].
 */
class ValidatorServiceImpl : ValidatorService {
    override fun isValid(url: String) :Boolean {
        try {
            val urll = URL(url);   // Change to "http://google.com" for www  test.
            val urlc = urll.openConnection() as HttpURLConnection;
            urlc.setConnectTimeout(10 * 1000);          // 10 s.
            urlc.connect();
            if (urlc.getResponseCode() < 400) {        // 200 = "OK" code (http connection is fine).
                System.out.println("Si" + urlc.getResponseCode())
                return true && urlValidator.isValid(url);
            } else {
                System.out.println("No" + urlc.getResponseCode())
                return false;
            }
        } catch (e : Exception) {
            return false;
        }
    }

    companion object {
        val urlValidator = UrlValidator(arrayOf("https"))
    }
}

/**
 * Implementation of the port [HashService].
 */
@Suppress("UnstableApiUsage")
class HashServiceImpl : HashService {
    override fun hasUrl(url: String) = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString()
}