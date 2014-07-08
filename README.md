# dropwizard-couchbase

Build Status: [![Build Status](https://travis-ci.org/smartmachine/dropwizard-couchbase.svg?branch=master)](https://travis-ci.org/smartmachine/dropwizard-couchbase)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Coverage Status: [![Coverage Status](https://img.shields.io/coveralls/smartmachine/dropwizard-couchbase.svg)](https://coveralls.io/r/smartmachine/dropwizard-couchbase?branch=master)


## Introduction
dropwizard-couchbase is Dropwizard bundle for Couchbase persistence.

The current version is 0.2.2 and it has the following dependencies:

* io.dropwizard dropwizard-core 0.7.1 (provided)
* com.couchbase.client couchbase-client 1.4.3 (compile time)

At the moment dropwizard-couchbase is compiled against JDK 8 because I love lambdas.

## Quickstart

Add the following dependency to your build.gradle
``` groovy
dependencies {
  compile "io.smartmachine:dropwizard-couchbase:0.2.2"
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
      <version>0.2.2</version>
      <scope>runtime</scope>
    </dependency>
  </dependencies>
</project>
```
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

    private final CouchbaseBundle couchbaseBundle = new CouchbaseBundle();

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
Implement `CouchbaseBundleConfiguration` in your configuration class.
``` java
package io.smartmachine.cs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.smartmachine.couchbase.CouchbaseBundleConfiguration;
import io.smartmachine.couchbase.CouchbaseConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

// Make sure to implement CouchbaseBundleConfiguration
class ConfigurationServerConfig extends Configuration implements CouchbaseBundleConfiguration {

    // All your usual setup goes here
    
    // Add this to your configuration class
    @Valid
    @NotNull
    private CouchbaseConfiguration cbc = new CouchbaseConfiguration();

    @JsonProperty("couchbase")
    public CouchbaseConfiguration getCouchbaseConfiguration() {
        return cbc;
    }

    @JsonProperty("couchbase")
    public void setCouchbaseConfiguration(CouchbaseConfiguration cbc) {
        this.cbc = cbc;
    }
}
```
