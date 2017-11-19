# cloudio-cloud-microservice [![Build Status](https://travis-ci.org/cloudio-project/cloudio-cloud-microservice.svg?branch=master)](https://travis-ci.org/cloudio-project/cloudio-cloud-microservice)

Cloud.iO cloud microservice base framework.

## How to use
You need to add the dependency to the cloud.iO cloud microservice base framework library to your Kotlin/Java project.
You find at the end of the page the instructions for [gradle](https://gradle.org) or [maven](https://maven.apache.org)
you have to apply to your projects in order to develop cloud.iO microservices.

### Configuration

We use environment variables in order to configure the base AMQP based microservice messaging layer. You might ask
yourself why are we actually using environment variables in order to configure the services? The reason for that is that
they work pretty well for Spring based applications running in docker containers.

The following parameters can be modified:

- **CLOUDIO_SERVICES_CERTIFICATE_FILE** *(Optional)*

  The location to search for the client certificate archive file to present to the AMQP message broker. The format of
  the archive file containing the client certificate has to be of type PKCS12.

  Default value is **file:/certificates/cloudio_services.p12**.

- **CLOUDIO_SERVICES_CERTIFICATE_PASSWORD** *(Optional)*

  If the PKCS12 archive is protected by a password, you can specify the password using this environment variable in
  order to enable the application to access the archive's content.

  Default value is an **empty string** which actually means no password.

- **CLOUDIO_CA_CERTIFICATE_FILE** *(optional)*

  The location to search for the certificate authority keystore file in order to validate the certificate presented by
  the broker during secure handshake. The format of the archive file containing the CA certificate has to be of type
  JKS.

  Default value is **file:/certificates/ca-cert.jks**.

- **CLOUDIO_CA_CERTIFICATE_PASSWORD** *(Required)*

  JKS keystore files have to be protected by a password. Use this environment variable in order to unlock the keystore.

- **CLOUDIO_SSL_PROTOCOL** *(Optional)*

  Protocol to prefer/use for secure connection.

  Default is **TLSv1.2**.

- **CLOUDIO_AMQP_HOST** *(Optional)*

  IP address or DNS name of the host that runs the central cloud.iO message broker.

  Default is **cloudio-broker**.

- **CLOUDIO_AMQP_PORT** *(Optional)*

  TCP port the central cloud.iO message broker is listening to.

  Defaults to **5671** (Standard AMQPS port).

- **CLOUDIO_AMQP_CHANNEL_CACHE_SIZE** *(Optional)*

  The number of channels to maintain in the cache. By default, channels are allocated on demand (unbounded) and this
  represents the maximum cache size.

  Defaults to **8**.

- **CLOUDIO_AMQP_CONCURRENT_CONSUMERS** *(Optional)*

  The default amount of concurrent consumers of each service for the service's queue per node running. This value
  is used for all services for the spring boot application.

  Defaults to the **number of CPUs available on the system**.

- **CLOUDIO_AMQP_MAX_CONCURRENT_CONSUMERS** *(Optional)*.

  The maximal amount of concurrent consumers of each service for the service's queue per node running. This value
  is used for all services for the spring boot application.

  Note that this value **must** be equal or greater than the parameter *CLOUDIO_AMQP_CONCURRENT_CONSUMERS*.

  Defaults to **4 times the number of CPUs available on the system**.

### Getting the framework

**At the moment, only development snapshots are available.**

#### Gradle

```groovie
repositories {
    jcenter()
    maven {
        url "https://oss.jfrog.org/artifactory/list/oss-snapshot-local"
    }
}

dependencies {
    compile(group: 'ch.hevs.cloudio', name: 'cloudio-cloud-microservice', version: '0.1.0-SNAPSHOT')
}
```

#### Maven

```xml
    <repositories>
        <repository>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
          <id>central</id>
          <name>bintray</name>
          <url>http://jcenter.bintray.com</url>
        </repository>
        <repository>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
          <id>JFrog OSS snapshots</id>
          <name>bintray snapshots</name>
           <url>https://oss.jfrog.org/artifactory/list/oss-snapshot-local</url>
         </repository>
    </repositories>

    <dependencies>
       <dependency>
         <groupId>ch.hevs.cloudio</groupId>
         <artifactId>cloudio-cloud-microservice</artifactId>
         <version>0.1.0-SNAPSHOT</version>
         <type>jar</type>
       </dependency>
    </dependencies>
```
