# cloudio-endpoint-java [![Build Status](https://travis-ci.org/cloudio-project/cloudio-endpoint-java.svg?branch=master)](https://travis-ci.org/cloudio-project/cloudio-endpoint-java) [ ![Download](https://api.bintray.com/packages/cloudio-project/clients/ch.hevs.cloudio%3Acloudio-endpoint-java/images/download.svg) ](https://bintray.com/cloudio-project/clients/ch.hevs.cloudio%3Acloudio-endpoint-java/_latestVersion)
Java endpoint (IoT device) library for [cloud.iO](https://cloudio.hevs.ch).

## Documentation
The online javadoc Documentation can be found [here](https://cloudio.hevs.ch/javadoc/0.1.0/cloudio-endpoint-java).

## How to use
First of all you need to add the dependency to the cloud.iO endpoint library to your Java project. The following chapters
show how to configure your [gradle](https://gradle.org) or [maven](https://maven.apache.org) projects in order to use cloud.iO.

#### Gradle
You first need to authenticate to github packages. There are 2 solutions:
#### Properties file
Add a "gradle.properties" file in the same directory as the "build.gradle" file.
```
gpr.user=Your_user_name_without_quotes
gpr.key=Your_api_key_without_quotes
```
#### Environment variable
You can define a "GPR_USER" and a "GPR_API_KEY" as environment variables.
#### build.gradle file
```groovie
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/cloudio-project/cloudio-endpoint-java")
            credentials {
                username = project.findProperty("gpr.user") ?: System.getenv("GPR_USER")
                password = project.findProperty("gpr.key") ?: System.getenv("GPR_API_KEY")
            }
    }
}

dependencies {
    compile "ch.hevs.cloudio:cloudio-endpoint-java:0.1.6"
}
```

#### Maven

You first need to authenticate to github packages. More informations [here](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages).
```xml
    <dependencies>
      <dependency>
        <groupId>ch.hevs.cloudio</groupId>
        <artifactId>cloudio-endpoint-java</artifactId>
        <version>0.1.6</version>
      </dependency>
    </dependencies>

```

## Using development snapshots
If you need the latest development snapshot, use these gradle/maven dependencies:

#### Gradle
You first need to authenticate to github packages. There are 2 solutions:
#### Properties file
Add a "gradle.properties" file in the same directory as the "build.gradle" file.
```
gpr.user=Your_user_name_without_quotes
gpr.key=Your_api_key_without_quotes
```
#### Environment variable
You can define a "GPR_USER" and a "GPR_API_KEY" as environment variables.
#### build.gradle file
```groovie
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/cloudio-project/cloudio-endpoint-java")
            credentials {
                username = project.findProperty("gpr.user") ?: System.getenv("GPR_USER")
                password = project.findProperty("gpr.key") ?: System.getenv("GPR_API_KEY")
            }
    }
}

dependencies {
    compile "ch.hevs.cloudio:cloudio-endpoint-java:0.2.0-SNAPSHOT"
}
```

#### Maven
You first need to authenticate to github packages. More informations [here](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages).
```xml
    <dependencies>
      <dependency>
        <groupId>ch.hevs.cloudio</groupId>
        <artifactId>cloudio-endpoint-java</artifactId>
        <version>0.2.0-SNAPSHOT</version>
      </dependency>
    </dependencies>

```
