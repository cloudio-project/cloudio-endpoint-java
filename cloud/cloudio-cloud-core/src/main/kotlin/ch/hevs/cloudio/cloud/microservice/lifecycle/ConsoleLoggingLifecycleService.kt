package ch.hevs.cloudio.cloud.microservice.lifecycle

import ch.hevs.cloudio.cloud.model.Endpoint
import ch.hevs.cloudio.cloud.model.Node
import org.slf4j.LoggerFactory

class ConsoleLoggingLifecycleService: AbstractLifecycleService() {
    companion object {
        val log = LoggerFactory.getLogger(ConsoleLoggingLifecycleService::class.java)
    }

    override fun endpointIsOnline(endpointId: String, endpoint: Endpoint) {
        log.info("Endpoint \"$endpointId\" is online: $endpoint")
    }

    override fun endpointIsOffline(endpointId: String) {
        log.info("Endpoint \"$endpointId\" is offline.")
    }

    override fun nodeAdded(endpointId: String, nodeName: String, node: Node) {
        log.info("New node \"$nodeName\" detected on endpoint \"$endpointId\": $node")
    }

    override fun nodeRemoved(endpointId: String, nodeName: String) {
        log.info("Node \"$nodeName\" removed from endpoint \"$endpointId\".")
    }
}