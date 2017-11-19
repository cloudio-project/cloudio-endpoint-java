package ch.hevs.cloudio.cloud.microservice.core

import com.rabbitmq.client.Channel
import org.springframework.amqp.core.Message

abstract class AbstractService: AbstractBaseService() {
    final override fun onMessage(message: Message?, channel: Channel?) {
        if (message != null) {
            handleMessage(message)
        }
    }

    abstract fun handleMessage(message: Message);
}
