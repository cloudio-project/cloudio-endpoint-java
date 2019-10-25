package ch.hevs.cloudio.endpoint;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JobsManager {

    private static final Logger log = LoggerFactory.getLogger(JobsManager.class);

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

    public void executeJob(String jobURI, String filePath, Boolean output, MqttAsyncClient mqtt, String uuid){
        ProcessBuilder processBuilder = new ProcessBuilder();

        String scheme = jobURI.split("://")[0];
        String job = jobURI.split("://")[1];

        switch (scheme) {
            case "file":

                if (!listScripts(filePath).isEmpty() && listScripts(filePath).contains(job)){
                    processBuilder.command("bash", "-c", "sh " + filePath + "/" + job);

                    try {

                        Process process = processBuilder.start();

                        if (!output)
                            return;

                        BufferedReader reader =
                                new BufferedReader(new InputStreamReader(process.getInputStream()));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            mqtt.publish("@execOutput/" + uuid, line.getBytes(), 1, false);
                        }

                        int exitCode = process.waitFor();
                        System.out.println("\nExited with error code : " + exitCode);

                    } catch (MqttException exception) {
                        log.error("Exception: " + exception.getMessage());
                        exception.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else
                {
                    try {
                        mqtt.publish("@execOutput/" + uuid, ("file uri:" + jobURI + " contain invalid file").getBytes(), 1, false);
                    } catch (MqttException exception) {
                        log.error("Exception: " + exception.getMessage());
                        exception.printStackTrace();
                    }
                }
                break;
            case "cmd":
                try {
                    System.out.println(job);
                    CmdJobs cmdJobs = CmdJobs.valueOf(job);
                    switch (cmdJobs){
                        case listJobs:
                            List<String> files = JobsManager.getInstance().listScripts(filePath);
                            try {
                                mqtt.publish("@execOutput/" + uuid, "List of jobs available as jobURI \"cmd://commandName\":".getBytes(), 1, false);
                                for(CmdJobs cmd : CmdJobs.values())
                                {
                                    mqtt.publish("@execOutput/" + uuid,("\t"+cmd.name()).getBytes(), 1, false);
                                }

                                mqtt.publish("@execOutput/" + uuid, "List of jobs available as jobURI \"file://fileName\":".getBytes(), 1, false);
                                for(String file : files)
                                {
                                    mqtt.publish("@execOutput/" + uuid,("\t"+file).getBytes(), 1, false);

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
                        mqtt.publish("@execOutput/" + uuid, ("cmd uri:" + jobURI + " not supported").getBytes(), 1, false);
                    } catch (MqttException exception) {
                        log.error("Exception: " + exception.getMessage());
                        exception.printStackTrace();
                    }
                }

                break;
            default:
                try {
                    mqtt.publish("@execOutput/" + uuid, ("uri:" + jobURI + " not supported").getBytes(), 1, false);
                } catch (MqttException exception) {
                    log.error("Exception: " + exception.getMessage());
                    exception.printStackTrace();
                }
                break;
        }
    }
}
