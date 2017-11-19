package ch.hevs.cloudio.cloud.backend.mongo.repo

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserEntityRepository: MongoRepository<UserEntity, String>
