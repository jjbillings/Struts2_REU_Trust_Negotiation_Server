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
Now, to access the Manager App, we need to give you permission to do so. Back in the terminal, start by shutting down your server with the ```$CATALINA_HOME/bin/shutdown.sh``` command, then edit the *users* file:
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

###Configure HTTPS/SSL
Time to configure our Tomcat server to receive secure connections. Begin by navigating to ``` $CATALINA_HOME/conf/ ``` and editing the server configuration file with: ``` nano server.xml ```. Somewhere closer to the end of the file you should find something along the lines of:
```
<Connector port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />
```
Somewhere below this *Connector*, add a new *Connector*:
```
<Connector port="8443" protocol="HTTP/1.1" URIEncoding="UTF-8"
               maxThreads="150" SSLEnabled="true" scheme="https" secure="true"
               keystoreFile="/usr/local/apache-tomcat-7.0.70/test_keystore/my_keystore.jks"
	           keystorePass="my_password"
	           clientAuth="false" sslProtocol="TLS" />
```
Use your own filepath/password for the keystore. As an aside, if you enable *clientAuth*, then you will need to configure a *Truststore* of certificates that you will accept as means of authentication. We leave this disabled, as our clients (for the trust negotiation server) are not required to have had any previous contact with this system, and thus will not have any certificates to authenticate with.
Your Tomcat server is now configured for HTTPS/SSL. To test that everything's working, navigate in you web browser to: ``` https://localhost:8443 ``` and you should be greeted with a prompt telling you that the browser doesn't trust your Tomcat server. This is because the server's certificate is self-signed, not signed by a legitimate Certificate Authority.

###Deploy Application
Finally we are ready to deploy the Trust Negotiation server. In your web browser navigate to either ```localhost:8080``` or ```https://localhost:8443```. Once you are at the Tomcat manager page, click the *Manager App* button on the right. On the next page, scroll down and select a ```WAR``` file to deploy. once selected, that's it, the Trust Negotiation is deployed!

###Configure Server
One more step. We will need to quickly configure the Trust Negotiation application on the server. navigate to:
```
https://localhost:8443/Struts2_REU_Trust_Negotiation_Server/
```
you will be greeted by the configuration page. First, select the *Setup Everything* button. Once that's done, go back. If this is the *Root* hospital server, then technically you don't need to do anything else. However, if this is NOT the *Root* server, then we need to change a few things. First, start by uploading the *Root AA* and *Root CA* Certificates and Keys using the supplied prompts. Once that is done, set this system's Certificate/Attribute Authority Common Names. Once that is done you are all good to go. ~~Optionally~~ Recommended that you set some *AC Fields* for the next AC to be generated, otherwise the default values will be used. Also, you may now upload the certificates of any Trusted AC Issuers. These certificates will be requried when validating any credentials submitted by the client. For instance, if the user presents certificates signed by Hospital_A's AA to Hospital_B, then a copy of Hospital_A's AA certificate MUST be uploaded to Hospital_B beforehand.

#####That's it! Your Trust Negotiation server is good to go!
#####Any questions? 
Contact Jack Billings: jjbillings@noctrl.edu
