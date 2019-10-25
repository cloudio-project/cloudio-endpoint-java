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


@Plugin(
        name = "CloudioLogAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
public class CloudioLogAppender extends AbstractAppender {

    private MqttAsyncClient mqtt = null;
    private String uuid = null;
    private CloudioMessageFormat messageFormat = null;



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

            CloudioLog cloudioLog = new CloudioLog(
                    event.getLevel().toString(),
                    (double)event.getTimeMillis(),
                    event.getMessage().getFormattedMessage(),
                    loggerName,
                    event.getSource().getClassName()+"/"+
                            event.getSource().getMethodName()+
                            ", line:"+event.getSource().getLineNumber()
            );
            try{
            mqtt.publish("@logs/" + uuid,
                    messageFormat.serializeCloudioLog(cloudioLog), 1, false);
            }
            catch (MqttException exception){
                exception.printStackTrace();
            }
        }

    }

    public void setAppenderMqttParameters(MqttAsyncClient mqtt, String uuid, CloudioMessageFormat messageFormat){
        this.mqtt = mqtt;
        this.uuid = uuid;
        this.messageFormat = messageFormat;
    }
}