package ch.hevs.cloudio.cloud.microservice

import com.rabbitmq.client.DefaultSaslConfig
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

@Configuration
@EnableRabbit
open class MicroserviceConfiguration() {
    companion object {
        private val log = LoggerFactory.getLogger(MicroserviceConfiguration::class.java)
    }

    @Autowired private lateinit var env: Environment
    @Autowired private lateinit var context: ApplicationContext

    private val clientKeyMangerFactory: KeyManagerFactory by lazy {
        val keyStore = KeyStore.getInstance("PKCS12")
        keyStore.load(context.getResource(env.getProperty("CLOUDIO_SERVICES_CERTIFICATE_FILE",
                "file:/certificates/cloudio_services.p12")).inputStream,
                env.getProperty("CLOUDIO_SERVICES_CERTIFICATE_PASSWORD", "").toCharArray())
        val factory = KeyManagerFactory.getInstance("SunX509")
        factory.init(keyStore, "".toCharArray())
        factory
    }

    private val trustManagerFactory: TrustManagerFactory by lazy {
        val keyStore = KeyStore.getInstance("JKS")
        keyStore.load(context.getResource(env.getProperty("CLOUDIO_CA_CERTIFICATE_FILE",
                "file:/certificates/ca-cert.jks")).inputStream,
                env.getProperty("CLOUDIO_CA_CERTIFICATE_PASSWORD", "").toCharArray())
        val factory = TrustManagerFactory.getInstance("SunX509")
        factory.init(keyStore)
        factory
    }

    @Bean
    open fun sslContext(): SSLContext {
        val sslContext = SSLContext.getInstance(env.getProperty("CLOUDIO_SSL_PROTOCOL", "TLSv1.2"))
        sslContext.init(clientKeyMangerFactory.keyManagers, trustManagerFactory.trustManagers, null)
        return sslContext
    }

    @Bean
    open fun connectionFactory(): ConnectionFactory {
        val factory = com.rabbitmq.client.ConnectionFactory()
        factory.host = env.getProperty("CLOUDIO_AMQP_HOST", "cloudio-broker")
        factory.virtualHost = "/"
        factory.port = env.getProperty("CLOUDIO_AMQP_PORT", Int::class.java, 5671)
        factory.saslConfig = DefaultSaslConfig.EXTERNAL
        factory.useSslProtocol(context.getBean(SSLContext::class.java))
        val cachingFactory = CachingConnectionFactory(factory)
        cachingFactory.channelCacheSize = env.getProperty("CLOUDIO_AMQP_CHANNEL_CACHE_SIZE", Int::class.java, 8)
        return cachingFactory
    }

     @Bean
    open fun rabbitListenerContainerFactory(): SimpleRabbitListenerContainerFactory {
        val cores = Runtime.getRuntime().availableProcessors()
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(context.getBean(ConnectionFactory::class.java))
        factory.setConcurrentConsumers(env.getProperty("CLOUDIO_AMQP_CONCURRENT_CONSUMERS", Int::class.java, cores))
        factory.setMaxConcurrentConsumers(env.getProperty("CLOUDIO_AMQP_MAX_CONCURRENT_CONSUMERS", Int::class.java,
                4 * cores))
        return factory;
    }
}