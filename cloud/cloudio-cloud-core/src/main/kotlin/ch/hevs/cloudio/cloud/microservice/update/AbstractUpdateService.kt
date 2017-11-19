package ch.hevs.cloudio.cloud.microservice.update

import ch.hevs.cloudio.cloud.microservice.core.AbstractService
import ch.hevs.cloudio.cloud.microservice.core.annotation.TopicService
import ch.hevs.cloudio.cloud.model.Attribute
import ch.hevs.cloudio.cloud.serialization.SerializationFormatFactory
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message

@TopicService(topics = arrayOf("@update.#"))
abstract class AbstractUpdateService: AbstractService() {
    companion object {
        private val log = LoggerFactory.getLogger(AbstractUpdateService::class.java)
    }

    final override fun handleMessage(message: Message) {
        try {
            val attributeId = message.messageProperties.receivedRoutingKey.removePrefix("@update.")
            val data = message.body
            val messageFormat = SerializationFormatFactory.serializationFormat(data)
            if (messageFormat != null) {
                val attribute = Attribute()
                messageFormat.deserializeAttribute(attribute, data)
                if (attribute.timestamp != -1.0 && attribute.value != null) {
                    attributeUpdated(attributeId, attribute)
                }
            } else {
                log.error("Unrecognized message format in @update message from $attributeId")
            }
        } catch (exception: Exception) {
            log.error("Exception during @online message handling:", exception)
        }
    }

    abstract fun attributeUpdated(attributeId: String, attribute: Attribute)
}