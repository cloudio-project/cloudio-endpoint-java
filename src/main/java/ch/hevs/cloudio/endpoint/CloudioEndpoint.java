package ch.hevs.cloudio.endpoint;

import ch.hevs.utils.ResourceLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.*;

/**
 * An Endpoint is the root object of any connection of a device or a gateway to cloud.io. The parameters of the
 * Endpoint can be either passed to the constructor as simple Java Properties or they can be present in a Java
 * Properties file at the following locations (The Properties files are searched in the order of listing):
 * <br><br>
 * <ul>
 *     <li>~/.config/cloud.io/{Endpoint UUID}.properties on the local file system.</li>
 *     <li>/etc/cloud.io/{Endpoint UUID}.properties on the local file system.</li>
 *     <li>cloud.io/{Endpoint UUID}.properties inside the application bundle.</li>
 * </ul>
 * <br><br>
 * <b>Specifying resource locations:</b><br>
 * When you specify a location for any configuration file or key/certificate file or any other kind of file, you need
 * to use URIs to specify the file location. The following URI schemes are supported:
 * <ul>
 *     <li>
 *         <b>classpath:</b><br>
 *         The file is located in the classpath (Either in the JAR file or inside the same OSGi Bundle).
 *     </li>
 *     <li>
 *         <b>file:</b>
 *         The file is located on the local file system. An absolute path to the file has to be given.
 *     </li>
 *     <li>
 *         <b>home:</b>
 *         The file is located in the home directory of the actual user. The given path is relative to the user's home
 *         directory.
 *     </li>
 *     <li>
 *         <b>http:</b>
 *         The resource (file) is located on an HTTP server.
 *     </li>
 * </ul>
 * <br><br>
 * The properties configure all aspects of the communication between the endpoint and the cloud. Here the list of the
 * supported properties:
 * <br><br>
 * <ul>
 *     <li>
 *         <b>ch.hevs.cloudio.endpoint.hostUri</b><br>
 *         URI of the cloud.io broker URI to connect to. An example might be "ssl://example.org:8883". Note that two
 *         schemes of connections are supported, "tcp" for non secure connections and "ssl" for secure connections.
 *         This property is <b>mandatory.</b>
 *     </li>
 *     <li>
 *         <b>ch.hevs.cloudio.endpoint.connectTimeout</b><br>
 *         Timeout in seconds to use when trying to connect to the cloud.io message broker. The default value is <b>5
 *         seconds</b>.
 *     </li>
 *     <li>
 *         <b>ch.hevs.cloudio.endpoint.connectRetryInterval</b><br>
 *         Interval in seconds to start a reconnect try after a connect failure. The default value is <b>10 seconds</b>.
 *         If the value is 0, no connection retry has will be done at all once the connection is lost or could not
 *         be established.
 *     </li>
 *     <li>
 *         <b>ch.hevs.cloudio.endpoint.keepAliveInterval</b><br>
 *         Interval at which the client exchanges messages with the server in order to check that the connection is
 *         still alive and in order to guarantee that NAT port mappings remain. Default is <b>60 seconds</b>.
 *     </li>
 *     <li>
 *         <b>ch.hevs.cloudio.endpoint.persistence</b><br>
 *         This option configures which persistence to use in order to save temporary data. Three options are possible:
 *         <br>
 *         <ul>
 *             <li>
 *                 <b>memory</b><br>
 *                 The temporary data is saved into memory. With this option, pending messages (data) can be lost
 *                 if the application is restarted. It is not advisable to use this persistence implementation in
 *                 productive environments.
 *             </li>
 *             <li>
 *                 <b>file</b><br>
 *                 The temporary data is saved onto the file system. Using this option, pending messages (data) will
 *                 not be lost during a restart of the application if not a clean session is forced using the option
 *                 ch.hevs.cloudio.endpoint.cleanSession.
 *             </li>
 *             <li>
 *                 <b>none</b><br>
 *                 No Persistence is used at all. Using this persistence, messages (data) will be almost certain be
 *                 lost, but if your application can handle such losses and you want to use as less as resources as
 *                 possible, this might be a solution.
 *             </li>
 *         </ul>
 *         This property is optional and the default is <b>file</b>.
 *     </li>
 *     <li>
 *         <b>ch.hevs.cloudio.endpoint.ssl.clientCert</b><br>
 *         Path to the client certificate file. The certificate file must be encoded in the PKCS12 key format and it
 *         needs to contain the client's certificate and the client's private key. The file has additionally to be
 *         encrypted and protected using a password. The password to use to decrypt the file is specified using the
 *         property <b>ch.hevs.cloudio.endpoint.ssl.clientPassword</b>.<br>
 *         This property is optional and if the property is not set, the endpoint searches these locations in the
 *         given order for the JKCS12 file:
 *         <ul>
 *             <li>~/.config/cloud.io/{Endpoint UUID}.p12 on the local file system.</li>
 *             <li>/etc/cloud.io/{Endpoint UUID}.p12 on the local file system.</li>
 *             <li>cloud.io/{Endpoint UUID}.p12 inside the application bundle (classpath).</li>
 *         </ul>
 *     </li>
 *     <li>
 *         <b>ch.hevs.cloudio.endpoint.ssl.clientPassword</b><br>
 *         Password to unlock the PKCS12 archive containing the endpoint's certificate and private key. The PCKS12 file
 *         is specified using the <b>ch.hevs.cloudio.endpoint.ssl.clientCert</b> property. Note that it is not mandatory
 *         to encrypt the PKCS12 files.<br>
 *         Optional, defaults to "" which means no password at all.
 *     </li>
 *     <li>
 *         <b>ch.hevs.cloudio.endpoint.ssl.authorityCert</b><br>
 *         The path to the certificate of the certification authority embedded into a JKS file.
 *     </li>
 *     <li>
 *         <b>ch.hevs.cloudio.endpoint.ssl.authorityPassword</b><br>
 *         Password to unlock the JKS archive containing the certification authority's certificate. The JKS file
 *         is specified using the <b>ch.hevs.cloudio.endpoint.ssl.authorityCert</b> property.<br>
 *         Optional, defaults to "" which means no password at all.
 *     </li>
 *     <li>
 *         <b>ch.hevs.cloudio.endpoint.ssl.protocol</b><br>
 *         Specifies the SSL protocol to use. Possible values depend on the actual SSL implementation. Most commonly
 *         supported are:
 *         <ul>
 *             <li>SSL</li>
 *             <li>SSLv2</li>
 *             <li>SSLv3</li>
 *             <li>TLS</li>
 *             <li>TLSv1</li>
 *             <li>TLSv1.1</li>
 *             <li>TLSv1.2</li>
 *         </ul>
 *         This property is optional and defaults to TLSv1.2.
 *     </li>
 *     <li>
 *         <b>ch.hevs.cloudio.endpoint.messageFormat</b><br>
 *         The message format used to communicate within cloud.io. Currently supported data formats are:
 *         <ul>
 *             <li>json: JSON Format.</li>
 *             <li>json+zip: Compressed JSON.</li>
 *         </ul>
 *         If this property is not set, the default "json" will be used.
 *         Note that it is important that all endpoints and applications in the same cloud.io installation use the very
 *         same message format, otherwise they will not be able to communicate with each other.
 *     </li>
 *     <li>
 *         <b>ch.hevs.cloudio.endpoint.cleanSession</b><br>
 *         This property can be either "true" or "false". If it is true, a clean MQTT session is established with the
 *         central broker. This means that pending messages from a previous session will be discarded. If it is "false"
 *         which is the default, pending messages from previous sessions will be send to the server upon the connection
 *         is established.
 *     </li>
 * </ul>
 */
public class CloudioEndpoint implements CloudioEndpointService {
    private static final Logger log = LogManager.getLogger(CloudioEndpoint.class);
    final InternalEndpoint internal;

    /**
     * Constructs a new Endpoint object using the given UUID. As no properties are given using this constructor, the
     * properties are loaded from the file system or the actual bundle itself.
     *
     * The Java Properties file at the following locations (The Properties files are searched in the order of listing)
     * are used:
     * <ul>
     *     <li>~/.config/cloud.io/{Endpoint UUID}.properties on the local file system.</li>
     *     <li>/etc/cloud.io/{Endpoint UUID}.properties on the local file system.</li>
     *     <li>~{Endpoint UUID}.properties inside the application bundle (classpath).</li>
     * </ul>
     *
     * The endpoint will try immediately to connect to the central message broker and tries automatically to maintain
     * this connection in the background.
     *
     * @param uuid                              Unique ID of the endpoint.
     * @throws InvalidUuidException             If the given UUID is invalid.
     * @throws InvalidPropertyException         Either a mandatory property is missing or a property has an invalid
     *                                          value.
     * @throws CloudioEndpointInitializationException  The endpoint could not be initialized. This might be caused by invalid
     *                                          parameters, invalid certificates or any other runtime errors.
     */
    public CloudioEndpoint(String uuid) throws InvalidUuidException, InvalidPropertyException,
        CloudioEndpointInitializationException {
        // Call internal designated constructor with empty properties reference.
        internal = new InternalEndpoint(uuid, null, null);
    }

    /**
     * Constructs a new CloudioEndpoint object using the given UUID and properties. The endpoint will try immediately to
     * connect to the central message broker and tries automatically to maintain this connection in the background.
     *
     * @param uuid                              Unique ID of the endpoint.
     * @param properties                        Properties containing the endpoint configuration parameters.
     * @throws InvalidUuidException             If the given UUID is invalid.
     * @throws InvalidPropertyException         Either a mandatory property is missing or a property has an invalid
     *                                          value.
     * @throws CloudioEndpointInitializationException  The endpoint could not be initialized. This might be caused by invalid
     *                                          parameters, invalid certificates or any other runtime errors.
     */
    public CloudioEndpoint(String uuid, Properties properties)
            throws InvalidUuidException, InvalidPropertyException, CloudioEndpointInitializationException {
        internal = new InternalEndpoint(uuid, new PropertiesEndpointConfiguration(properties), null);
    }

    /**
     * Constructs a new CloudioEndpoint object using the given UUID and properties. The endpoint will try immediately to
     * connect to the central message broker and tries automatically to maintain this connection in the background.
     * The given endpoint listener receives updates about the state of the endpoint.
     *
     * @param uuid                              Unique ID of the endpoint.
     * @param properties                        Properties containing the endpoint configuration parameters.
     * @param listener                          Reference to the listener receiving status updates.
     * @throws InvalidUuidException             If the given UUID is invalid.
     * @throws InvalidPropertyException         Either a mandatory property is missing or a property has an invalid
     *                                          value.
     * @throws CloudioEndpointInitializationException  The endpoint could not be initialized. This might be caused by invalid
     *                                          parameters, invalid certificates or any other runtime errors.
     */
    public CloudioEndpoint(String uuid, Properties properties, CloudioEndpointListener listener)
            throws InvalidUuidException, InvalidPropertyException, CloudioEndpointInitializationException {
        internal = new InternalEndpoint(uuid, new PropertiesEndpointConfiguration(properties), listener);
    }

    /**
     * Designated constructor. Uses the {@link CloudioEndpointConfiguration} interface to read the endpoint configuration
     * options.
     *
     * @param uuid                              Unique ID of the endpoint.
     * @param configuration                     Configuration object containing the endpoint configuration parameters.
     * @throws InvalidUuidException             If the given UUID is invalid.
     * @throws InvalidPropertyException         Either a mandatory property is missing or a property has an invalid
     *                                          value.
     * @throws CloudioEndpointInitializationException  The endpoint could not be initialized. This might be caused by invalid
     *                                          parameters, invalid certificates or any other runtime errors.
     */
    CloudioEndpoint(String uuid, CloudioEndpointConfiguration configuration) throws InvalidUuidException, InvalidPropertyException,
        CloudioEndpointInitializationException {
        internal = new InternalEndpoint(uuid, configuration, null);
    }

    public void close() {
        internal.close();
    }

    @Override
    public boolean isOnline() {
        return internal.mqtt.isConnected();
    }

    @Override
    public void addEndpointListener(CloudioEndpointListener listener) {
        if (listener != null) {
            internal.listeners.add(listener);
        }
     }

    @Override
    public void removeEndpointListener(CloudioEndpointListener listener) {
        internal.listeners.remove(listener);
    }

    @Override
    public void addNode(String nodeName, CloudioNode node) throws DuplicateNamedItemException {
        if (nodeName != null && node != null) {
            // Add node to endpoint.
            node.internal.setName(nodeName);
            node.internal.setParentNodeContainer(this.internal);
            internal.nodes.addItem(node.internal);

            // If the endpoint is online, send node add message.
            if (isOnline()) {
                try {
                    byte[] data = internal.messageFormat.serializeNode(node.internal);
                    internal.mqtt.publish("@nodeAdded/" + node.internal.getUuid(), data, 1, false);
                } catch (MqttException exception) {
                    log.error("Exception: " + exception.getMessage());
                    exception.printStackTrace();
                }
            }
        }
    }

    @Override
    public <T extends CloudioNode> T addNode(String nodeName, Class<T> nodeClass)
            throws InvalidCloudioNodeException, DuplicateNamedItemException {
        if (nodeName != null && nodeClass != null) {
            try {
                // Create node instance.
                T node = nodeClass.newInstance();

                // Add the node.
                addNode(nodeName, node);

                // Return reference to the new node instance.
                return node;
            } catch (InstantiationException exception) {
                throw new InvalidCloudioNodeException();
            } catch (IllegalAccessException exception) {
                throw new InvalidCloudioNodeException();
            }
        }

        return null;
    }

    @Override
    public void removeNode(CloudioNode node) {
        if (node != null && internal.nodes.contains(node.internal)) {
            // Remove parent from node.
            node.internal.setParentNodeContainer(null);

            // If the endpoint is online, send the node remove message.
            if (isOnline()) {
                try {
                    internal.mqtt.publish("@nodeRemoved/" + node.internal.getUuid(), null, 1, false);
                } catch (MqttException exception) {
                    log.error("Exception: " + exception.getMessage());
                    exception.printStackTrace();
                }
            }

            // Remove the node from the child nodes list.
            internal.nodes.removeItem(node.internal);
        }
    }

    @Override
    public void removeNode(String nodeName) {
        if (nodeName != null) {
            CloudioNode.InternalNode internalNode = internal.nodes.getItem(nodeName);
            if (internalNode != null) {
                // Remove parent from node.
                internalNode.setParentNodeContainer(null);

                // If the endpoint is online, send the node remove message.
                if (isOnline()) {
                    try {
                        internal.mqtt.publish("@nodeRemoved/" + internalNode.getUuid(), null, 1, false);
                    } catch (MqttException exception) {
                        log.error("Exception: " + exception.getMessage());
                        exception.printStackTrace();
                    }
                }
            }

            // Remove the node from the child nodes list.
            internal.nodes.removeItem(internalNode);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CloudioNode> T getNode(String nodeName) throws ClassCastException {
        return (T)internal.nodes.getItem(nodeName).getExternalNode();
    }

    /*** Internal API *************************************************************************************************/
    class InternalEndpoint implements CloudioNodeContainer, MqttCallback, Runnable {
        /*** Constants ************************************************************************************************/
        private static final String MQTT_HOST_URI_PROPERTY          = "ch.hevs.cloudio.endpoint.hostUri";
        private static final String MQTT_CONNECTION_TIMEOUT_PROPERTY= "ch.hevs.cloudio.endpoint.connectTimeout";
        private static final String MQTT_CONNECTION_TIMEOUT_DEFAULT = "5";
        private static final String MQTT_CONNECT_RETRY_PROPERTY     = "ch.hevs.cloudio.endpoint.connectRetryInterval";
        private static final String MQTT_CONNECT_RETRY_DEFAULT      = "10";
        private static final String MQTT_KEEPALIVE_INTERVAL_PROPERTY= "ch.hevs.cloudio.endpoint.keepAliveInterval";
        private static final String MQTT_KEEPALIVE_INTERVAL_DEFAULT = "60";
        private static final String MQTT_MAXINFLIGHT_PROPERTY		= "ch.hevs.cloudio.endpoint.maxInFlight";
        private static final String MQTT_MAXINFLIGHT_DEFAULT		= "1000";
        private static final String MQTT_PERSISTENCE_MEMORY         = "memory";
        private static final String MQTT_PERSISTENCE_FILE           = "file";
        private static final String MQTT_PERSISTENCE_NONE           = "none";
        private static final String MQTT_PERSISTENCE_PROPERTY       = "ch.hevs.cloudio.endpoint.persistence";
        private static final String MQTT_PERSISTENCE_DEFAULT        = MQTT_PERSISTENCE_FILE;
        private static final String ENDPOINT_IDENTITY_FILE_TYPE     = "PKCS12";
        private static final String ENDPOINT_IDENTITY_MANAGER_TYPE  = "SunX509";
        private static final String ENDPOINT_IDENTITY_FILE_PROPERTY = "ch.hevs.cloudio.endpoint.ssl.clientCert";
        private static final String ENDPOINT_IDENTITY_PASS_PROPERTY = "ch.hevs.cloudio.endpoint.ssl.clientPassword";
        private static final String ENDPOINT_IDENTITY_PASS_DEFAULT  = "";
        private static final String CERT_AUTHORITY_FILE_TYPE        = "JKS";
        private static final String CERT_AUTHORITY_MANAGER_TYPE     = "SunX509";
        private static final String CERT_AUTHORITY_FILE_PROPERTY    = "ch.hevs.cloudio.endpoint.ssl.authorityCert";
        private static final String CERT_AUTHORITY_FILE_DEFAULTNAME = "authority.jks";
        private static final String CERT_AUTHORITY_PASS_PROPERTY    = "ch.hevs.cloudio.endpoint.ssl.authorityPassword";
        private static final String CERT_AUTHORITY_PASS_DEFAULT     = "";
        private static final String SSL_PROTOCOL_PROPERTY           = "ch.hevs.cloudio.endpoint.ssl.protocol";
        private static final String SSL_PROTOCOL_DEFAULT            = "TLSv1.2";
        private static final String MESSAGE_FORMAT                  = "ch.hevs.cloudio.endpoint.messageFormat";
        private static final String MESSAGE_FORMAT_DEFAULT          = "json";
        private static final String MQTT_CLEAN_SESSION_PROPERTY     = "ch.hevs.cloudio.endpoint.cleanSession";
        private static final String MQTT_CLEAN_SESSION_DEFAULT      = "false";
        private static final String ENDPOINT_JOBS_SCRIPT_FOLDER     = "ch.hevs.cloudio.endpoint.jobs.folder";

        /*** Attributes ***********************************************************************************************/
        private final String uuid;
        private final NamedItemSet<CloudioNode.InternalNode> nodes = new NamedItemSet<CloudioNode.InternalNode>();
        private final MqttConnectOptions options;
        private int retryInterval;
        private final MqttAsyncClient mqtt;
        private final MqttClientPersistence persistence;
        private final CloudioMessageFormat messageFormat;
        private final List<CloudioEndpointListener> listeners = new LinkedList<CloudioEndpointListener>();
        private String jobsFilePath;
        private CloudioPersistentData cloudioPersistentData;
        private File cloudioPersistentDataPath;

        public InternalEndpoint(String uuid, CloudioEndpointConfiguration configuration, CloudioEndpointListener listener)
                throws InvalidUuidException, InvalidPropertyException, CloudioEndpointInitializationException {

            // The ID has to be a valid string!
            if (uuid == null) {
                throw new InvalidUuidException("Uuid can not be null!");
            }

            // Set the UUID.
            this.uuid = uuid;

            // Add the listener if present.
            if (listener != null) {
                this.listeners.add(listener);
            }

            // Do we need to load the properties from a file?
            if (configuration == null) {
                Properties properties = new Properties();
                try {
                    InputStream propertiesInputStream = ResourceLoader.getResourceFromLocations(uuid + ".properties",
                            this,
                            "home:" + "/.config/cloud.io/",
                            "file:/etc/cloud.io/",
                            "classpath:cloud.io/");
                    properties.load(propertiesInputStream);
                    configuration = new PropertiesEndpointConfiguration(properties);
                } catch (Exception exception) {
                    throw new InvalidPropertyException("CloudioEndpoint properties missing: No properties given as " +
                            "argument to constructor and no properties file found " +
                            "[\"home:/.config/cloud.io/" + uuid + ".properties\", " +
                            "\"file:/etc/cloud.io/" + uuid + ".properties\", " +
                            "\"classpath:" + uuid + ".properties\"].");
                }
            }

            // Create message format instance.
            String messageFormatId = configuration.getProperty(MESSAGE_FORMAT, MESSAGE_FORMAT_DEFAULT);
            if ("json".equals(messageFormatId)) {
                messageFormat = new JsonMessageFormat();
            } else if ("json+zip".equals(messageFormatId)) {
                messageFormat = new JsonZipMessageFormat();
            } else {
                throw new InvalidPropertyException("Unknown message format (ch.hevs.cloudio.endpoint.messageFormat): " +
                        "\"" + messageFormatId + "\"");
            }

            // Create a SSL based MQTT option object.
            options = new MqttConnectOptions();
            try {
                options.setSocketFactory(createSocketFactory(uuid, configuration));
            } catch (Exception exception) {
                throw new CloudioEndpointInitializationException(exception);
            }

            // Get retry interval.
            try {
                retryInterval = Integer.parseInt(configuration.getProperty(MQTT_CONNECT_RETRY_PROPERTY,
                    MQTT_CONNECT_RETRY_DEFAULT));
                if (retryInterval <= 0) {
                    throw new InvalidPropertyException("Invalid connect retry interval " +
                        "(ch.hevs.cloudio.endpoint.connectRetryInterval), " +
                        "must be a greater than 0");
                }
            } catch (NumberFormatException exception) {
                throw new InvalidPropertyException("Invalid connect retry interval " +
                    "(ch.hevs.cloudio.endpoint.connectRetryInterval), " +
                    "must be a valid integer number");
            }

            // Do we start a clean session?
            String cleanSession = configuration.getProperty(MQTT_CLEAN_SESSION_PROPERTY, MQTT_CLEAN_SESSION_DEFAULT)
                    .toLowerCase();
            if ("true".equals(cleanSession)) {
                options.setCleanSession(true);
            } else if ("false".equals(cleanSession)) {
                options.setCleanSession(false);
            } else {
                throw new InvalidPropertyException("Clean session parameter (ch.hevs.cloudio.endpoint.cleanSession), " +
                        "must either be \"true\" or \"false\"");
            }

            // Get the connection timeout property.
            try {
                options.setConnectionTimeout(Integer.parseInt(
                    configuration.getProperty(MQTT_CONNECTION_TIMEOUT_PROPERTY, MQTT_CONNECTION_TIMEOUT_DEFAULT)));
            } catch (NumberFormatException e) {
                throw new InvalidPropertyException("Invalid connect timeout " +
                        "(ch.hevs.cloudio.endpoint.connectTimeout), " +
                        "must be a valid integer number");
            }

            // Get the keep alive interval property.
            try {
                options.setKeepAliveInterval(Integer.parseInt(
                    configuration.getProperty(MQTT_KEEPALIVE_INTERVAL_PROPERTY, MQTT_KEEPALIVE_INTERVAL_DEFAULT)));
            } catch (NumberFormatException exception) {
                throw new InvalidPropertyException("Invalid keep alive interval " +
                        "(ch.hevs.cloudio.endpoint.keepAliveInterval), " +
                        "must be a valid integer number");
            }
            
            // Get the maxInFlight property.
            try {
                options.setMaxInflight(Integer.parseInt(
                    configuration.getProperty(MQTT_MAXINFLIGHT_PROPERTY, MQTT_MAXINFLIGHT_DEFAULT)));
            } catch (NumberFormatException exception) {
                throw new InvalidPropertyException("Invalid max in flight messages" +
                        "(ch.hevs.cloudio.endpoint.maxInFlight), " +
                        "must be a valid integer number");
            }

            // Create persistence object.
            String persistenceProvider = configuration.getProperty(MQTT_PERSISTENCE_PROPERTY, MQTT_PERSISTENCE_DEFAULT);
            if (persistenceProvider.equals(MQTT_PERSISTENCE_MEMORY)) {
                persistence = new MemoryPersistence();
            } else if (persistenceProvider.equals(MQTT_PERSISTENCE_FILE)) {
                persistence = new MqttDefaultFilePersistence();
            } else if (persistenceProvider.equals(MQTT_PERSISTENCE_NONE)) {
                persistence = null;
            } else {
                throw new InvalidPropertyException("Unknown persistence implementation " +
                        "(ch.hevs.cloudio.endpoint.persistence): " +
                        "\"" + persistenceProvider + "\"");
            }

            // Last will is a message with the UUID of the endpoint and no payload.
            options.setWill("@offline/" + uuid, new byte[0], 1, false);


            // Create the MQTT client.
            try {
                String host = configuration.getProperty(MQTT_HOST_URI_PROPERTY);
                if (host == null) {
                    throw new InvalidPropertyException("Missing mandatory property \"" + MQTT_HOST_URI_PROPERTY + "\"");
                }
                mqtt = new MqttAsyncClient(configuration.getProperty(MQTT_HOST_URI_PROPERTY), uuid, persistence);
            } catch (MqttException exception) {
                throw new CloudioEndpointInitializationException(exception);
            }

            //Get folder path for Jobs.
            if (configuration.containsKey(ENDPOINT_JOBS_SCRIPT_FOLDER)) {
                jobsFilePath = configuration.getProperty(ENDPOINT_JOBS_SCRIPT_FOLDER);
            } else {
                jobsFilePath = "etc/cloud.io";
            }

            //Initialize the cloudioPersistentData file
            ObjectMapper objectMapper = new ObjectMapper();
            String homePath = System.getProperty("user.home");
            cloudioPersistentDataPath = new File(homePath+"/.config/cloudio/CloudioPersistentData.json");
            try {
                //if file doesn't exist, create it and initialize it
                if(!cloudioPersistentDataPath.exists()){
                    new File(homePath+"/.config/cloudio").mkdir();
                    cloudioPersistentDataPath.createNewFile();
                    cloudioPersistentData = new CloudioPersistentData("DEBUG");
                    objectMapper.writeValue(cloudioPersistentDataPath, cloudioPersistentData);
                }else {
                    cloudioPersistentData = objectMapper.readValue(cloudioPersistentDataPath, CloudioPersistentData.class);
                    Level log4jLevel = Level.getLevel(cloudioPersistentData.getLevel());
                    Configurator.setRootLevel(log4jLevel);
                }

            }catch(Exception exception){
                throw new CloudioEndpointInitializationException(exception);
            }

            //Create the CloudioLogAppender and give the mqtt object to it
            org.apache.logging.log4j.core.Logger coreLogger =
                    (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();

            CloudioLogAppender cloudioLogAppender = new CloudioLogAppender("CloudioLogAppender", null);
            cloudioLogAppender.setAppenderMqttParameters(mqtt, uuid, messageFormat);

            coreLogger.addAppender(cloudioLogAppender);
            cloudioLogAppender.start();

            // Start the connection process in a detached thread.
            new Thread(this).start();
        }

        /*** NodeContainer Implementation *****************************************************************************/
        @Override
        public void attributeHasChangedByEndpoint(CloudioAttribute.InternalAttribute attribute) {
            // Create the MQTT message using the given message format.
            byte[] data = messageFormat.serializeAttribute(attribute);

            // Try to send the message if the MQTT client is connected.
            boolean messageSend = false;
            if (mqtt.isConnected()) {
                try {
                    mqtt.publish("@update/" + attribute.getUuid().toString(), data, 1, true);
                    messageSend = true;
                } catch (MqttException exception) {
                    log.error("Exception :" + exception.getMessage());
                    exception.printStackTrace();
                }
            }

            // If the message could not be send for any reason, add the message to the pending updates persistence if
            // available.
            if (!messageSend && persistence != null) {
                try {
                    persistence.put("PendingUpdate-" + attribute.getUuid().toString().replace("/", ";")
                            + "-" + Calendar.getInstance().getTimeInMillis(),
                        new PendingUpdate(data));
                } catch (MqttPersistenceException exception) {
                    log.error("Exception :" + exception.getMessage());
                    exception.printStackTrace();
                }
            }
        }

        @Override
        public void attributeHasChangedByCloud(CloudioAttribute.InternalAttribute attribute) {
            attributeHasChangedByEndpoint(attribute);
        }

        /*** UniqueIdentifiable Implementation ************************************************************************/
        @Override
        public Uuid getUuid() {
            return new TopicUuid(this);
        }

        /*** NamedItem Implementation *********************************************************************************/
        @Override
        public String getName() {
            return uuid;
        }

        @Override
        public void setName(String name) {
            throw new CloudioModificationException("CloudioEndpoint name can not be changed!");
        }

        /*** MqttCallback implementation ******************************************************************************/
        @Override
        public void connectionLost(Throwable throwable) {
            for (CloudioEndpointListener listener: listeners) {
                listener.endpointIsOffline(CloudioEndpoint.this);
            }

            // Start thread that tries to reestablish the connection if needed.
            if (retryInterval != 0) {
                new Thread(this).start();
            }
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            try {
                byte[] data = message.getPayload();

                // First determine the message format (first byte identifies the message format).
                CloudioMessageFormat messageFormat = CloudioMessageFormatFactory.massageFormat(message.getPayload()[0]);
                if (messageFormat == null) {
                    log.error("Message-format " + (int)message.getPayload()[0] + " not supported!");
                    return;
                }

                // Create attribute location path stack.
                Stack<String> location = new Stack<String>();
                String[] topics = topic.split("/");
                for (int i = topics.length - 1; i >= 0; --i) {
                    location.push(topics[i]);
                }

                // Read the action tag from the topic.
                String action = location.peek();
                if ("@set".equals(action)) {
                    location.pop();
                    set(topic, location, messageFormat, data);
                }
                else if("@exec".equals(action)){
                    JobsParameter jobsParameter = new JobsParameter();

                    messageFormat.deserializeJobsParameter(data, jobsParameter);

                    JobsManager.getInstance().executeJob(jobsParameter.getJobURI(),jobsFilePath,
                            jobsParameter.getCorrelationID(), jobsParameter.getSendOutput(),
                            internal.mqtt, messageFormat, internal.uuid);

                }
                else if("@logsLevel".equals(action)){
                    LogParameter logParameter = new LogParameter();
                    messageFormat.deserializeLogParameter(data, logParameter);

                    try{
                        Level log4jLevel = Level.getLevel(logParameter.getLevel());
                        Configurator.setRootLevel(log4jLevel);
                        cloudioPersistentData.setLevel(log4jLevel.toString());

                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.writeValue(cloudioPersistentDataPath, cloudioPersistentData);
                    }catch (Exception e){
                        log.error("Level \"" + logParameter.getLevel() + "\" not supported!");
                        e.printStackTrace();
                    }
                }
                else {
                    log.error("Method \"" + location.pop() + "\" not supported!");
                }
            } catch (Exception exception) {
                log.error("Exception: " + exception.getMessage());
                exception.printStackTrace();
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            // Does not matter.
        }

        /*** Runnable implementation **********************************************************************************/
        @Override
        public void run() {
            mqtt.setCallback(null);

            // As long as we are not connected, try to establish a connection to the broker.
            while (!mqtt.isConnected()) {
                try {
                    // Try to connect to the broker.
                    IMqttToken token = mqtt.connect(options, null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken iMqttToken) {
                            try {
                                // Send birth message.
                                mqtt.publish("@online/" + internal.uuid,
                                    messageFormat.serializeEndpoint(InternalEndpoint.this), 1, true);

                                // Subscribe to all set commands.
                                mqtt.subscribe("@set/" + internal.uuid + "/#", 1);
                                // Subscribe to all exec.
                                mqtt.subscribe("@exec/" + internal.uuid , 1);
                                // Subscribe to all logsLevel
                                mqtt.subscribe("@logsLevel/" + internal.uuid , 1);

                                // Send all saved updates on update topic.
                                if (persistence != null) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                @SuppressWarnings("unchecked")
                                                Enumeration<String> keyEnum = persistence.keys();
                                                while (mqtt.isConnected() && keyEnum.hasMoreElements()) {
                                                    String key = keyEnum.nextElement();

                                                    // Is it a pending update?
                                                    if (key.startsWith("PendingUpdate-")) {

                                                        // Get the pending update persistent object from store.
                                                        MqttPersistable pendingUpdate = persistence.get(key);
                                                        String uuid = key.substring(14, key.lastIndexOf("-")).replace(";", "/");

                                                        // Try to send the update to the broker and remove it from the storage.
                                                        try {
                                                            mqtt.publish("@update/" + uuid,
                                                                pendingUpdate.getHeaderBytes(), 1, true);
                                                            persistence.remove(key);
                                                        } catch (MqttException exception) {
                                                            log.error("Exception: " + exception.getMessage());
                                                            exception.printStackTrace();
                                                        }

                                                        try {
                                                            Thread.sleep(100);
                                                        } catch (InterruptedException exception) {
                                                            log.error("Exception: " + exception.getMessage());
                                                            exception.printStackTrace();
                                                        }
                                                    }
                                                }
                                            } catch (MqttPersistenceException exception) {
                                                log.error("Exception: " + exception.getMessage());
                                                exception.printStackTrace();
                                            }

                                        }
                                    }).start();
                                }
                            } catch (MqttException exception) {
                                log.error("Exception: " + exception.getMessage());
                                exception.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                            log.error("Exception: " + throwable.getMessage());
                            throwable.printStackTrace();
                        }
                    });

                    // Wait for connect or error...
                    token.waitForCompletion();

                } catch (MqttException exception) {
                    log.error("Exception during connect:", exception);
                }

                // If the connection could not be established, sleep a moment before the next try.
                if (!mqtt.isConnected()) {
                    // If we should not retry, give up.
                    if (retryInterval == 0) return;

                    // Wait the retry interval.
                    try {
                        Thread.sleep(1000 * retryInterval);
                    } catch (InterruptedException exception) {
                        log.error("Exception: " + exception.getMessage());
                        exception.printStackTrace();
                    }

                    // Again, if we should not retry, give up.
                    if (retryInterval == 0) return;
                }
            }

            // If we arrive here, we are online, so we can inform listeners about that and stop the connecting thread.
            mqtt.setCallback(this);
            for (CloudioEndpointListener listener: listeners) {
                listener.endpointIsOnline(CloudioEndpoint.this);
            }
        }

        /*** Package private methods **********************************************************************************/
        public List<CloudioNode.InternalNode> getNodes() {
            return nodes.toList();
        }

        /*** Private methods ******************************************************************************************/
        private SSLSocketFactory createSocketFactory(String endpointUuid, CloudioEndpointConfiguration properties)
            throws Exception {
            // Endpoint identity (Key & Certificate) in single PKCS #12 archive file named with the actual Endpoint ID.
            KeyStore endpointKeyCertStore = KeyStore.getInstance(ENDPOINT_IDENTITY_FILE_TYPE);

            // If the key file is present in settings, use it to load the identity file.
            if (properties.containsKey(ENDPOINT_IDENTITY_FILE_PROPERTY)) {
                endpointKeyCertStore.load(ResourceLoader.getResource(
                                properties.getProperty(ENDPOINT_IDENTITY_FILE_PROPERTY), this),
                        properties.getProperty(ENDPOINT_IDENTITY_PASS_PROPERTY,
                                ENDPOINT_IDENTITY_PASS_DEFAULT).toCharArray());

            // If the key file is not given, try to load from default locations.
            } else {
                endpointKeyCertStore.load(ResourceLoader.getResourceFromLocations(endpointUuid + ".p12", this,
                                "home:" + "/.config/cloud.io/",
                                "file:/etc/cloud.io/",
                                "classpath:cloud.io/"),
                        properties.getProperty(ENDPOINT_IDENTITY_PASS_PROPERTY,
                                ENDPOINT_IDENTITY_PASS_DEFAULT).toCharArray());
            }

            KeyManagerFactory endpointKeyCertManagerFactory =
                    KeyManagerFactory.getInstance(ENDPOINT_IDENTITY_MANAGER_TYPE);
            endpointKeyCertManagerFactory.init(endpointKeyCertStore, "".toCharArray());

            // Authority certificate in JKS format.
            KeyStore authorityKeyStore = KeyStore.getInstance(CERT_AUTHORITY_FILE_TYPE);

            if (properties.containsKey(CERT_AUTHORITY_FILE_PROPERTY)) {
                authorityKeyStore.load(ResourceLoader.getResource(properties.getProperty(CERT_AUTHORITY_FILE_PROPERTY),
                        this), properties.getProperty(CERT_AUTHORITY_PASS_PROPERTY,
                    CERT_AUTHORITY_PASS_DEFAULT).toCharArray());
            } else {
                authorityKeyStore.load(ResourceLoader.getResourceFromLocations(CERT_AUTHORITY_FILE_DEFAULTNAME, this,
                    "home:" + "/.config/cloud.io/",
                    "file:/etc/cloud.io/",
                    "classpath:cloud.io/"), properties.getProperty(CERT_AUTHORITY_PASS_PROPERTY,
                    CERT_AUTHORITY_PASS_DEFAULT).toCharArray());
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(CERT_AUTHORITY_MANAGER_TYPE);
            trustManagerFactory.init(authorityKeyStore);

            // Create SSL Context.
            SSLContext sslContext = SSLContext.getInstance(properties.getProperty(SSL_PROTOCOL_PROPERTY,
                    SSL_PROTOCOL_DEFAULT));
            sslContext.init(endpointKeyCertManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            return sslContext.getSocketFactory();
        }

        private void set(String topic, Stack<String> location, CloudioMessageFormat messageFormat, byte[] data)
            throws Exception {
            // The path to the location must be start with the actual UUID of the endpoint.
            if (!location.isEmpty() && uuid.equals(location.pop()) &&
                !location.isEmpty() && "nodes".equals(location.pop()) &&
                !location.isEmpty()) {

                // Get the node with the name according to the topic.
                CloudioNode.InternalNode node = nodes.getItem(location.peek());
                if (node != null) {
                    location.pop();

                    // Get the attribute reference.
                    CloudioAttribute.InternalAttribute attribute = node.findAttribute(location);
                    if (attribute != null) {
                        // Deserialize the message into the attribute.
                        messageFormat.deserializeAttribute(data, attribute);
                    } else {
                        log.error("Attribute at \"" + topic + "\" not found!");
                    }
                } else {
                    log.error("Node \"" + location.pop() + "\" not found!");
                }
            } else {
                log.error("Invalid topic: " + topic);
            }
        }

        void close() {
            // Disconnect.
            retryInterval = 0;
            if (mqtt.isConnected()) {
                try {
                    mqtt.disconnect();
                } catch (MqttException exception) {
                    exception.printStackTrace();
                }
            }

            // Close MQTT.
            try {
                mqtt.close();
            } catch (MqttException exception) {
                exception.printStackTrace();
            }

            // Close persistence.
            try {
                persistence.close();
            } catch (MqttPersistenceException exception) {
                exception.printStackTrace();
            }

            // Remove all nodes.
            for (CloudioNode.InternalNode node: nodes) {
                node.close();
            }
            nodes.clear();
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            close();
        }
    }
}
