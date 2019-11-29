# cloudio-endpoint-java [![Build Status](https://travis-ci.org/cloudio-project/cloudio-endpoint-java.svg?branch=master)](https://travis-ci.org/cloudio-project/cloudio-endpoint-java) [ ![Download](https://api.bintray.com/packages/cloudio-project/clients/ch.hevs.cloudio%3Acloudio-endpoint-java/images/download.svg) ](https://bintray.com/cloudio-project/clients/ch.hevs.cloudio%3Acloudio-endpoint-java/_latestVersion)
Java endpoint (IoT device) library for [cloud.iO](https://cloudio.hevs.ch).

## Documentation
The online javadoc Documentation can be found [here](https://cloudio.hevs.ch/javadoc/0.1.0/cloudio-endpoint-java).

## How to use
First of all you need to add the dependency to the cloud.iO endpoint library to your Java project. The following chapters
show how to configure your [gradle](https://gradle.org) or [maven](https://maven.apache.org) projects in order to use cloud.iO.

#### Gradle

```groovie
repositories {
    jcenter()
}

dependencies {
    compile(group: 'ch.hevs.cloudio', name: 'cloudio-endpoint-java', version: '0.1.5')
}
```

#### Maven

```xml
    <repositories>
        <repository>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
          <id>central</id>
          <name>bintray</name>
          <url>http://jcenter.bintray.com</url>
        </repository>
    </repositories>

    <dependencies>
       <dependency>
         <groupId>ch.hevs.cloudio</groupId>
         <artifactId>cloudio-endpoint-java</artifactId>
         <version>0.1.5</version>
         <type>jar</type>
       </dependency>
    </dependencies>
```

## Using development snapshots
If you need the latest development snapshot, use these gradle/maven dependencies:

#### Gradle

```groovie
repositories {
    jcenter()
    maven {
        url "https://oss.jfrog.org/artifactory/list/oss-snapshot-local"
    }
}

dependencies {
    compile(group: 'ch.hevs.cloudio', name: 'cloudio-endpoint-java', version: '0.2.0-SNAPSHOT', ext: 'jar')
}
```

#### Maven

```xml
    <repositories>
        <repository>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
          <id>central</id>
          <name>bintray</name>
          <url>http://jcenter.bintray.com</url>
        </repository>
        <repository>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
          <id>JFrog OSS snapshots</id>
          <name>bintray snapshots</name>
           <url>https://oss.jfrog.org/artifactory/list/oss-snapshot-local</url>
         </repository>
    </repositories>

    <dependencies>
       <dependency>
         <groupId>ch.hevs.cloudio</groupId>
         <artifactId>cloudio-endpoint-java</artifactId>
         <version>0.2.0-SNAPSHOT</version>
         <type>jar</type>
       </dependency>
    </dependencies>
```
