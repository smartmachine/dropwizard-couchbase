package com.smartcore.dw.couchbase;

import com.couchbase.client.CouchbaseClient;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CouchbaseClientFactory implements Managed {

    private CouchbaseClient client = null;
    private static Logger log = LoggerFactory.getLogger(CouchbaseClientFactory.class);
    private CouchbaseConfiguration config;

    public CouchbaseClientFactory(CouchbaseConfiguration config) {
        this.config = config;
    }

    public CouchbaseClient client() {
        return client;
    }

    @Override
    public void start() throws Exception {
        log.info("Connecting to Couchbase -> hosts: " + config.getHosts() + " bucket: " + config.getBucket() + " password: " + config.getPassword());
        client = new CouchbaseClient(config.getHosts(), config.getBucket(), config.getPassword());
    }

    @Override
    public void stop() throws Exception {
        client.shutdown();
    }
}
