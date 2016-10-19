# Sputnik

Long-term processes performance monitor application. Similar to Munin or Cacti. The advantages of Sputnik are simple configuration, low resource consumption and modern UI.

### Build
 
`dist.sh`

### Run

unzip `sputnik-server/target/sputnik-server-x.x.x.zip`

run `sputnik/bin/sputnik.sh run`

###  Intall linux service

`cp -r sputnik/config /var/sputnik`

`cp sputnik/bin/sputnik.sh /etc/init.d/sputnik`

`service sputnik start`

### Use

Navigate to sputnik home page, by default [http://localhost:8000](http://localhost:8000).
 
You need to sign in to be able to add/modify data sources. Use any login and a password from propertiess file (`sputnik.password`). If password is not set a random one is generated and is printed to the log.
 
### Data sources

Currently sputnik can collect data from http endpoints. 

JEE containers like Jetty/Tomcat can be monitored using provided `metrics.war`.
 
### Data profiles
 
Data profile defines what information from a datasource must be archived and represented. The following profiles are available out of the box:
  * `java`: spring-boot based applications and JEE containers
  * `elasticsearch`: elasticsearch nodes 