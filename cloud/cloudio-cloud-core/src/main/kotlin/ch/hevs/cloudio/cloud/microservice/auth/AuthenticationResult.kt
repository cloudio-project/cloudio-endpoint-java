package ch.hevs.cloudio.cloud.microservice.auth

final data class AuthenticationResult(
    val authenticated: Boolean = false,
    val authorities: Set<Authority> = emptySet()
)