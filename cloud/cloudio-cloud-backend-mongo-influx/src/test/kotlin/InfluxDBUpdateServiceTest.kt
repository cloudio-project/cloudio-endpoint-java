
import ch.hevs.cloudio.cloud.backend.influx.InfluxUpdateService
import org.influxdb.InfluxDB
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner


@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = arrayOf(InfluxDBUpdateServiceTest::class))
class InfluxDBUpdateServiceTest {

    @Autowired lateinit var influx: InfluxDB
    @Autowired lateinit var env: Environment

    @Bean
    fun influxDB(): InfluxDB = InfluxDBTestMock()

    @Test
    fun testBooleanUpdateMessage() {
        val service = InfluxUpdateService(env, influx)
        service.handleMessage(Message("{\"timestamp\": 1500554648.614, \"type\": \"Boolean\", \"value\": true, \"constraint\": \"Measure\"}".toByteArray(),
                MessageProperties().let {
                    it.receivedRoutingKey = "@update.test"
                    it
                }
        ))
        print((influx as InfluxDBTestMock).points.firstOrNull()?.lineProtocol())
        assertEquals((influx as InfluxDBTestMock)?.points.firstOrNull()?.lineProtocol(), "test,constraint=Measure,type=Boolean value=true 1500554648614000000")
        influx.deleteDatabase("CLOUDIO")
    }

    @Test
    fun testIntegerUpdateMessage() {
        val service = InfluxUpdateService(env, influx)
        service.handleMessage(Message("{\"timestamp\": 1500554648.614, \"type\": \"Integer\", \"value\": 99, \"constraint\": \"Parameter\"}".toByteArray(),
                        MessageProperties().let {
                            it.receivedRoutingKey = "@update.test"
                            it
                        }
        ))
        print((influx as InfluxDBTestMock).points.firstOrNull()?.lineProtocol())
        assertEquals((influx as InfluxDBTestMock)?.points.firstOrNull()?.lineProtocol(), "test,constraint=Parameter,type=Integer value=99i 1500554648614000000")
        influx.deleteDatabase("CLOUDIO")
    }

    @Test
    fun testNumberUpdateMessage() {
        val service = InfluxUpdateService(env, influx)
        service.handleMessage(Message("{\"timestamp\": 1500554648.614, \"type\": \"Number\", \"value\": 1.234, \"constraint\": \"SetPoint\"}".toByteArray(),
                MessageProperties().let {
                    it.receivedRoutingKey = "@update.test"
                    it
                }
        ))
        print((influx as InfluxDBTestMock).points.firstOrNull()?.lineProtocol())
        assertEquals((influx as InfluxDBTestMock)?.points.firstOrNull()?.lineProtocol(), "test,constraint=SetPoint,type=Number value=1.234 1500554648614000000")
        influx.deleteDatabase("CLOUDIO")
    }

    @Test
    fun testStringUpdateMessage() {
        val service = InfluxUpdateService(env, influx)
        service.handleMessage(Message("{\"timestamp\": 1500554648.614, \"type\": \"String\", \"value\": \"Hello influx\", \"constraint\": \"Status\"}".toByteArray(),
                MessageProperties().let {
                    it.receivedRoutingKey = "@update.test"
                    it
                }
        ))
        print((influx as InfluxDBTestMock).points.firstOrNull()?.lineProtocol())
        assertEquals((influx as InfluxDBTestMock)?.points.firstOrNull()?.lineProtocol(), "test,constraint=Status,type=String value=\"Hello influx\" 1500554648614000000")
        influx.deleteDatabase("CLOUDIO")
    }
}
