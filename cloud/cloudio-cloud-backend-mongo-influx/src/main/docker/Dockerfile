FROM frolvlad/alpine-oraclejdk8:slim
MAINTAINER Michael Clausen (michael.clausen@hevs.ch)

########################################################################################################################
# Copy Spring Boot application into container.                                                                         #
########################################################################################################################
ADD cloudio-cloud-backend-mongo-influx-0.1.2-SNAPSHOT.jar app.jar
RUN sh -s 'touch /app.jar'

########################################################################################################################
# Certificates have to be provided on volume /certificates.                                                            #                                                                                                                    #
#                                                                                                                      #
# The certificates with the name "ca-cert.jks" and "cloudio_services.p12" have to be located at the  location          #
# /certificates. These certificates will be used by the Spring boot application in order to authenticate the           #
# within the central cloud.iO message broker. If those files do not exist the application will fail to start.          #
#                                                                                                                      #
# The client certificate has to be of type PKCS12 and the trust store (Certificate Authority) must be a JKS keystore.  #
#                                                                                                                      #
# If these certificates are located somwhere else, you can override the file location using the environment variables  #
# CLOUDIO_SERVICES_CERTIFICATE_FILE for the client certificate and CLOUDIO_CA_CERTIFICATE_FILE for the trusted key     #
# store.                                                                                                               #
#                                                                                                                      #
# If these certificates are protected by a password, you can specify the password using the environment variables      #
# CLOUDIO_SERVICES_CERTIFICATE_PASSWORD in order to unlock the client certificate and CLOUDIO_CA_CERTIFICATE_PASSWORD  #
# to unlock the trusted certificates keystore. Note that it is mandatory to protect a JKS file using a password, so    #
# CLOUDIO_CA_CERTIFICATE_PASSWORD must have actually a value.                                                          #
#                                                                                                                      #
# - CLOUDIO_SERVICES_CERTIFICATE_FILE (Optional)                                                                       #
#   Location of the certificate to use in order to connect to the broker. Defaults to                                  #
#   '/certificates/cloudio_services.p12'.                                                                              #
#                                                                                                                      #
# - CLOUDIO_SERVICES_CERTIFICATE_PASSWORD (Optional)                                                                   #
#   Password used to access the service's certificate file. Defaults to '' (empty).                                    #
#                                                                                                                      #
# - CLOUDIO_CA_CERTIFICATE_FILE (Optional)                                                                             #
#   Path to the certificate authority JKS file. Defaults to '/certificates/ca-cert.jks'.                               #
#                                                                                                                      #
# - CLOUDIO_CA_CERTIFICATE_PASSWORD (Required)                                                                         #
#   Password used to access the CA's certificate JKS file.                                                             #
########################################################################################################################
VOLUME /certificates
#ENV CLOUDIO_SERVICES_CERTIFICATE_FILE
#ENV CLOUDIO_SERVICES_CERTIFICATE_PASSWORD
#ENV CLOUDIO_CA_CERTIFICATE_FILE
#ENV CLOUDIO_CA_CERTIFICATE_PASSWORD

########################################################################################################################
# The used encryption can be specified using the environment variable CLOUDIO_SSL_PROTOCOL. Default value is *TLSv1.2" #
#                                                                                                                      #
# - CLOUDIO_SSL_PROTOCOL (Optional)                                                                                    #
#   Used encryption for all SSL sockets. Defaults to 'TLSv1.2'.                                                        #
########################################################################################################################
#ENV CLOUDIO_SSL_PROTOCOL TLSv1.2

########################################################################################################################
# cloud.iO message broker configuration (RabbitMQ).                                                                    #
#                                                                                                                      #
# The following parameters can be used to parametrize the microservice's connection to the central cloud.iO message    #
# broker (AMQP broker/RabbitMQ).                                                                                       #
#                                                                                                                      #
# - CLOUDIO_AMQP_HOST (Optional)                                                                                       #
#   IP address or DNS name of the host that runs the central cloud.iO message broker. Default is cloudio-broker.       #
#                                                                                                                      #
# - CLOUDIO_AMQP_PORT (Optional)                                                                                       #
#   TCP port the central cloud.iO message broker is listening to. Defaults to 5671 (Standard AMQPS port).              #
#                                                                                                                      #
# - CLOUDIO_AMQP_CHANNEL_CACHE_SIZE (Optional)                                                                         #
#   The number of channels to maintain in the cache. By default, channels are allocated on demand (unbounded) and this #
#   represents the maximum cache size. Defaults to 8.                                                                  #
#                                                                                                                      #
# - CLOUDIO_AMQP_CONCURRENT_CONSUMERS (Optional)                                                                       #
#   The default amount of concurrent consumers of each service for the service's queue per node running. This value is #
#   used for all services for the spring boot application. Defaults to the number of CPUs available on the system.     #
#                                                                                                                      #
# - CLOUDIO_AMQP_MAX_CONCURRENT_CONSUMERS (Optional).                                                                  #
#   The maximal amount of concurrent consumers of each service for the service's queue per node running. This value is #
#   used for all services for the spring boot application. Note that this value must be equal or greater than the      #
#   parameter CLOUDIO_AMQP_CONCURRENT_CONSUMERS. Defaults to 4 times the number of CPUs available on the system.       #
########################################################################################################################
#ENV CLOUDIO_AMQP_HOST cloudio-broker
#ENV CLOUDIO_AMQP_PORT 5671
#ENV CLOUDIO_AMQP_CHANNEL_CACHE_SIZE 8
#ENV CLOUDIO_AMQP_CONCURRENT_CONSUMERS
#ENV CLOUDIO_AMQP_MAX_CONCURRENT_CONSUMERS

########################################################################################################################
# MongoDB configuration                                                                                                #
#                                                                                                                      #
# The following parameters can be used to parametrize the microservice's connection to the MongoDB NoSQL database:     #
#                                                                                                                      #
# - CLOUDIO_MONGO_HOST (Optional)                                                                                      #
#   IP address or DNS name of the machine or container that actually runs the MongoDB instance (or cluster node) to    #
#   connect to. Default to "cloudio-mongo".                                                                            #
#                                                                                                                      #
# - CLOUDIO_MONGO_PORT (Optional)                                                                                      #
#   TCP port the MongoDB server is listening to. Default to the standard MongoDB port 27017.                           #
#                                                                                                                      #
# - CLOUDIO_MONGO_DATABASE (Optional)                                                                                  #
#   Name of the database to use in order to save the cloud.iO related documents. Defaults to "CLOUDIO".                #
#                                                                                                                      #
# - CLOUDIO_MONGO_PASSWORD (Optional)                                                                                  #
#   If CLOUDIO_MONGO_PASSWORD is defined, the MongoDB client authenticates during connection setup, otherwise no       #
#   authentication will take place.                                                                                    #
#                                                                                                                      #
# - CLOUDIO_MONGO_USER (Optional)                                                                                      #
#   Username to use in order to authenticate within MongoDB. Defaults to 'cloudio_services'.                           #
########################################################################################################################
#ENV CLOUDIO_MONGO_HOST cloudio-mongo
#ENV CLOUDIO_MONGO_PORT 27017
#ENV CLOUDIO_MONGO_DATABASE CLOUDIO
#ENV CLOUDIO_MONGO_USER
#ENV CLOUDIO_MONGO_PASSWORD

########################################################################################################################
# InfluxDB configuration                                                                                               #
#                                                                                                                      #
# The following parameters can be used to parametrize the microservice's connection to the InfluxSB time series        #
# database:                                                                                                            #
#                                                                                                                      #
# - CLOUDIO_INFLUX_HOST (Optional)                                                                                     #
#   IP address or DNS name of the machine or the container that actually runs the InfluxDB instance (or cluster node)  #
#   to connect to. Default to "cloudio-influx".                                                                        #
#                                                                                                                      #
# - CLOUDIO_INFLUX_PORT (Optional)                                                                                     #
#   TCP port the InfluxDB server is listening to. Default to the standard InfluxDB port 8086.                          #
#                                                                                                                      #
# - CLOUDIO_INFLUX_DATABASE (Optional)                                                                                 #
#   Name of the database to use in order to save the cloud.iO related time series data. Defaults to "CLOUDIO".         #
#                                                                                                                      #
# - CLOUDIO_INFLUX_USER (Optional)                                                                                     #
#   Username to use in order to connect to the InfluxDB time series server. Default user is "cloudio_services".        #
#                                                                                                                      #
# - CLOUDIO_INFLUX_PASSWORD (Optional)                                                                                 #
#   Password to use in order to connect to the InfluxDB time series server. Default to no password.                    #
#                                                                                                                      #
# - CLOUDIO_INFLUX_BATCHSIZE (Optional)                                                                                #
#   If this property is set, the data points are written in batches to the InfluxDB server instead of one by one which #
#   can improve the overall performance dramatically. If not specified, batch mode will not be enabled and points are  #
#   written one by one.                                                                                                #
#                                                                                                                      #
# - CLOUDIO_INFLUX_FLUSHDURATION (Optiona)                                                                             #
#   The time to wait at most before a batch is written to the InfluxDB database even if the batch size has not been    #
#   reached. The parameter is in milliseconds. Defaults to 10000 (10 Seconds).                                         #
########################################################################################################################
#ENV CLOUDIO_INFLUX_HOST cloudio-influx
#ENV CLOUDIO_INFLUX_PORT 8086
#ENV CLOUDIO_INFLUX_DATABASE CLOUDIO
#ENV CLOUDIO_INFLUX_USER cloudio_services
#ENV CLOUDIO_INFLUX_PASSWORD
#ENV CLOUDIO_INFLUX_BATCHSIZE
#ENV CLOUDIO_INFLUX_FLUSHDURATION


ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
