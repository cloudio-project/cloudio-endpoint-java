package ch.hevs.cloudio.endpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JobsManager {

    private static final Logger log = LogManager.getLogger(JobsManager.class);

    private enum CmdJobs{
        listJobs, updateJobs
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

    public void executeJob(String jobURI, String filePath, String correlationID, Boolean output, String jobsData, MqttAsyncClient mqtt, CloudioMessageFormat messageFormat, String uuid){
        ProcessBuilder processBuilder = new ProcessBuilder();

        String[] splitJobUri = jobURI.split("://");
        String scheme = splitJobUri[0];
        String job = splitJobUri[1];

        byte[] data;

        switch (scheme) {
            case "file":

                if (!listScripts(filePath).isEmpty() && listScripts(filePath).contains(job)){
                    processBuilder.command(filePath + "/" + job);

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
                            mqtt.publish("@execOutput/" + uuid + "/" + correlationID, data, 1, false);
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
                        mqtt.publish("@execOutput/" + uuid + "/" + correlationID, data, 1, false);
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
                                mqtt.publish("@execOutput/" + uuid + "/" + correlationID, data, 1, false);
                                for(CmdJobs cmd : CmdJobs.values())
                                {
                                    data = messageFormat.serializeJobsLineOutput(
                                            new JobsLineOutput("\t"+cmd.name(), correlationID));
                                    mqtt.publish("@execOutput/" + uuid + "/" + correlationID,data , 1, false);
                                }

                                data = messageFormat.serializeJobsLineOutput(
                                        new JobsLineOutput("List of jobs available as jobURI \"file://fileName\":", correlationID));
                                mqtt.publish("@execOutput/" + uuid + "/" + correlationID, data, 1, false);
                                for(String file : files)
                                {
                                    data = messageFormat.serializeJobsLineOutput(
                                            new JobsLineOutput("\t"+file, correlationID));
                                    mqtt.publish("@execOutput/" + uuid + "/" + correlationID, data, 1, false);

                                }
                            } catch (MqttException exception) {
                                log.error("Exception: " + exception.getMessage());
                                exception.printStackTrace();
                            }
                            break;
                        case updateJobs:
                            try {

                                data = messageFormat.serializeJobsLineOutput(
                                        new JobsLineOutput("Fetching file in  " + jobsData, correlationID));
                                mqtt.publish("@execOutput/" + uuid + "/" + correlationID, data, 1, false);
                                InputStream in = new URL(jobsData).openStream();
                                Files.copy(in, Paths.get("tmp.zip"), StandardCopyOption.REPLACE_EXISTING);
                                data = messageFormat.serializeJobsLineOutput(
                                        new JobsLineOutput("UnZipping file in directory " + filePath, correlationID));
                                mqtt.publish("@execOutput/" + uuid + "/" + correlationID, data, 1, false);
                                unzipFileIntoDirectory("tmp.zip", filePath, mqtt, messageFormat, uuid, correlationID);
                                data = messageFormat.serializeJobsLineOutput(
                                        new JobsLineOutput("File successfully unzipped in " + filePath, correlationID));
                                mqtt.publish("@execOutput/" + uuid + "/" + correlationID, data, 1, false);
                            }catch (FileNotFoundException exception){
                                log.error("Exception: " + exception.getMessage());
                                exception.printStackTrace();
                                try{
                                    data = messageFormat.serializeJobsLineOutput(
                                            new JobsLineOutput("Couldn't download file at specified URL: " + jobsData, correlationID));
                                    mqtt.publish("@execOutput/" + uuid + "/" + correlationID, data, 1, false);
                                }catch (Exception e){
                                    log.error("Exception: " + e.getMessage());
                                    e.printStackTrace();}

                            }catch (Exception exception){
                                log.error("Exception: " + exception.getMessage());
                                exception.printStackTrace();
                            }

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
                        mqtt.publish("@execOutput/" + uuid + "/" + correlationID, data, 1, false);
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
                    mqtt.publish("@execOutput/" + uuid + "/" + correlationID, data, 1, false);
                } catch (MqttException exception) {
                    log.error("Exception: " + exception.getMessage());
                    exception.printStackTrace();
                }
                break;
        }
    }

    private void unzipFileIntoDirectory(String archiveStr, String destinationDirStr, MqttAsyncClient mqtt, CloudioMessageFormat messageFormat, String uuid, String correlationID)
            throws Exception {
        File archive = new File(archiveStr);
        File destinationDir = new File(destinationDirStr);
        final int BUFFER_SIZE = 1024;
        BufferedOutputStream dest = null;
        FileInputStream fis = new FileInputStream(archive);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
        ZipEntry entry;
        File destFile;
        byte[] dataMqtt;

        while ((entry = zis.getNextEntry()) != null) {
            dataMqtt = messageFormat.serializeJobsLineOutput(
                    new JobsLineOutput("unZipped "+destinationDir+ File.separator +entry.getName(), correlationID));
            mqtt.publish("@execOutput/" + uuid + "/" + correlationID, dataMqtt, 1, false);
            destFile = new File (destinationDir+ File.separator +entry.getName());
            if (entry.isDirectory()) {
                destFile.mkdirs();
                continue;
            } else {
                int count;
                byte data[] = new byte[BUFFER_SIZE];
                destFile.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(destFile);
                dest = new BufferedOutputStream(fos, BUFFER_SIZE);
                while ((count = zis.read(data, 0, BUFFER_SIZE)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
                fos.close();
            }
        }
        zis.close();
        fis.close();
    }
}
