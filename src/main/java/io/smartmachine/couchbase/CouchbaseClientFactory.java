package io.smartmachine.couchbase;

import com.couchbase.client.java.AsyncBucket;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.lifecycle.Managed;
import io.smartmachine.couchbase.spi.CouchbaseDriver;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CouchbaseClientFactory implements Managed {

    private volatile CouchbaseDriver driver = null;

    private static Logger log = LoggerFactory.getLogger(CouchbaseClientFactory.class);


    @Valid
    @NotNull
    private List<String> hosts = new ArrayList<>();

    @Valid
    @NotEmpty
    private String bucketName = "default";

    @Valid
    private String password = null;

    @JsonProperty
    public List<String> getHosts() {
        return hosts;
    }

    @JsonProperty
    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    @JsonProperty
    public String getBucketName() {
        return bucketName;
    }

    @JsonProperty
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public void start() throws Exception {
        log.info("Connecting to Couchbase -> hosts: " + hosts + " bucket: " + bucketName + " password: *****");
        driver = CouchbaseDriver.create(hosts, bucketName, password);
    }

    public void stop() throws Exception {
        log.info("Disconnecting from Couchbase Cluster");
        driver.close().get();
    }


    public AsyncBucket bucket() {
        return driver.bucket();
    }
}
