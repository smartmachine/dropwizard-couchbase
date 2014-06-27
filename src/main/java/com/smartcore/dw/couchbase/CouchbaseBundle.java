package com.smartcore.dw.couchbase;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class CouchbaseBundle implements ConfiguredBundle<CouchbaseBundleConfiguration> {

    private CouchbaseClientFactory factory;

    @Override
    public final void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public final void run(CouchbaseBundleConfiguration bundleConfig, Environment environment) throws Exception {
        CouchbaseConfiguration config = bundleConfig.getCouchbaseConfiguration();
        factory = new CouchbaseClientFactory(config);
        final CouchbaseHealthCheck couchbaseHealthCheck = new CouchbaseHealthCheck(factory);
        environment.lifecycle().manage(factory);
        environment.healthChecks().register("couchbase", couchbaseHealthCheck);
    }

    public CouchbaseClientFactory getFactory() {
        return factory;
    }

}