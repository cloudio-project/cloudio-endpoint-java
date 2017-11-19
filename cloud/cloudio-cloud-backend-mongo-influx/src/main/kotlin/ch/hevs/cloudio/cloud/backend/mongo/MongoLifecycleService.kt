package ch.hevs.cloudio.cloud.backend.mongo

import ch.hevs.cloudio.cloud.microservice.lifecycle.AbstractLifecycleService
import ch.hevs.cloudio.cloud.backend.mongo.repo.EndpointEntity
import ch.hevs.cloudio.cloud.backend.mongo.repo.EndpointEntityRepository
import ch.hevs.cloudio.cloud.model.Endpoint
import ch.hevs.cloudio.cloud.model.Node
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MongoLifecycleService @Autowired constructor(
        private val endpointEntityRepository: EndpointEntityRepository): AbstractLifecycleService() {
    override fun endpointIsOnline(endpointId: String, endpoint: Endpoint) {
        val endpointEntity = endpointEntityRepository.findOne(endpointId) ?: EndpointEntity(endpointId)
        endpointEntity.online = true
        endpointEntity.endpoint = endpoint
        endpointEntityRepository.save(endpointEntity)
    }

    override fun endpointIsOffline(endpointId: String) {
        val endpointEntity = endpointEntityRepository.findOne(endpointId)
        if (endpointEntity != null) {
            endpointEntity.online = false
            endpointEntityRepository.save(endpointEntity)
        }
    }

    override fun nodeAdded(endpointId: String, nodeName: String, node: Node) {
        val endpointEntity = endpointEntityRepository.findOne(endpointId)
        if (endpointEntity != null) {
            endpointEntity.endpoint.nodes[nodeName] = node
            endpointEntityRepository.save(endpointEntity)
        }
    }

    override fun nodeRemoved(endpointId: String, nodeName: String) {
        val endpointEntity = endpointEntityRepository.findOne(endpointId)
        if (endpointEntity != null) {
            endpointEntity.endpoint.nodes.remove(nodeName)
            endpointEntityRepository.save(endpointEntity)
        }
    }
}