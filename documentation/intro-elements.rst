Elements in a cloud.iO ecosystem
================================

A cloud.iO system is basically made up of three categories of elements as illustrated by the following figure:

.. figure:: _static/intro-elements-1.svg
   :align: center
   :figwidth: 75 %

   cloud.iO ecosystem

The following paragraphs will give a short introduction to the role of each category of elements.

Endpoint
--------

Endpoints are distributed field devices featuring:

* a TCP/IP interface to access cloud.iO core services.
* a set of sensors and/or actuators either directly integrated in the endpoint device or connected by some (local) networking technology like Zigbee for
  example.

It needs a SSL certificate to communicate with cloud.iO (MQTT protocol). The definitions (interfaces, pre-processing steps, ...) are stored in CloudiO, but the implementation is done on back-end solutions, depending on the type of endpoint.

An endpoint does:

* publish his complete data model (including current values) when connecting to the cloud.
* publish a message when disconnected from the cloud.
* sends a message every time data of his data model has changed.
* allow applications or users to change the data from the outside (control, parameters).

Application
-----------

An applications is a computer programs that:

* can subscribe to input signals, which are typically results of sensors measurement.
* receive updates of subscribed input values without polling.
* set values of output signals, which are typically set points for actuators.
* access past values for any input or output signals.

It needs a SSL certificate to communicate with CloudiO (AMQP protocol).

An application can:

* search for actual data using schemes (interfaces or data classes).
* get actual and historical data for endpoint's attributes.
* control set points and parameters of endpoints.

Cloud.iO
--------

The main objectives of the cloud.iO core services are:

* to decouple Applications from Endpoints by providing a syntactic abstraction layer.
* to keep up-to-date the topology of cloud.iO Endpoints and their current status.
* to log history values of all input signals and to make the allow Applications querying the.
* to enable Applications access to the current topology as well as the history logs.
* to enforce privacy rules based on access rights.

The history logs, the current topology and the access rights are stored in databases. These databases are named respectively *history database*,
*process database* and *access rights database*.

Those databases are not part of the cloud.iO framework. The latter provides a database management system independent back-end connector for the three databases
as well as a reference implementation for drivers of database management system compatible with the connectors.

User
----

A user is the owner of endpoints and applications. A user:

* owns one or more endpoints.
* can give other users access to his endpoints.
* can give applications access to his endpoints.
* can write his own applications.
* needs a login and password to communicate with cloud.iO (AMQP protocol)

We actually do not distinguish between different kind of users (simple users, developers, ...).
