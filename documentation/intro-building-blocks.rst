Building blocks
===============

cloud.iO is based on existing and mature open source technology. The following chapters will introduce the building blocks used to develop cloud.iO.

Message based microservice architecture
---------------------------------------

cloud.iO is based on a microservice architecture as illustrated in the following figure:

.. figure:: _static/intro-building-blocks-1.svg
   :align: center
   :figwidth: 75 %

This has the following advantages:

* Simplicity
   * A complex system can be composed by many of simple services.
   * Most of those services are completely stateless.
* Scalability
   * Services can be spread over multiple computing nodes.
   * Services under high load can be dynamically deployed to additional computing nodes.
* Extensibility
   * The System can be extended by new services or services can be modified/updated without any need for system downtime.

Spring framework
----------------

The |Spring_framework| is a mature, modular, open-source Java EE application stack with focus on web and cloud development. It offers the following features
from a cloud.iO point of view:

* Lightweight and modular, ideal for microservices.
* Uses Java and Kotlin as main programming languages.
* Dependency injection and inversion of control allow decoupling of components and simplifies testing.
* Spring comes with an impressive amount of libraries and functionality to simplify developing.
* Spring offers excellent AMQP, JMS, Apache Kafka and STOMP messaging support.
* Spring offers support for many popular SQL and NoSQL databases.
* Spring is completely open source.

RabbitMQ
--------

MQTT
----

AMQP
----

.. |Spring_framework| raw:: html

   <a href="https://spring.io" target="_blank">Spring framework</a>
