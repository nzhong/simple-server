README
======

This README describes the simple-server app. 

<br />

What is simple-server?
---------------------------------------------------------
Simple server is a starting point for a micro-service java web server. It is 

* based on undertow
* should support static content, servlet, and REST APIs.
* should be really easy to host an AngularJS app, a ReactJS app, or a RestAPI server.


<br />

How to run?
---------------------------------------------------------
* ```mvn clean package```
* ```mvn exec:java```
* or ```java -jar target/simple-server-1.0-SNAPSHOT-jar-with-dependencies.jar```
* Point browser to:
* http://localhost:6060/api   should return api
* http://localhost:6060/static  should return static html/css/js
* http://localhost:6060/serv/hello  is handled by servlet
* http://localhost:6060/rest/api/sayhello is handled by REST API
