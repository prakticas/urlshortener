package es.unizar.urlshortener.infrastructure.comunication

import com.google.gson.Gson
import es.unizar.urlshortener.core.UrlError
import es.unizar.urlshortener.core.threatInfo
import es.unizar.urlshortener.core.threatInfoURL
import org.apache.commons.validator.routines.UrlValidator
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Component
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


@Component
@Profile("ValidatorReceiver")
class ExternalData{
    @Value("\${apiKey}")
    lateinit var  apiKey :String
    fun apiKey():String= apiKey



}

@Component
@Profile("ValidatorReceiver")
class Receiver(
    private val externalData:ExternalData){


    private fun checkSafety(url: String):Boolean{

        val urlAsked = threatInfoURL(url=url)
        val body = threatInfo( threatEntries = arrayOf(urlAsked))
        val threatInfoRqt = Gson().toJson(body)
        val requestBody = "{threatInfo: $threatInfoRqt}"

        val ak = externalData.apiKey()
        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://safebrowsing.googleapis.com/v4/threatMatches:find?key=${externalData.apiKey()}"))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body().toString()=="{}\n"
    }

    private fun checkAvailability(url: String):Boolean{
        try {
            val urll = URL(url);
            val urlc = urll.openConnection() as HttpURLConnection;
            urlc.connectTimeout = 10 * 1000;   // 10 s.
            urlc.connect();
            return if (urlc.responseCode < 400) {  // 200 = "OK" code (http connection is fine).
                urlValidator.isValid(url);
            } else {
                false;
            }
        } catch (e : Exception) {
            return false;
        }
    }

    companion object {
        val urlValidator = UrlValidator(arrayOf("http", "https"))
    }

    @RabbitListener(queues = ["validation_queue.rpc.requests"])
    @SendTo("validation_queue.rpc.replies")
    fun receiveMessage(url: String): UrlError {

        println("Nodo  comprobando la URL $url")

        //checks the format
        if(!urlValidator.isValid(url))
            return UrlError.INCORRECT_URL

        //checks availability
        if(!checkAvailability(url))
            return UrlError.NOT_AVAILABLE
        //checks if it is safe
        if ( !checkSafety(url))
            return UrlError.NOT_SECURE

        //no errors found
        return UrlError.NO_ERROR
    }
}