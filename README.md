#as2-peppol-servlet

A standalone servlet that takes AS2 requests with OpenPEPPOL StandardBusinessDocuments and handles them via SPI. This is not a self-contained package, but a good starting point for handling PEPPOL AS2 messages. 

##Dependencies
This package depends on [ph-commons](https://github.com/phax/ph-commons), [ph-sbdh](https://github.com/phax/ph-sbdh) and [as2-lib](https://github.com/phax/as2-lib).

##Usage
To use this project you have to do the following:
  1. Add this project as a dependency to your project
  2. Create an AS2 configuration file and store it in a folder that is writable to your project. The details of the configuration files are described below. 
  3. Modify your `WEB-INF/web.xml` file so that it references the `AS2PeppolReceiveServlet`.
  
Example `WEB-INF/web.xml` configuration:
```xml
...
  <servlet>
    <servlet-name>AS2PeppolReceiveServlet</servlet-name>
    <servlet-class>com.helger.as2servlet.AS2PeppolReceiveServlet</servlet-class>
    <init-param>
      <param-name>as2-servlet-config-filename</param-name>
      <param-value>as2-server-data/as2-server-config.xml</param-value>
    </init-param>
  </servlet>
  <servlet-mapping>
    <servlet-name>AS2PeppolReceiveServlet</servlet-name>
    <url-pattern>/as2/*</url-pattern>
  </servlet-mapping>
...  
```
As you can see, a configuration file called `as2-server-data/as2-server-config.xml` is referenced as an `init-param` of the servlet. The name of the `init-param` must be `as2-servlet-config-filename`. Please make sure to insert the correct absolute path to the configuration file inside the `param-value` element.
  
#Configuration file

A special XML configuration file must be used to configure the AS2 handling. 

Complete example configuration file:
 
```xml
<?xml version="1.0" encoding="utf-8"?>
<openas2>
  <!-- [required] The keystore to be used -->
  <certificates classname="com.helger.as2lib.cert.PKCS12CertificateFactory" 
                filename="%home%/server-certs.p12"
                password="peppol" />
  <!-- [required] The pro-forma partnership factory -->                  
  <partnerships classname="com.helger.as2servlet.util.AS2ServletPartnershipFactory"
                filename="%home%/server-partnerships.xml"
                disablebackup="true" />
 
  <!-- [required] the processing queue -->
  <processor classname="com.helger.as2lib.processor.DefaultMessageProcessor"
             pendingMDN="%home%/pendingMDN"
             pendingMDNinfo="%home%/pendinginfoMDN">
    <!-- [optional] Store sent MDNs to a file -->
    <module classname="com.helger.as2lib.processor.storage.MDNFileModule"
            filename="%home%/mdn/$date.yyyy$/$date.MM$/$mdn.msg.sender.as2_id$-$mdn.msg.receiver.as2_id$-$mdn.msg.headers.message-id$"      
            protocol="as2"
            tempdir="%home%/temp"/>
    <!-- [optional] Store received messages and headers to a file -->
    <module classname="com.helger.as2lib.processor.storage.MessageFileModule"
            filename="%home%/inbox/$msg.sender.as2_id$-$msg.receiver.as2_id$-$msg.headers.message-id$"
            header="%home%/inbox/msgheaders/$date.yyyy$/$date.MM$/$msg.sender.as2_id$-$msg.receiver.as2_id$-$msg.headers.message-id$"    
            protocol="as2"
            tempdir="%home%/temp"/>
    <!-- [required] The main receiver module that performs the message parsing.
         Note: the port attribute is required but can be ignored in our case!
     -->            
    <module classname="com.helger.as2servlet.util.AS2ServletReceiverModule"      
            port="10080"
            errordir="%home%/inbox/error"
            errorformat="$msg.sender.as2_id$, $msg.receiver.as2_id$, $msg.headers.message-id$"/>        
    <!-- [required] Process incoming SBD documents -->
    <module classname="com.helger.as2servlet.sbd.AS2ServletSBDModule" />      
  </processor>
</openas2>
```
  