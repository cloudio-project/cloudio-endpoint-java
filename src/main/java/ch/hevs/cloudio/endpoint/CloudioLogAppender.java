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


@Plugin(
        name = "CloudioLogAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
public class CloudioLogAppender extends AbstractAppender {

    LogAppenderDelegate logAppenderDelegate = null;

    public interface LogAppenderDelegate{
        void logDelegate(CloudioLogMessage cloudioLogMessage );
    }

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
        if(logAppenderDelegate != null){
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

            logAppenderDelegate.logDelegate(cloudioLogMessage);
        }
    }

    public void setAppenderLogAppenderDelegate(LogAppenderDelegate logAppenderDelegate){
        this.logAppenderDelegate = logAppenderDelegate;
    }
}