package ch.hevs.cloudio.cloud.backend.mongo

import ch.hevs.cloudio.cloud.backend.mongo.repo.EndpointEntityRepository
import ch.hevs.cloudio.cloud.microservice.update.AbstractUpdateService
import ch.hevs.cloudio.cloud.model.Attribute
import ch.hevs.cloudio.cloud.model.CloudioObject
import ch.hevs.cloudio.cloud.model.Node
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class MongoUpdateService @Autowired constructor(
                         private val endpointEntityRepository: EndpointEntityRepository): AbstractUpdateService() {
    override fun attributeUpdated(attributeId: String, attribute: Attribute) {
        val path = Stack<String>()
        path.addAll(attributeId.split(".").toList().reversed())
        if (path.size >= 3) {
            val endpointEntity = endpointEntityRepository.findOne(path.pop())
            if (endpointEntity != null && path.pop() == "nodes") {
                val node = endpointEntity.endpoint.nodes[path.pop()]
                if (node != null) {
                    val existingAttribute = findAttributeInNode(node, path)
                    if (existingAttribute != null) {
                        existingAttribute.timestamp = attribute.timestamp
                        existingAttribute.constraint = attribute.constraint
                        existingAttribute.type = attribute.type
                        existingAttribute.value = attribute.value
                        endpointEntityRepository.save(endpointEntity)
                    }
                }
            }
        }
    }

    private fun findAttributeInNode(node: Node, path: Stack<String>): Attribute? {
        if (path.size > 1 && path.pop() == "objects") {
            val obj = node.objects[path.pop()]
            if (obj != null) {
                return findAttributeInObject(obj, path)
            }
        }

        return null
    }

    private fun findAttributeInObject(obj: CloudioObject, path: Stack<String>): Attribute? {
        if (path.size > 1) {
            when (path.pop()) {
                "objects" -> {
                    val childObj = obj.objects[path.pop()]
                    if (childObj != null) {
                        return findAttributeInObject(childObj, path)
                    } else {
                        return null
                    }
                }
                "attributes" -> return obj.attributes[path.pop()]
                else -> return null
            }
        } else {
            return null
        }
    }
}
