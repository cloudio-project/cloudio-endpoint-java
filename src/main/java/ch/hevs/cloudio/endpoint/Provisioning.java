package ch.hevs.cloudio.endpoint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
public class Provisioning {
    public boolean provision(URI uri) {
        return provision(uri, Paths.get(System.getProperty("user.home"), ".config", "cloud.io"));
    }
    public boolean provision(URI uri, Path destination) {
        return provision(uri, destination, null);
    }
    public boolean provision(URI uri, Path destination, String propertiesFileName) {
        return provision(uri, destination, propertiesFileName, null);
    }
    private boolean provision(URI uri, Path destination, String propertiesFileName, String publicKey) {
        try {
            if (propertiesFileName != null) uri = new URI(uri.toString() + "?propertiesFileName=" + URLEncoder.encode(propertiesFileName, StandardCharsets.UTF_8));
            if (publicKey != null) uri = new URI(uri.toString() + (propertiesFileName == null ? "?" : "&") + "publicKey=" + URLEncoder.encode(publicKey, StandardCharsets.UTF_8));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .header("Accept", "application/java-archive")
                    .timeout(Duration.of(10, ChronoUnit.SECONDS))
                    .build();
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.ISO_8859_1));
            if (response.statusCode() == 200) {
                JarInputStream jar = new JarInputStream(new ByteArrayInputStream(response.body().getBytes(StandardCharsets.ISO_8859_1)));
                JarEntry entry;
                while ((entry = jar.getNextJarEntry()) != null) {
                    if (entry.isDirectory()) {
                        Files.createDirectories(destination.resolve(entry.getName()));
                    } else {
                        Files.createDirectories(destination.resolve(entry.getName()).getParent());
                        Files.copy(jar, destination.resolve(entry.getName()));
                    }
                }
            } else {
                return false;
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            return false;
        }
        return true;
    }
    public static void main(String[] args) {
        boolean result = false;
        boolean showUsage = false;
        try {
            Provisioning provisioning = new Provisioning();
            switch (args.length) {
                case 1:
                    result = provisioning.provision(new URI(args[0]));
                    break;
                case 2:
                    result = provisioning.provision(new URI(args[0]), Path.of(args[1]));
                    break;
                case 3:
                    result = provisioning.provision(new URI(args[0]), Path.of(args[1]), args[2]);
                    break;
                default:
                    showUsage = true;
            }
        } catch (URISyntaxException exception) {
            System.err.println("Invalid URI: " + exception.getMessage());
            showUsage = true;
        } catch (Exception exception) {
            System.err.println("Exception: " + exception.getMessage());
        }
        if (result) {
            System.out.println("Provisioning succeeded.");
        } else {
            System.out.println("Provisioning failed.");
        }
        if (showUsage) {
            System.out.println("\nUsage: java ... <provisioning url> <output path> <application name>");
            System.out.println("<provisioning url> : URL to get the provisioning data from (inclusive provisioning token). Required");
            System.out.println("<output path> : Optional output path for the endpoint configuration, certificates and keys. Defaults to ~/.config/cloud.iO");
            System.out.println("<application name> : Optional application name (name of the .properties file). Defaults to the UUID of the endpoint.");
        }
        System.exit(result ? 0 : 1);
    }
}