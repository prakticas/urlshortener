package es.unizar.urlshortener.infrastructure.delivery

import org.springframework.stereotype.Component
import java.util.*

interface ProgressListener {
    fun onProgress(value: Int)
    fun onCompletion()
}

@Component
class PdfGenerator {
    private val randomGenerator: Random = Random()
    fun generatePdf(id: String, listener: ProgressListener) {
        LOGGER.info("Generating PDF for user $id...")
        var progress = 0
        listener.onProgress(progress)
        do {
            sleep()
            progress = (progress + randomGenerator.nextInt(10)).coerceAtMost(100)
            LOGGER.info("Progress for user $id: $progress")
            listener.onProgress(progress)
        } while (progress < 100)
        LOGGER.info("Done for user $id!")
        listener.onCompletion()
    }

    companion object {
        private val LOGGER by logger()
    }
}

fun sleep() {
    try {
        Thread.sleep(500)
    } catch (e: InterruptedException) {
    }
}
