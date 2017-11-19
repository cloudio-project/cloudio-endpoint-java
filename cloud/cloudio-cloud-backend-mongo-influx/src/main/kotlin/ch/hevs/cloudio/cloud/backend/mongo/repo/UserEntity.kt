package ch.hevs.cloudio.cloud.backend.mongo.repo

import ch.hevs.cloudio.cloud.microservice.auth.Authority
import ch.hevs.cloudio.cloud.microservice.auth.Permission
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "User")
data class UserEntity (
        @Id
        var userName: String = "",
        var passwordHash: String = "",
        var authorities: Set<Authority> = emptySet(),
        var permissions: Map<String, Permission> = emptyMap()
)