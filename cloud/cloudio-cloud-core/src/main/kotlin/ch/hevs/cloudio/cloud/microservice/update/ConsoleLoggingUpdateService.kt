package ch.hevs.cloudio.cloud.microservice.update

import ch.hevs.cloudio.cloud.model.Attribute
import org.slf4j.LoggerFactory

class ConsoleLoggingUpdateService: AbstractUpdateService() {
    companion object {
        val log = LoggerFactory.getLogger(ConsoleLoggingUpdateService::class.java)
    }

    override fun attributeUpdated(attributeId: String, attribute: Attribute) {
        log.info("Attribute $attributeId has changed: $attribute")
    }
}