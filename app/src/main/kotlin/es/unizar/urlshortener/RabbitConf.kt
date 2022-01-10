package es.unizar.urlshortener

import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConf {
    val VALIDATION_QUEUE = "validation_queue.rpc.requests"
    val VALIDATION_EXCHANGE = "validation_exchange.rpc"
    val ROUTING_KEY = "rpc"

    @Bean
    fun queue() = Queue(VALIDATION_QUEUE)

    @Bean
    fun exchange() = TopicExchange(VALIDATION_EXCHANGE)

    @Bean
    fun binding() = BindingBuilder.bind(queue()).to(exchange()).with(ROUTING_KEY)


    @Bean
    fun converter() = Jackson2JsonMessageConverter()


    @Bean
    fun template(connectionFactory: ConnectionFactory): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = converter()
        return rabbitTemplate
    }


}