package ch.hevs.cloudio.cloud.microservice.lifecycle

import ch.hevs.cloudio.cloud.microservice.core.AbstractService
import ch.hevs.cloudio.cloud.microservice.core.annotation.TopicService
import ch.hevs.cloudio.cloud.model.Endpoint
import ch.hevs.cloudio.cloud.model.Node
import ch.hevs.cloudio.cloud.serialization.SerializationFormatFactory
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message

@TopicService(topics = arrayOf("@online.*", "@offline.*", "@nodeAdded.*.nodes.*", "@nodeRemoved.*.nodes.*"))
abstract class AbstractLifecycleService: AbstractService() {
    companion object {
        private val log = LoggerFactory.getLogger(AbstractLifecycleService::class.java)
    }

    final override fun handleMessage(message: Message) {
        with (message.messageProperties.receivedRoutingKey) {
            if (startsWith("@online.")) {
                handleOnlineMessage(message)
            } else if (startsWith("@offline.")) {
                handleOfflineMessage(message)
            } else if (startsWith("@nodeAdded.")) {
                handleNodeAddedMessage(message)
            } else if (startsWith("@nodeRemoved.")) {
                handleNodeRemovedMessage(message)
            }
        }
    }


    final private fun handleOnlineMessage(message: Message) {
        try {
            val endpointId = message.messageProperties.receivedRoutingKey.split(".")[1]
            val data = message.body
            val messageFormat = SerializationFormatFactory.serializationFormat(data)
            if (messageFormat != null) {
                val endpoint = Endpoint()
                messageFormat.deserializeEndpoint(endpoint, data)
                endpointIsOnline(endpointId, endpoint)
            } else {
                log.error("Unrecognized message format in @online message from $endpointId")
            }
        } catch (exception: Exception) {
            log.error("Exception during @online message handling:", exception)
        }
    }

    abstract fun endpointIsOnline(endpointId: String, endpoint: Endpoint)


    final private fun handleOfflineMessage(message: Message) {
        try {
            endpointIsOffline(message.messageProperties.receivedRoutingKey.split(".")[1])
        } catch (exception: Exception) {
            log.error("Exception during @offline message handling:", exception)
        }
    }

    abstract fun endpointIsOffline(endpointId: String)


    final private fun handleNodeAddedMessage(message: Message) {
        try {
            val endpointId = message.messageProperties.receivedRoutingKey.split(".")[1]
            val nodeName = message.messageProperties.receivedRoutingKey.split(".")[3]
            val data = message.body
            val messageFormat = SerializationFormatFactory.serializationFormat(data)
            if (messageFormat != null) {
                val node = Node()
                messageFormat.deserializeNode(node, data)
                nodeAdded(endpointId, nodeName, node)
            } else {
                log.error("Unrecognized message format in @online message from $endpointId")
            }
        } catch (exception: Exception) {
            log.error("Exception during @nodeAdded message handling:", exception)
        }
    }

    abstract fun nodeAdded(endpointId: String, nodeName: String, node: Node)


    final private fun handleNodeRemovedMessage(message: Message) {
        try {
            val endpointId = message.messageProperties.receivedRoutingKey.split(".")[1]
            val nodeName = message.messageProperties.receivedRoutingKey.split(".")[3]
            nodeRemoved(endpointId, nodeName)
        } catch (exception: Exception) {
            log.error("Exception during @nodeAdded message handling:", exception)
        }
    }

    abstract fun nodeRemoved(endpointId: String, nodeName: String)
}