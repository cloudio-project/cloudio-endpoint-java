package ch.hevs.cloudio.cloud.microservice.core.annotation

import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class TopicService(
    val topics: Array<String>
)
