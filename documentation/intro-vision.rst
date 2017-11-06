Vision
======

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
