package es.unizar.urlshortener.infrastructure.delivery

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException

@Component
class SseRepository {
    val sseEmitters: Multimap<String, SseEmitter> = MultimapBuilder.hashKeys().arrayListValues().build()
    fun put(id: String, sseEmitter: SseEmitter) {
        sseEmitters.put(id, sseEmitter)
    }

    fun createProgressListener(id: String): SseEmitterProgressListener {
        return SseEmitterProgressListener(sseEmitters[id])
    }
}

@Controller
class SseController(
    private val repository: SseRepository
) {
    @GetMapping("/progress-events")
    fun progressEvents(@RequestParam("uuid") id: String): SseEmitter {
        val sseEmitter = SseEmitter(Long.MAX_VALUE)
        repository.put(id, sseEmitter)
        println("Adding SseEmitter for user: $id")
        with(sseEmitter) {
            onCompletion { LOGGER.info("SseEmitter for user $id is completed") }
            onTimeout { LOGGER.info("SseEmitter for user $id is timed out") }
            onError { ex -> LOGGER.info("SseEmitter for user $id got error:", ex) }
        }
        return sseEmitter
    }

    companion object {
        private val LOGGER by logger()
    }
}

class SseEmitterProgressListener(private val sseEmitters: Collection<SseEmitter>) : ProgressListener {
    var htmlaso: String = ""
    
    override fun onProgress(value: Int) {
        val html = """
            <div id="progress-container" class="progress-container">
                <div class="progress-bar" style="width:$value%"></div>
            </div>
            <div>
                Downloading... $value
            </div>
            """.trimIndent()
        htmlaso = htmlaso.plus("\n").plus(html)
        sendToAllClients(htmlaso)
    }

    override fun onCompletion() {
        val html = "<div><a href=\"#\">Download PDF</div>"
        sendToAllClients(html)
    }

    private fun sendToAllClients(html: String) {
        for (sseEmitter in sseEmitters) {
            try {
                // multiline strings are sent as multiple "data:" SSE
                // this confuses HTMX in our example so, we remove all newlines
                // so only one "data:" is sent per html
                sseEmitter.send(html.replace("\n", ""))
            } catch (ex: IOException) {
                LOGGER.error(ex.message, ex)
            }
        }
    }

    companion object {
        private val LOGGER by logger()
    }
}

fun <R : Any> R.logger(): Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(this::class.java.name) }
}
