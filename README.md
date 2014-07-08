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
