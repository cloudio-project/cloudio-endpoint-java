package ch.hevs.cloudio.cloud.backend.influx

import ch.hevs.cloudio.cloud.microservice.update.AbstractUpdateService
import ch.hevs.cloudio.cloud.model.Attribute
import ch.hevs.cloudio.cloud.model.AttributeType
import org.influxdb.InfluxDB
import org.influxdb.dto.Point
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class InfluxUpdateService @Autowired constructor(val env: Environment, val influx: InfluxDB): AbstractUpdateService() {
    val database: String by lazy { env.getProperty("CLOUDIO_INFLUX_DATABASE", "CLOUDIO") }

    override fun attributeUpdated(attributeId: String, attribute: Attribute) {
        val point = Point
                .measurement(attributeId)
                .time((attribute.timestamp *(1000.0) * 1000.0).toLong(), TimeUnit.MICROSECONDS)
                .tag("constraint", attribute.constraint.toString())
                .tag("type", attribute.type.toString())

        when (attribute.type) {
            AttributeType.Boolean -> point.addField("value", attribute.value as Boolean)
            AttributeType.Integer, AttributeType.Number -> point.addField("value", attribute.value as Number)
            AttributeType.String -> point.addField("value", attribute.value as String)
            else -> {}
        }

        influx.write(database, "autogen", point.build())
    }
}
