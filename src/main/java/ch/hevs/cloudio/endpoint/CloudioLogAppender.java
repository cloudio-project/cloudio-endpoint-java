package ch.hevs.cloudio.endpoint;


import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.Calendar;
import java.util.concurrent.ConcurrentMap;


@Plugin(
        name = "CloudioLogAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
public class CloudioLogAppender extends AbstractAppender {

    private MqttAsyncClient mqtt = null;
    private String uuid = null;
    private CloudioMessageFormat messageFormat = null;
    private boolean persistence = false;
    String PERSISTENCE_FILE;
    String PERSISTENCE_MAP_MQTT_MESSAGES;
    Object persistenceLock;



    protected CloudioLogAppender(String name, Filter filter) {
        super(name, filter, null);
    }

    @PluginFactory
    public static CloudioLogAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter) {
        return new CloudioLogAppender(name, filter);
    }

    @Override
    public void append(LogEvent event) {
        if(mqtt != null && uuid!= null && messageFormat != null){
            String loggerName;
            if(event.getLoggerName().equals(""))
                loggerName = "RootLogger";
            else
                loggerName = event.getLoggerName();

            CloudioLogMessage cloudioLogMessage = new CloudioLogMessage(
                    event.getLevel().toString(),
                    (double)event.getTimeMillis(),
                    event.getMessage().getFormattedMessage(),
                    loggerName,
                    event.getSource().getClassName()+"/"+
                            event.getSource().getMethodName()+
                            ", line:"+event.getSource().getLineNumber()
            );

            byte data[] = messageFormat.serializeCloudioLog(cloudioLogMessage);

            // Try to send the message if the MQTT client is connected.
            boolean messageSend = false;
            if (mqtt.isConnected()) {
                try {
                    mqtt.publish("@logs/" + uuid, data, 1, false);
                    messageSend = true;
                } catch (MqttException exception) {
                    exception.printStackTrace();
                }
            }

            // If the message could not be send for any reason, add the message to the pending updates persistence if
            // available.
            if (!messageSend && persistence) {
                try {
                    synchronized (persistenceLock) {
                        DB dbPersistenceData = DBMaker.fileDB(PERSISTENCE_FILE).make();
                        ConcurrentMap map = dbPersistenceData.hashMap(PERSISTENCE_MAP_MQTT_MESSAGES).expireMaxSize(3).expireAfterUpdate().createOrOpen();
                        map.put("PendingUpdate-@logs/" + uuid
                                        + "-" + Calendar.getInstance().getTimeInMillis(),
                                data);
                        dbPersistenceData.close();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

    }

    public void setAppenderMqttParameters(MqttAsyncClient mqtt, String uuid, CloudioMessageFormat messageFormat, boolean persistence, String PERSISTENCE_FILE, String PERSISTENCE_MAP_MQTT_MESSAGES, Object persistenceLock){
        this.mqtt = mqtt;
        this.uuid = uuid;
        this.messageFormat = messageFormat;
        this.persistence = persistence;
        this.PERSISTENCE_FILE = PERSISTENCE_FILE;
        this.PERSISTENCE_MAP_MQTT_MESSAGES = PERSISTENCE_MAP_MQTT_MESSAGES;
        this.persistenceLock = persistenceLock;
    }
}