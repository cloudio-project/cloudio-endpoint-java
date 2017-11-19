import org.influxdb.InfluxDB
import org.influxdb.dto.*
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer
import java.util.function.Consumer

class InfluxDBTestMock : InfluxDB {
    private var batchEnabled = true
    private var gzipEnabled = true
    val points = mutableListOf<Point>()

    override fun disableBatch() {
        batchEnabled = false
    }

    override fun isGzipEnabled() = gzipEnabled

    override fun setLogLevel(logLevel: InfluxDB.LogLevel?) = this

    override fun version(): String = "TEST-MOCK"

    override fun describeDatabases() = mutableListOf("CLOUDIO")

    override fun write(point: Point?) {
        point?.let { points.add(it) }
    }

    override fun write(records: String?) {
        TODO("not implemented")
    }

    override fun write(records: MutableList<String>?) {
        TODO("not implemented")
    }

    override fun write(database: String?, retentionPolicy: String?, point: Point?) {
        assert(database == "CLOUDIO") { "You can only write to database CLOUDIO during unit testing!" }
        write(point)
    }

    override fun write(udpPort: Int, point: Point?) {
        TODO("not implemented")
    }

    override fun write(batchPoints: BatchPoints?) {
        assert(batchPoints?.database == "CLOUDIO") { "You can only write to database CLOUDIO during unit testing!" }
        batchPoints?.points?.forEach {
            write(it)
        }
    }

    override fun write(database: String?, retentionPolicy: String?, consistency: InfluxDB.ConsistencyLevel?, records: String?) {
        TODO("not implemented")
    }

    override fun write(database: String?, retentionPolicy: String?, consistency: InfluxDB.ConsistencyLevel?, records: MutableList<String>?) {
        TODO("not implemented")
    }

    override fun write(udpPort: Int, records: String?) {
        TODO("not implemented")
    }

    override fun write(udpPort: Int, records: MutableList<String>?) {
        TODO("not implemented")
    }

    override fun enableBatch(actions: Int, flushDuration: Int, flushDurationTimeUnit: TimeUnit?) = this

    override fun enableBatch(actions: Int, flushDuration: Int, flushDurationTimeUnit: TimeUnit?, threadFactory: ThreadFactory?): InfluxDB {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun enableBatch(actions: Int, flushDuration: Int, flushDurationTimeUnit: TimeUnit?, threadFactory: ThreadFactory?, exceptionHandler: BiConsumer<MutableIterable<Point>, Throwable>?): InfluxDB {
        batchEnabled = true
        return this
    }

    override fun flush() {}

    override fun setConsistency(consistency: InfluxDB.ConsistencyLevel?) = this

    override fun query(query: Query?): QueryResult {
        TODO("not implemented")
    }

    override fun query(query: Query?, chunkSize: Int, consumer: Consumer<QueryResult>?) {
        TODO("not implemented")
    }

    override fun query(query: Query?, timeUnit: TimeUnit?): QueryResult {
        TODO("not implemented")
    }

    override fun ping() = Pong()

    override fun setDatabase(database: String?): InfluxDB {
        assert(database == "CLOUDIO") { "You can only use database CLOUDIO during unit testing!" }
        return this
    }

    override fun databaseExists(name: String?): Boolean {
        return name == "CLOUDIO"
    }

    override fun setRetentionPolicy(retentionPolicy: String?) = this

    override fun close() {}

    override fun deleteDatabase(name: String?) {
        assert(name == "CLOUDIO") { "You can only use database CLOUDIO during unit testing!" }
        points.clear()
    }

    override fun enableGzip(): InfluxDB {
        gzipEnabled = true
        return this
    }

    override fun isBatchEnabled() = batchEnabled

    override fun createDatabase(name: String?) {
        assert(name == "CLOUDIO") { "You can only use database CLOUDIO during unit testing!" }
    }

    override fun disableGzip(): InfluxDB {
        gzipEnabled = false
        return this
    }
}
