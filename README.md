# logback-nifi-appender

A Simple logback appender to publish data to NIFI using site-to-site protocol. [Site-to-Site Protocol](https://docs.hortonworks.com/HDPDocuments/HDF3/HDF-3.0.1.1/bk_user-guide/content/site-to-site.html) is more scalable, efficient means of communication with Nifi ports. Nifi on the other end can be used as a central tool to push data to multiple destinations.

With current state, there are two type of Centralized logging solutions - Agent based to tail files and Agent less utilizing the appender mechanism.

#### Agent Based
Utilizes an Agent running along side of the application.
- Pros
	- Executes out side of application
	- Data is hold even if the receiver is down

- Cons
	- consumes more memory/disk space
	- Might not be efficient with docker and micro- environments.

#### Agent Less:
- Pros
 	- No additional memory or process is needed
- Cons
	- Data can be lost if the receiver fails to accept data
	- Data can be lost if there is a network latency

This appender combines the best of the two style. It predominantly works as a Agent Less but can spin an Agent when the receiver fails to accept data. There are multiple Fallback Strategies that can be leveraged on Failure of receiver.

features
- Light Weight
- Leverages Site to Site protocol for auto discovery of Nifi cluster and transaction confirmations
- Fallback to a file if Nifi is unable to receive data.

## Full configuration example

Add `logback-nifi-appender` and `nifi-site-to-site-client` as  dependencies to your project in pom.

##### Dependency in pom.xml

```xml
<dependency>
    <groupId>com.github.logback.nifi.appender</groupId>
    <artifactId>logback-nifi-appender</artifactId>
    <version>0.0.1</version>
    <scope>runtime</scope>
</dependency>
<dependency>
	<groupId>org.apache.nifi</groupId>
	<artifactId>nifi-site-to-site-client</artifactId>
	<version>1.4.0</version>
</dependency>
```
##### Configuration in logback.xml
Below are the only required properties for this appender.
```xml
<appender name="NIFI_APPENDER" class="com.github.logback.NifiAppender">
	<url>http://localhost:8000/nifi/</url>
	<receiverPortName>FromLogback</receiverPortName>
	<receiverPortId>3f5c5ece-4e8f-1c7f-ffff-ffff8a1f130b</receiverPortId>
	<pushInterval>1000</pushInterval>
</appender>
```

##### FAQ

- __Q: Why another appender? Why not UDP appender?<br>__
 A: UDP appender can be user to push data to Nifi single host. But it cannot automatically discover cluster and distribute the data.

- __Q: Any other encoders supported?<br>__
 A: You can use any other encoders that come with logback and place it in the configuration for the appender.<br>

 - __Q: Does this hold data when nifi is down?<br>__
  A: Yes. This appender has multiple fallback implementations. Default one is a Console appender. But there is also a File appender which gets used when a nifi is down or unable to accept requests.<br>

- __Q: What is the retry mechanism?<br>__
   A: If Nifi server is down a fallback is used and main appender uses Exponential backoff Strategy for next retry.<br>


### References

- https://docs.hortonworks.com/HDPDocuments/HDF3/HDF-3.0.1.1/bk_user-guide/content/site-to-site.html
