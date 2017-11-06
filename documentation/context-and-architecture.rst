Concept and architecture
************************

The vision
==========

The development of cloud computing platforms, ubiquitous networking and embedded/mobile systems has enabled the connection of various devices and appliances to
the internet and the deployment of related services. Such systems where, tenths, hundreds or thousands of devices are connected to the internet are known as
"Internet of Things" (IoT).

Of course, each individual project can assemble embedded sensors and actuators, develop its own communication protocols and security systems, setup specific
databases and develop ad hoc services. This approach, which has been used and is still used in many projects, does not recognize the fact that there is a
common pattern to all projects: each distributed “thing” feature a data model made up of measurement values, configuration parameters, status.

The vision of the cloud.iO project has been launched is to “industrialize” IoT systems deployment. To concrete this vision, the following options were
considered in the design of cloud.iO:

1. The cloud.iO IoT platform is operated as SaaS (Software as a Service) and is meant to host multiple projects.
2. cloud.iO applications dispose of a syntactically uniform access to so-called data points in distributed embedded systems.
3. cloud.io does not impose any semantics for data model. Individual projects can either define their own semantics or use a free data model.
4. The cloud.iO platform manages access rights down to a single data point level, enabling the owner of a data point to provide applications with read 
   and also possible right access to data points. Hence any privacy policy can be enforced.
5. cloud.iO keeps a history all read and write operations and provides an access to the history logs.
6. cloud.iO manages a searchable directory with the current status of all connected “things”.
7. cloud.iO defines secure connection rules for embedded devices as well as for applications.
8. cloud.iO provides APIs (Application Programming Interfaces) for the software development of embedded devices as well as for the development of cloud
   applications.
9. cloud.iO design philosophy is the following: reuse field proven open source software components and make minimum custom developments around them..
10. A provisioning system is responsible for the supervision of IoT devices and for triggering alarms when a problem is detected. cloud.iO provides a component
    letting individual projects define their own provisioning rules.
11. cloud.iO provides an interface for devices, applications and user management.
12. cloud.iO supports the plug and play deployment of large amounts of devices.

In its current status, many options listed above are not implemented, but the architecture does not put any barrier to possible future development of the not
yet implemented options.

cloud.iO can be operated in two modes:

1. **Private mode**: A cloud.io framework is deployed for an IoT project.
2.	**Public mode**: Several projects share a unique cloud.iO infrastructure.


Elements in a cloud.iO ecosystem
================================

Architecture
------------

A cloud.iO system is basically made up of three categories of elements as illustrated by *Figure 1*.

.. figure:: _static/cloudio.png
   :align: center

   Figure 1: cloud.iO ecosystem.

The following paragraphs explain the role of each category of elements.

Endpoint
--------

Endpoints are distributed field devices featuring:

* a TCP/IP interface to access cloud.iO core services, and
* a set of sensors and/or actuators either directly integrated in the endpoint device or connected by some (local) networking technology like Zigbee as example.

Applications
------------

Applications are computer programs that:

* can subscribe to input signals, which are typically results of sensors measurement,
* receive updates of subscribed input values without polling,
* set values of output signals, which are typically set points for actuators,
* access past values for any input or output signals.

Cloud
-----

The main objectives of the cloud services are:

* to decouple Applications from Endpoints by providing a syntactic abstraction layer,
* to keep up-to-date the topology of cloud.iO Endpoints and their current status,
* to log history values of all input signals and to make the allow Applications querying the,
* to enable Applications access to the current topology as well as the history logs, and
* to enforce privacy rules based on access rights.

The history logs, the current topology and the access rights are stored in databases. These databases are named respectively *history database*,
*process database* and *access rights database*.

The databases are not part of the cloud.iO framework. The latter provides a database management system independent back-end connector for the three databases
as well as a reference implementation for drivers of database management system compatible with the connectors.


Information model and message routing
=====================================

Information model
-----------------

Industrial automation field devices (take OPC as an example) feature a so-called information model: input and output signals are organized as a tree:

* The trunk is the device itself. A hierarchy of intermediate nodes allow classifying input and output signals.
* Leaves represent an input signal or an output signal.

Usually intermediate nodes serve merely to classify the many signals available in a device.

The above-described tree is the information model of the device.

Industrial devices offer typically the following services to remote controllers:

* browse the information model, including meta-data related to model elements (i.e. “nodes” and “leaves”),
* read and/or write single signals, and
* subscribe to changes of input signals.

If a controller has subscribed for changes on a given input signal, the device will push new values to it without the need for periodic polling.

General-purpose industrial automation systems put no limit (or few limits) on the structure of the data model: supervised infrastructure can have any topology
and hence definition of infrastructure specific data models should be possible. Conversely, domain specific field automation devices have a specific semantics:
for example, the input and output signals for photovoltaic inverters are not dependent on the manufacturer. Hence, it makes sense to have a common information
model for all inverters. Electricians, with the IEC 61850 standard series, have standardized information models for the most common field appliance types like
for example, circuit breakers. Standardized information models promote interoperability between field appliances (Endpoints in the cloud.iO terminology) and
controllers (Applications).
