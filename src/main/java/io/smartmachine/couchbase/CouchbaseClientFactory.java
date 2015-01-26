package io.smartmachine.couchbase;

import com.couchbase.client.ClusterManager;
import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;
import com.couchbase.client.CouchbaseConnectionFactoryBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.lifecycle.Managed;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CouchbaseClientFactory implements Managed {

    private CouchbaseClient client = null;
    private static Logger log = LoggerFactory.getLogger(CouchbaseClientFactory.class);
    private ClusterManager manager;


    public CouchbaseClient client() {
        return client;
    }

    public ClusterManager getClusterManager() {
        return manager;
    }

    @Valid
    @NotNull
    private List<URI> hosts = new ArrayList<>();

    {
        try {
            hosts.add(new URI("http://localhost:8091/pools"));
        } catch (URISyntaxException e) {
            hosts = null;
        }
    }

    @Valid
    @NotEmpty
    private String bucket = "default";

    @Valid
    private String password = null;

    @JsonProperty
    public List<URI> getHosts() {
        return hosts;
    }

    @JsonProperty
    public void setHosts(List<URI> hosts) {
        this.hosts = hosts;
    }

    @JsonProperty
    public String getBucket() {
        return bucket;
    }

    @JsonProperty
    public void setBucket(String bucket) {
        this.bucket = bucket;
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
        log.info("Connecting to Couchbase -> hosts: " + hosts + " bucket: " + bucket + " password: *****");
        CouchbaseConnectionFactoryBuilder builder = new CouchbaseConnectionFactoryBuilder();
        CouchbaseConnectionFactory factory = builder.buildCouchbaseConnection(hosts, bucket, password);
        manager = factory.getClusterManager();
        client = new CouchbaseClient(factory);
    }

    public void stop() throws Exception {
        manager.shutdown();
        client.shutdown(30, TimeUnit.SECONDS);
    }


}
