package ch.hevs.cloudio.cloud.backend

import ch.hevs.cloudio.cloud.microservice.MicroserviceConfiguration
import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import org.apache.commons.logging.LogFactory
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import java.util.concurrent.TimeUnit

@SpringBootApplication
@Configuration
@Import(MicroserviceConfiguration::class)
open class MongoInfluxConfiguration {
    private val log = LogFactory.getLog(MongoInfluxConfiguration::class.java)
    @Autowired lateinit private var env: Environment

    @Bean
    open fun mongoDbFactory(): MongoDbFactory {
        val credentials = mutableListOf<MongoCredential>()
        if (env.containsProperty("CLOUDIO_MONGO_PASSWORD")) {
            credentials.add(MongoCredential.createCredential(
                    env.getProperty("CLOUDIO_MONGO_USER", "cloudio_services"),
                    env.getProperty("CLOUDIO_MONGO_DATABASE", "CLOUDIO"),
                    env.getProperty("CLOUDIO_MONGO_PASSWORD").toCharArray())
            )
        }

        return SimpleMongoDbFactory(
                MongoClient(ServerAddress(
                        env.getProperty("CLOUDIO_MONGO_HOST", "cloudio-mongo"),
                        env.getProperty("CLOUDIO_MONGO_PORT", Int::class.java, 27017)),
                        credentials),
                env.getProperty("CLOUDIO_MONGO_DATABASE", "CLOUDIO")
        )
    }

    @Bean
    open fun mongoTemplate() = MongoTemplate(mongoDbFactory())

    @Bean
    open fun influxDb(): InfluxDB {
        val influx = InfluxDBFactory.connect(
                "http://${env.getProperty("CLOUDIO_INFLUX_HOST", "cloudio-influx")}:" +
                        "${env.getProperty("CLOUDIO_INFLUX_PORT", Int::class.java, 8086)}",
                env.getProperty("CLOUDIO_INFLUX_USER", "cloudio_services"),
                env.getProperty("CLOUDIO_INFLUX_PASSWORD"))

        val database = env.getProperty("CLOUDIO_INFLUX_DATABASE", "CLOUDIO")

        try {
            if (!influx.describeDatabases().contains(database)) {
                influx.createDatabase(database)
            }
        } catch (e: RuntimeException) {
            log.warn("Could not check if MongoDB database \"$database\" exists.")
        }

        if (env.containsProperty("CLOUDIO_INFLUX_BATCHSIZE")) {
            influx.enableBatch(
                    env.getProperty("CLOUDIO_INFLUX_BATCHSIZE", Int::class.java),
                    env.getProperty("CLOUDIO_INFLUX_FLUSHDURATION", Int::class.java, 10000),
                    TimeUnit.MILLISECONDS)
        }

        return influx
    }
}

fun main(vararg parameters: String) {
    SpringApplication.run(MongoInfluxConfiguration::class.java)
}
