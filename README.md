README
======

This README describes the simple-server app. 

<br />

What is simple-server?
---------------------------------------------------------
Simple server is a starting point for a micro-service java web server. It is 

* based on undertow
* should support static content, servlet, and REST APIs.
* should be really easy to host a AngularJS app, or a RestAPI server.


<br />

How to run?
---------------------------------------------------------
* ```mvn clean package```
* ```mvn exec:java```
* Point browser to http://localhost:6060/api and http://localhost:6060/static
