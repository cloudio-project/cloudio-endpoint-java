package ch.hevs.cloudio.endpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JobsManager {

    private static final Logger log = LogManager.getLogger(JobsManager.class);

    private enum CmdJobs{
        listJobs;
    }

    private JobsManager(){
    }

    private static JobsManager instance = new JobsManager();

    public static JobsManager getInstance(){
        return instance;
    }

    public List<String> listScripts(String filePath){
        File folder = new File(filePath);
        File[] listOfFiles = folder.listFiles();
        ArrayList result = new ArrayList();

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                result.add(listOfFile.getName());
            }
        }
        return result;
    }

    public void executeJob(String jobURI, String filePath, String correlationID, Boolean output, MqttAsyncClient mqtt, CloudioMessageFormat messageFormat, String uuid){
        ProcessBuilder processBuilder = new ProcessBuilder();

        String[] splitJobUri = jobURI.split("://");
        String scheme = splitJobUri[0];
        String job = splitJobUri[1];

        byte[] data;

        switch (scheme) {
            case "file":

                if (!listScripts(filePath).isEmpty() && listScripts(filePath).contains(job)){
                    processBuilder.command("$SHELL", filePath + "/" + job);

                    try {

                        Process process = processBuilder.start();

                        if (!output)
                            return;

                        BufferedReader reader =
                                new BufferedReader(new InputStreamReader(process.getInputStream()));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            data = messageFormat.serializeJobsLineOutput(
                                    new JobsLineOutput(line, correlationID));
                            mqtt.publish("@execOutput/" + uuid, data, 1, false);
                        }

                        int exitCode = process.waitFor();
                        log.error("Exited "+jobURI+" with error code : " + exitCode);

                    } catch (MqttException exception) {
                        log.error("Exception: " + exception.getMessage());
                        exception.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else
                {
                    try {
                        data = messageFormat.serializeJobsLineOutput(
                                new JobsLineOutput("file uri:" + jobURI + " contain invalid file", correlationID));
                        mqtt.publish("@execOutput/" + uuid, data, 1, false);
                    } catch (MqttException exception) {
                        log.error("Exception: " + exception.getMessage());
                        exception.printStackTrace();
                    }
                }
                break;
            case "cmd":
                try {
                    CmdJobs cmdJobs = CmdJobs.valueOf(job);
                    switch (cmdJobs){
                        case listJobs:
                            List<String> files = JobsManager.getInstance().listScripts(filePath);
                            try {
                                data = messageFormat.serializeJobsLineOutput(
                                        new JobsLineOutput("List of jobs available as jobURI \"cmd://commandName\":", correlationID));
                                mqtt.publish("@execOutput/" + uuid, data, 1, false);
                                for(CmdJobs cmd : CmdJobs.values())
                                {
                                    data = messageFormat.serializeJobsLineOutput(
                                            new JobsLineOutput("\t"+cmd.name(), correlationID));
                                    mqtt.publish("@execOutput/" + uuid,data , 1, false);
                                }

                                data = messageFormat.serializeJobsLineOutput(
                                        new JobsLineOutput("List of jobs available as jobURI \"file://fileName\":", correlationID));
                                mqtt.publish("@execOutput/" + uuid, data, 1, false);
                                for(String file : files)
                                {
                                    data = messageFormat.serializeJobsLineOutput(
                                            new JobsLineOutput("\t"+file, correlationID));
                                    mqtt.publish("@execOutput/" + uuid, data, 1, false);

                                }
                            } catch (MqttException exception) {
                                log.error("Exception: " + exception.getMessage());
                                exception.printStackTrace();
                            }
                            break;
                        default:
                            //shouldn't arrive, caught in IllegalArgumentException catch
                            break;
                    }
                }
                catch (IllegalArgumentException e)
                {
                    try {
                        data = messageFormat.serializeJobsLineOutput(
                                new JobsLineOutput("cmd uri:" + jobURI + " not supported", correlationID));
                        mqtt.publish("@execOutput/" + uuid, data, 1, false);
                    } catch (MqttException exception) {
                        log.error("Exception: " + exception.getMessage());
                        exception.printStackTrace();
                    }
                }

                break;
            default:
                try {
                    data = messageFormat.serializeJobsLineOutput(
                            new JobsLineOutput("uri:" + jobURI + " not supported", correlationID));
                    mqtt.publish("@execOutput/" + uuid, data, 1, false);
                } catch (MqttException exception) {
                    log.error("Exception: " + exception.getMessage());
                    exception.printStackTrace();
                }
                break;
        }
    }
}
