package ch.hevs.cloudio.cloud.microservice.auth

import ch.hevs.cloudio.cloud.microservice.core.AbstractRpcService
import ch.hevs.cloudio.cloud.microservice.core.annotation.FanoutService
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import java.util.*

@FanoutService(exchange = "authentication")
abstract class AbstractAuthService: AbstractRpcService() {
    companion object {
        private val log = LoggerFactory.getLogger(AbstractAuthService::class.java)
    }

    private fun Boolean.toAmqAuthResult() = if (this) "allow" else "deny"

    override fun handleMessage(message: Message): Any? {
        try {
            when (message.messageProperties.headers["action"]?.toString()) {
                "login" -> {
                    val id = message.messageProperties.headers["username"]?.toString()
                    if (id != null) {
                        val password = message.messageProperties.headers["password"]?.toString()
                        val authenticationResult = if (password != null) {
                            authenticateEntityWithPassword(id, password)
                        } else {
                            authenticateEntityWithoutPassword(id)
                        }
                        if (authenticationResult.authenticated) {
                            return authenticationResult.authorities.map { it.value }.joinToString(separator = ",")
                        } else {
                            log.info("Login attempt for entity $id is negative. Access denied.")
                            return "refused"
                        }
                    } else {
                        log.warn("Login attempt with invalid entity (null), Access denied.")
                        return "refused"
                    }
                }
                "check_vhost" -> {
                    val id = message.messageProperties.headers["username"]?.toString()
                    val vhost = message.messageProperties.headers["vhost"]?.toString()
                    if (id != null && vhost != null) {
                        return authorizeVHostAccess(id, vhost).toAmqAuthResult()
                    } else {
                        log.warn("Entity ID or vHost missing in vHost authorization attempt. Access denied.")
                        return false.toAmqAuthResult()
                    }
                }
                "check_resource" -> {
                    val id = message.messageProperties.headers["username"]?.toString()
                    val resource = message.messageProperties.headers["resource"]?.toString()
                    val name = message.messageProperties.headers["name"]?.toString()
                    val permissionStr = message.messageProperties.headers["permission"]?.toString()
                    if (permissionStr != null) {
                        try {
                            val permission = Permission.valueOf(permissionStr.toUpperCase(Locale.ENGLISH))
                            if (id != null && name != null && resource != null) {
                                when (resource) {
                                    "exchange" -> {
                                        return (authorizeExchangeAccess(id, name) >= permission).toAmqAuthResult()
                                    }
                                    "queue" -> {
                                        return (authorizeQueueAccess(id, name) >= permission).toAmqAuthResult()
                                    }
                                    "routing_key" -> {
                                        val topicComponents = name.split(".")
                                        if (topicComponents.size >= 2) {
                                            return (authorizeEndpointAccess(id, topicComponents[1]) >= permission)
                                                    .toAmqAuthResult()
                                        } else {
                                            log.warn("$name is an invalid topic to authenticate. Access denied.")
                                        }
                                    }
                                }
                            } else {
                                log.info("Entity ID or resource name/type missing in resource authorization attempt. " +
                                        "Access denied.")
                                return false.toAmqAuthResult()
                            }
                        } catch (exception: IllegalArgumentException) {
                            log.warn("Invalid permission $permissionStr in resource authorization attempt. " +
                                     "Access denied")
                            return false.toAmqAuthResult()
                        }
                    } else {
                        log.warn("Missing permission in resource authorization attempt. Access denied")
                        return false.toAmqAuthResult()
                    }
                }
                else -> {
                    log.warn("Unsupported authentication message: ${message.messageProperties.headers}. " +
                             "Access denied.")
                    return "refused"
                }
            }
        } catch (exception: Exception) {
            log.error("Exception during authentication message handling", exception)
        }

        return null
    }

    abstract fun authenticateEntityWithPassword(id: String, password: String): AuthenticationResult
    abstract fun authenticateEntityWithoutPassword(id: String): AuthenticationResult

    open fun authorizeVHostAccess(id: String, vHost: String): Boolean = vHost == "/"
    open fun authorizeExchangeAccess(id: String, exchange: String): Permission {
        return when (exchange) {
            "amq.topic" -> Permission.WRITE
            else -> Permission.DENY
        }
    }
    open fun authorizeQueueAccess(id: String, queueName: String): Permission {
        // TODO: Allow only generated, exclusive queues for AMQP or MQTT...
        // For the moment we allow everyone to do everything on queues for simplicity.
        return Permission.CONFIGURE
    }
    abstract fun authorizeEndpointAccess(id: String, endpointId: String): Permission
}