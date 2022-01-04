package es.unizar.urlshortener.infrastructure.comunication

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Component



@Component
class Receiver {


    @RabbitListener(queues = ["validation_queue.rpc.requests"])
    @SendTo("validation_queue.rpc.replies")
    fun receiveMessage(message: String):Boolean {
        println("Received <$message>")
        return true
    }
}