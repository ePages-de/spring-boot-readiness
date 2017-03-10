# Spring Boot Readiness checks

This app is calling a number of remote services for their [Spring Boot Actuator](http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready.html) health endpoints
to aggregate a combined readiness state. It includes a build process for creating a Docker image for easy execution.

The app offers two modes of operation: command line interface (`cli`) or web interface (`dashboard`).
In `cli` mode it prints out the readiness check results to the console as a YAML document.
In `dashboard` mode it starts as a daemon and offers an HTTP port (default: 2112) for HTML and JSON results.

## Configure target platform

The app uses [externalized configuration](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) for setting up the remote services to lookup.
Put a configuration file specific to the target platform as a YAML document named `application-${platform}.yml` into the local folder `./config`,
where `${platform}` is an arbitrary name identifying your target. See `./config/application-example.com.yml` as a reference.

You specify what platform to use by the configuration property called `platform`. 
This can be introduced as a Java System Property (`-Dplatform=example.com`), 
environment variable (`PLATFORM=example.com`) or Java command argument (`--platform=example.com`).

The supported configuration options are:

```
readiness:
  connectionTimeout: 100
  readTimeout: 200
  connectionRequestTimeout: 500
  services:
    - service: 'users'
      uri: 'https://example.com/users/health.json'
    - service: 'items'
      uri: 'https://example.com/items/health.json'
```

If your services use self-signed SSL certificates, you need to relax the security of this app:

```
spring:
  profiles:
    include: 'insecure'

readiness:
  services:
    - service: 'users'
      uri: 'https://example.com/users/health.json'
```

Since the `./config` directory is not part of the resulting Docker image, you need to mount it
into the running Docker container as a volume. See `docker-compose.yml` how to do this.

## Running in dashboard mode

In `dashboard` mode you can view the combined readiness check result presented as a single HTML page.
Append a query parameter `?refresh=5` to let your browser automatically refresh the output every 5 seconds.

You can use `docker-compose` to start the app for a specific target platform:

```
$ docker-compose run -d --service-ports --name readiness_dashboard dashboard --platform=example.com
$ open http://192.168.99.100:2112/readiness.html?refresh=5
$ docker logs -f readiness_dashboard
```

The REST endpoint returns a JSON document and can be accessed using a different URI:

```
$ http http://192.168.99.100:2112/readiness.json
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8
Date: Fri, 10 Mar 2017 08:57:06 GMT
Transfer-Encoding: chunked

{
    "status": "UP",
    "readiness": {
        "platform": "example.com", 
        "status": "UP", 
        "totalTimeMillis": 268
        "users": {
            "application": {
                "status": "UP"
            }, 
            "hystrix": {
                "status": "UP"
            }, 
            "refreshScope": {
                "status": "UP"
            }, 
            "status": "UP", 
            "totalTimeMillis": 24, 
            "uri": "https://example.com/users/health.json"
        }, 
        "items": {
            "db": {
                "database": "MySQL", 
                "hello": 1, 
                "status": "UP"
            }, 
            "rabbit": {
                "status": "UP", 
                "version": "3.5.8"
            }, 
            "refreshScope": {
                "status": "UP"
            }, 
            "status": "UP", 
            "totalTimeMillis": 189, 
            "uri": "https://example.com/items/health.json"
        }
    }
}
```

## Running in cli mode

In `cli` mode the app executes a single run of the combined readiness check 
and prints the resulting YAML document to `stdout`.

You can use `docker-compose` to start the app for a specific target platform:

```
$ docker-compose run --rm  --name readiness_cli cli --platform=example.com
Starting ngreadiness_gradle_1
Picked up JAVA_TOOL_OPTIONS: -Xmx256m -Xss512k -Dfile.encoding=UTF-8 -XX:+UseCompressedOops -XX:+PrintCommandLineFlags
-XX:InitialHeapSize=130683392 -XX:MaxHeapSize=268435456 -XX:+PrintCommandLineFlags -XX:ThreadStackSize=512 -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseParallelGC
09:57:31.035 [main] DEBUG org.springframework.core.env.PropertySourcesPropertyResolver - Found key 'logging.pattern.console' in [applicationConfigurationProperties] with type [String]
---
readiness:
  status: "UP"
  platform: "example.com"
  totalTimeMillis: 795
  users:
    status: "UP"
    uri: "https://example.com/users/health.json"
    totalTimeMillis: 50
    application:
      status: "UP"
    refreshScope:
      status: "UP"
    hystrix:
      status: "UP"
  items:
    status: "UP"
    uri: "https://example.com/items/health.json"
    totalTimeMillis: 656
    rabbit:
      status: "UP"
      version: "3.5.8"
    db:
      status: "UP"
      database: "MySQL"
      hello: 1
    refreshScope:
      status: "UP"
```

## Debug config file loading

Sometimes it's difficult to identify why a desired configuration option is not being applied. 
In this case increase the log level to find out which configuration files are read on app startup: 

```
$ docker-compose run --rm cli --logging.level.org.springframework.boot.context.config.ConfigFileApplicationListener=TRACE --platform=example.com | grep "profiles\|Loaded\|empty"
Starting ngreadiness_gradle_1
Activated profiles cli
Loaded config file 'jar:file:/root/app.jar!/BOOT-INF/classes!/application.yml' (classpath:/application.yml)
Loaded config file 'file:./config/application-example.com.yml' (file:./config/application-example.com.yml)
Skipped (empty) config file 'file:./config/application-example.com.yml' (file:./config/application-example.com.yml) for profile example.com
Skipped (empty) config file 'jar:file:/root/app.jar!/BOOT-INF/classes!/application.yml' (classpath:/application.yml) for profile example.com
Skipped (empty) config file 'file:./config/application-example.com.yml' (file:./config/application-example.com.yml) for profile insecure
Skipped (empty) config file 'jar:file:/root/app.jar!/BOOT-INF/classes!/application.yml' (classpath:/application.yml) for profile insecure
Skipped (empty) config file 'file:./config/application-example.com.yml' (file:./config/application-example.com.yml) for profile cli
Loaded config file 'jar:file:/root/app.jar!/BOOT-INF/classes!/application-cli.yml' (classpath:/application-cli.yml)
Skipped (empty) config file 'jar:file:/root/app.jar!/BOOT-INF/classes!/application-cli.yml' (classpath:/application-cli.yml) for profile cli
Skipped (empty) config file 'jar:file:/root/app.jar!/BOOT-INF/classes!/application.yml' (classpath:/application.yml) for profile cli
```
