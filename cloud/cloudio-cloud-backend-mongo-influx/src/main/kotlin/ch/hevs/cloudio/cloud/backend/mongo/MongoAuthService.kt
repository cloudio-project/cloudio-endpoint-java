package ch.hevs.cloudio.cloud.backend.mongo

import ch.hevs.cloudio.cloud.microservice.auth.AbstractAuthService
import ch.hevs.cloudio.cloud.microservice.auth.AuthenticationResult
import ch.hevs.cloudio.cloud.microservice.auth.Permission
import ch.hevs.cloudio.cloud.backend.mongo.repo.EndpointEntityRepository
import ch.hevs.cloudio.cloud.backend.mongo.repo.UserEntity
import ch.hevs.cloudio.cloud.backend.mongo.repo.UserEntityRepository
import ch.hevs.cloudio.cloud.microservice.auth.Authority
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class MongoAuthService @Autowired constructor(private val users: UserEntityRepository,
                                              private val endpoints: EndpointEntityRepository): AbstractAuthService() {
    private var encoder: PasswordEncoder = BCryptPasswordEncoder()

    @PostConstruct
    private fun createFirstUser() {
        if (users.count() == 0L) {
            users.save(UserEntity("admin",
                    encoder.encode("admin"),
                    setOf(Authority.BROKER_ADMINISTRATION, Authority.HTTP_ACCESS),
                    mapOf("test" to Permission.OWN)))
        }
    }

    override fun authenticateEntityWithPassword(id: String, password: String): AuthenticationResult {
        val user = users.findOne(id)
        if (user != null) {
            if (encoder.matches(password, user.passwordHash)) {
                return AuthenticationResult(true, user.authorities)
            }
        }

        return AuthenticationResult()
    }

    override fun authenticateEntityWithoutPassword(id: String): AuthenticationResult =
            AuthenticationResult(endpoints.findOne(id)?.blocked?.not() ?: true)

    override fun authorizeEndpointAccess(id: String, endpointId: String): Permission =
        users.findOne(id).permissions[endpointId] ?: Permission.DENY
}