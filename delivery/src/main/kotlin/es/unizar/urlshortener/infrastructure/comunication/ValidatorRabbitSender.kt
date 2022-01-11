package es.unizar.urlshortener.infrastructure.comunication

import es.unizar.urlshortener.core.UrlError
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component


class ValidatorRabbitSender(
    private val template: RabbitTemplate,
    private val exchange: TopicExchange) {

    fun validate(url: String): UrlError {
        return  template.convertSendAndReceive(exchange.name, "rpc", url)  as UrlError
    }
}