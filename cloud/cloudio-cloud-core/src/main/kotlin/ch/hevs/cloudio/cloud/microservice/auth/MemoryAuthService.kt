package ch.hevs.cloudio.cloud.microservice.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import javax.annotation.PostConstruct

final class MemoryAuthService(): AbstractAuthService() {
    private data class Entity(
        val id: String,
        val password: String?,
        val authorities: Set<Authority>,
        val endpointPermissions: MutableMap<String, Permission> = TreeMap<String,Permission>())

    private var encoder: PasswordEncoder = BCryptPasswordEncoder()
    @Autowired(required = false) private lateinit var configurer: Configurer
    private val entities = ArrayList<Entity>()
    private var acceptAllCertificates = false

    @PostConstruct
    private fun configure() = configurer.configure(this)

    interface Configurer {
        fun configure(memoryAuthService: MemoryAuthService)
    }

    fun addEntityWithPassword(id: String, password: String, vararg authorities: Authority): MemoryAuthService {
        entities.add(Entity(id, encoder.encode(password), authorities.toSet()))
        return this
    }

    fun addEntityWithCertificate(id: String, vararg authorities: Authority): MemoryAuthService {
        entities.add(Entity(id, null, authorities.toSet()))
        return this
    }

    fun acceptAllCertificates() {
        acceptAllCertificates = true
    }

    fun hasEndpointPermission(endpointId: String, permission: Permission): MemoryAuthService {
        if (entities.isNotEmpty()) {
            entities.last().endpointPermissions[endpointId] = permission
        }
        return this
    }

    override fun authenticateEntityWithPassword(id: String, password: String): AuthenticationResult {
        val entity = entities.find { it.id == id && encoder.matches(password, it.password) }
        if (entity != null) {
            return AuthenticationResult(true, entity.authorities)
        } else {
            return AuthenticationResult()
        }
    }

    override fun authenticateEntityWithoutPassword(id: String): AuthenticationResult {
        val entity = entities.find { it.id == id && it.password == null }
        if (entity != null) {
            return AuthenticationResult(true, entity.authorities)
        } else {
            return AuthenticationResult(acceptAllCertificates)
        }
    }

    override fun authorizeEndpointAccess(id: String, endpointId: String): Permission {
        val entity = entities.find { it.id == id }
        if (entity !== null) {
            val endpointPermission = entity.endpointPermissions[endpointId]
            if (endpointPermission !== null) {
                return endpointPermission
            } else {
                return Permission.DENY
            }
        } else {
            return Permission.DENY
        }
    }
}