package es.unizar.urlshortener.infrastructure.delivery

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component



@Component
class Receiver {


    @RabbitListener(queues = ["validation_queue"])
    fun receiveMessage(message: String) {
        println("Received <$message>")
    }
}