# dropwizard-couchbase

Build Status: [![Build Status](https://travis-ci.org/smartmachine/dropwizard-couchbase.svg?branch=master)](https://travis-ci.org/smartmachine/dropwizard-couchbase)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Coverage Status: [![Coverage Status](https://img.shields.io/coveralls/smartmachine/dropwizard-couchbase.svg)](https://coveralls.io/r/smartmachine/dropwizard-couchbase?branch=master)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Stories Ready: [![Stories in Ready](https://badge.waffle.io/smartmachine/dropwizard-couchbase.png?label=ready&title=Ready)](https://waffle.io/smartmachine/dropwizard-couchbase)
## Introduction
dropwizard-couchbase is a Dropwizard bundle for Couchbase persistence.

The current version is 0.2.3 and it has the following dependencies:

* io.dropwizard dropwizard-core **0.8.0-rc2** (provided)
* com.couchbase.client couchbase-client 1.4.6 (compile time)

If you are looking for the latest version compatible with **dropwizard 0.7.x**, it can be found [here](https://github.com/smartmachine/dropwizard-couchbase/tree/v0.2.2).

At the moment dropwizard-couchbase is compiled against JDK 8 because I love lambdas.

## Quickstart

### Dependencies

Add the following dependency to your build.gradle
``` groovy
dependencies {
  compile "io.smartmachine:dropwizard-couchbase:0.2.3"
}
```
or pom.xml
``` xml
<project>
  ...
  <dependencies>
    <dependency>
      <groupId>io.smartmachine</groupId>
      <artifactId>dropwizard-couchbase</artifactId>
      <version>0.2.3</version>
      <scope>runtime</scope>
    </dependency>
  </dependencies>
</project>

```

### Add bundle

Add a `CouchbaseBundle` to your `Application` class:

``` java
package io.sample;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.smartmachine.couchbase.CouchbaseBundle;

public class ConfigurationServer extends Application<ConfigurationServerConfig> {

    public static void main(String[] args) throws Exception {
        new ConfigurationServer().run(args);
    }

    private final CouchbaseBundle<ConfigurationServerConfig> couchbaseBundle = new CouchbaseBundle<ConfigurationServerConfig>() {

        @Override
        public CouchbaseClientFactory getCouchbaseClientFactory(ConfigurationServerConfig configuration) {
            return configuration.getCouchbaseClientFactory();
        }
    };

    @Override
    public void initialize(Bootstrap<ConfigurationServerConfig> bootstrap) {
        bootstrap.addBundle(couchbaseBundle);
    }

    @Override
    public void run(ConfigurationServerConfig configuration, Environment environment) throws Exception {
        // Register all your resources here as usual
    }

    @Override
    public String getName() {
        return "configuration-server";
    }
}
```

Implement your configuration class.

``` java
package io.smartmachine.cs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

class ConfigurationServerConfig extends Configuration {

    // All your usual setup goes here
    
    @Valid
    @NotNull
    private CouchbaseClientFactory ccf = new CouchbaseClientFactory();

    @JsonProperty("couchbase")
    public CouchbaseClientFactory getCouchbaseClientFactory() {
        return ccf;
    }

    @JsonProperty("couchbase")
    public void setCouchbaseClientFactory(CouchbaseClientFactory ccf) {
        this.ccf = ccf;
    }
}
```

### Configuration

Add the following to your yaml configuration file:

``` yaml
# Your server configuration
server:
  applicationConnectors:
    - type: http
      port: 9000
  adminConnectors:
    - type: http
      port: 9001

# Default Couchbase Configuration
couchbase:
  bucket: default
  hosts:
    - http://localhost:8091/pools
  password: ""
```
If you don't add a couchbase configuration section to your yaml the defaults (above) will be assumed.  More CouchbaseClient configuration options  will be added in the future.

### Implementation

#### Automatic DAO generation

dropwizard-couchbase will autogenerate DAO implementation classes and views for you to facilitate standard CRUD operations.

We assume that you have a standard dropwizard resource class as well as a model class with the appropriate `JsonProperty` and `JsonCreator` annotations for Jackson de/serialization. Let's look at such a class, called `Device.java`:

```java
package io.sample.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

public class  Device {

    @Length(min = 12, max=12)
    @NotEmpty
    private final String serial;

    private List<String> modules;

    @JsonCreator
    public Device(@JsonProperty("serial") String serial) {
        this.serial = serial;
    }

    @JsonProperty
    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    @JsonProperty
    public List<String> getModules() {
        return modules;
    }

    @JsonProperty
    public String getSerial() {
        return serial;
    }

}
```

In order to enable automatic DAO generation you need to write an Accessor interface (similar to DAO class/interface in JPA/Hibernate):

```java
package io.sample.api;

import io.smartmachine.couchbase.GenericAccessor;
import io.smartmachine.couchbase.ViewQuery;

import java.util.List;

public interface DeviceAccessor extends GenericAccessor<Device> {

    @ViewQuery("/^DEVICE/.test(meta.id)")
    public List<Device> findAll();

}
```

The `findall()` method will be autogenerated. The `@ViewQuery` annotation will generate a Couchbase Design Document called DEVICE with a view called findAll like so:

```javascript
function (doc, meta) {
  if (/^DEVICE/.test(meta.id)) {
    emit(meta.id, null);
  }
}
```

The emit statement can be controlled as well, `emit(meta.id, null)` is the default.  The following annotation will generate a view that emits full documents: `@ViewQuery("/^DEVICE/.test(meta.id)", emit = "emit(meta.id, doc)")`. Note that the name of the Design Document will be your model class `.toUpperCase()`.

The extended `GenericAccessor<Device>` interface has the following contract, all methods will be automatically implemented:

```java
package io.smartmachine.couchbase;

public interface GenericAccessor<T>  {

    void create(String id, T newinstance);
    T read(String id);
    void update(String id, T object);
    void delete(String id);
    void set(String id, T object);

}
```

Note that this is CRUD plus an extra set operation to conform with the provided methods of CouchbaseClient.

The last step is to annotate your Resource class as follows:

```java
package io.sample.resources;

import com.codahale.metrics.annotation.Timed;
import io.sample.api.Device;
import io.sample.api.DeviceAccessor;
import io.smartmachine.couchbase.Accessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/devices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceResource {

    private static final Logger log = LoggerFactory.getLogger(DeviceResource.class);

    // Note the Accessor annotation
    @Accessor
    private DeviceAccessor accessor;

    private final List<String> defaultModules;

    public DeviceResource(List<String> defaultModules) {
        this.defaultModules = defaultModules;
    }

    @GET
    @Path("{id}")
    @Timed
    public Device devices(@PathParam("id") String id) {
        return accessor.read(id);
    }

    @GET
    @Timed
    public List<Device> all() {
        return accessor.findAll();
    }

    @PUT
    @Path("{id}")
    @Timed
    public Device add(@PathParam("id") String id, @Valid Device device) {
        if (device.getModules() == null || device.getModules().size() == 0) {
            device.setModules(defaultModules);
        }
        accessor.create(id, device);
        return device;
    }

}
```

Note the @Accessor annotation in the above example.  dropwizard-couchbase will inject an implementation of DeviceAccessor for you to use in your `Resource` class.  

#### Manual usage of CouchbaseClient

If you want to do some more low-level stuff with `CouchbaseClient` you can use the following annotation in your Resource class in stead:

```java
import io.smartmachine.couchbase.CouchbaseClientFactory;
import io.smartmachine.couchbase.ClusterManager

public class DeviceResource {

    @Accessor
    private CouchbaseClientFactory factory;

    @GET
    @Path("{id}")
    @Timed
    public Device devices(@PathParam("id") String id) {
        CouchbaseClient client = factory.client();
        // and / or
        ClusterManager manager = factory.getClusterManager();
    }

    ...
    
}
```
## Roadmap for version 0.2.4

- [ ] Implement full set of configuration options for CouchbaseClientFactory
- [ ] Implement proper Couchbase health checks and metrics
- [ ] Finish out unit tests
- [ ] Update Javadocs

## Contributors

Pull requests are very welcome.  Create issues in the Github issue system for this repository against any bugs/feature requests.

## License

dropwizard-couchbase is released under the MIT license.
