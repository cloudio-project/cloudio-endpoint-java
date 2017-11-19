package ch.hevs.cloudio.cloud.microservice.core

import com.rabbitmq.client.Channel
import org.springframework.amqp.core.Message

abstract class AbstractRpcService: AbstractBaseService() {
    final override fun onMessage(message: Message?, channel: Channel?) {
        if (message != null) {
            val result = handleMessage(message)
            if (result != null && message.messageProperties.replyTo != null) {
                handleResult(result, message, channel)
            }
        }
    }

    abstract fun handleMessage(message: Message): Any?;
}
