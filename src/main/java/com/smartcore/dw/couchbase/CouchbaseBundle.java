package com.smartcore.dw.couchbase;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public abstract class CouchbaseBundle<T extends Configuration> implements ConfiguredBundle<T> {

    protected abstract CouchbaseClientFactory couchbaseConfiguration(T configuration);

    private CouchbaseClientFactory factory;

    @Override
    public final void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public final void run(T configuration, Environment environment) throws Exception {
        factory = couchbaseConfiguration(configuration);
        final CouchbaseHealthCheck couchbaseHealthCheck = new CouchbaseHealthCheck(factory);
        environment.lifecycle().manage(factory);
        environment.healthChecks().register("couchbase", couchbaseHealthCheck);
    }

    public CouchbaseClientFactory getFactory() {
        return factory;
    }

}