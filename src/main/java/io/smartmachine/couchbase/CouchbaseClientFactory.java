package io.smartmachine.couchbase;

import com.couchbase.client.ClusterManager;
import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;
import com.couchbase.client.CouchbaseConnectionFactoryBuilder;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CouchbaseClientFactory implements Managed {

    private CouchbaseClient client = null;
    private static Logger log = LoggerFactory.getLogger(CouchbaseClientFactory.class);
    private CouchbaseConfiguration config;
    private ClusterManager manager;

    public CouchbaseClientFactory(CouchbaseConfiguration config) {
        this.config = config;
    }

    public CouchbaseClient client() {
        return client;
    }

    public ClusterManager getClusterManager() {
        return manager;
    }

    @Override
    public void start() throws Exception {
        log.info("Connecting to Couchbase -> hosts: " + config.getHosts() + " bucket: " + config.getBucket() + " password: " + config.getPassword());
        CouchbaseConnectionFactoryBuilder builder = new CouchbaseConnectionFactoryBuilder();
        CouchbaseConnectionFactory factory = builder.buildCouchbaseConnection(config.getHosts(), config.getBucket(), config.getPassword());
        manager = factory.getClusterManager();
        client = new CouchbaseClient(factory);
    }

    @Override
    public void stop() throws Exception {
        client.shutdown();
    }
}
