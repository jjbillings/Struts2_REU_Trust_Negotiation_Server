#Setting Up Trust Negotiation server
This document will provide instructions for setting up the Trust Negotiation Server.

###Download Tomcat
First, we will need Apache-Tomcat-7 which can be downloaded here: [Download](https://tomcat.apache.org/download-70.cgi)
Install that somewhere you can get to. On my MAC I installed it at:
```
/usr/local/${root}
```
where ``` ${root} ``` is the root of the Tomcat directory structure (something like ```apache-tomcat-7.0.70```). From here on out, we will refer to that root director as ```$CATALINA_HOME```; that is, ```$CATALINA_HOME=/usr/local/apache-tomcat-7.0.70/```.

To test the server and see if it's working, run:
```
$CATALINA_HOME/bin/startup.sh
```
Then go to your web browser and navigate to: ```localhost:8080```. If you see the Tomcat manager screen then you were successful.

###Obtain Admin Privelidges
Now, to access the Manager App, we need to give you permission to do so. Back in the terminal, edit the *users* file:
```
nano $CATALINA_HOME/conf/tomcat-users.xml
```
Inside the ``` <tomcat-users> ``` tag at the bottom, add:
```
<role rolename="admin-gui"/>
<user username="admin" password="my_password" role="manager-gui, manager-script, admin-gui, admin-script, admin"/>
```
Save the changes and exit. Now you may deploy applications to your Tomcat Server! However, there are still a couple of things we need to do.

###Setup Keystore
Now we are going to set up the keystore that we will use to enable secure connections to this server. navigate to ``` $CATALINA_HOME ``` and create a new directory for the keystore. I have ``` $CATALINA_HOME/test_keystore/ ```. Navigate into that directory and we are going to play with Java's Keytool program. use the command:
```
keytool -genkey -keyalg RSA -alias my_alias -keystore my_keystore.jks -storepass my_password -validity 360 -keysize 2048
```
and follow the onscreen prompts to create a self-signed certificate and keys for this server. Make note of the password you choose forthe keystore, as we will need it when generating certificates, as well as when we configure the SSL. Use another keytool command to generate a copy of the certificate:
```
keytool -export -alias my_alias -file test_cert.crt -keystore my_keystore.jks
```
Enter your password and a certificate will be generated inside your current directory. This certificate is a copy of the one contained in the keystore. Give this copy to any device that you want to connect to the server over secure connection. Keytool is very useful, especially for examining certificates. A bunch of useful Keytool commands may be found [HERE](https://www.sslshopper.com/article-most-common-java-keytool-keystore-commands.html)

###Configure HTTPS/SSl

###Deploy Application
