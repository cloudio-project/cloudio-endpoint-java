package ch.hevs.cloudio.cloud.microservice.core

import ch.hevs.cloudio.cloud.microservice.core.annotation.FanoutService
import ch.hevs.cloudio.cloud.microservice.core.annotation.TopicService
import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar
import org.springframework.amqp.rabbit.listener.adapter.AbstractAdaptableMessageListener
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractBaseService:
        AbstractAdaptableMessageListener(), RabbitListenerConfigurer {
    @Autowired private lateinit var amqp: AmqpAdmin

    override fun configureRabbitListeners(registrar: RabbitListenerEndpointRegistrar?) {
        if (registrar != null) {
            val topicAnnotation = this.javaClass.getAnnotation(TopicService::class.java)
            if (topicAnnotation != null && topicAnnotation.topics.size > 0) {
                configureTopicService(registrar, topicAnnotation.topics)
            }

            val fanoutAnnotation = this.javaClass.getAnnotation(FanoutService::class.java)
            if (fanoutAnnotation != null && fanoutAnnotation.exchange.isNotBlank()) {
                configureFanoutService(registrar, fanoutAnnotation.exchange)
            }
        }
    }

    private fun configureTopicService(registrar: RabbitListenerEndpointRegistrar, topics: Array<String>) {
        val queue = Queue(this.javaClass.canonicalName)
        amqp.declareQueue(queue)

        val exchange = TopicExchange("amq.topic")
        for (topic in topics) {
            amqp.declareBinding(BindingBuilder.bind(queue).to(exchange).with(topic))
        }

        val endpoint = SimpleRabbitListenerEndpoint()
        endpoint.id = this.javaClass.canonicalName
        endpoint.setQueues(queue)
        endpoint.messageListener = this
        registrar.registerEndpoint(endpoint)
    }

    private fun configureFanoutService(registrar: RabbitListenerEndpointRegistrar, exchangeName: String) {
        val queue = Queue(this.javaClass.canonicalName)
        amqp.declareQueue(queue)

        val exchange = FanoutExchange(exchangeName)
        amqp.declareBinding(BindingBuilder.bind(queue).to(exchange))

        val endpoint = SimpleRabbitListenerEndpoint()
        endpoint.id = this.javaClass.canonicalName
        endpoint.setQueues(queue)
        endpoint.messageListener = this
        registrar.registerEndpoint(endpoint)
    }
}
