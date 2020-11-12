# Digital Signature Service Example Web Application

This Java EE web application demonstrates how to integration the Digital Signature Service.
The web application has been tested on different Java EE application servers.

The DSSP client requires Apache WSS4J to be available at runtime.
This WSS4J runtime can either be embedded within the application server, or can be bundled within the WAR, depending on the capabilities of the used application server. It is the responsibility of the web application owner to manage a functional WSS4J runtime.
See also: https://www.e-contract.be/sites/dssp/dssp-client/


## WildFly & JBoss EAP 7

Deploy the web application on a local running WildFly or JBoss EAP 7 application server via:
```
mvn clean wildfly:deploy
```
The web application will use the Apache WSS4J version 2 that is embedded within the JBoss EAP application server.

The web application should be available at: http://localhost:8080/dssp-example/

For versions of WildFly that still use WSS4J 2.1.x, you can deploy via:
```
mvn clean wildfly:deploy -Pwss4j21
```

## JBoss EAP 6

Deploy the web application on a local running JBoss EAP 6 application server via:
```
mvn clean jboss-as:deploy -Pwss4j1
```
The web application will use the Apache WSS4J version 1 that is embedded within the JBoss EAP application server.

The web application should be available at: http://localhost:8080/dssp-example/


## Open Liberty

Run the web application on Open Liberty via:
```
mvn clean test -Pliberty
```
Apache WSS4J version 2 will be bundled within the WAR.

The web application should be available at: http://localhost:9080/dssp-example/


## Payara

Build the web application for deployment on Payara via:
```
mvn clean install -Ppayara
```
Apache WSS4J version 2 will be bundled within the WAR.