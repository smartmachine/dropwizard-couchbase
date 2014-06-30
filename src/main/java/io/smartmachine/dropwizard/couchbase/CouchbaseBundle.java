package io.smartmachine.dropwizard.couchbase;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class CouchbaseBundle implements ConfiguredBundle<CouchbaseBundleConfiguration> {

    @Override
    public final void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public final void run(CouchbaseBundleConfiguration bundleConfig, Environment environment) throws Exception {
        final CouchbaseConfiguration config = bundleConfig.getCouchbaseConfiguration();
        final CouchbaseClientFactory factory = new CouchbaseClientFactory(config);
        final CouchbaseHealthCheck couchbaseHealthCheck = new CouchbaseHealthCheck(factory);
        environment.lifecycle().manage(factory);
        environment.healthChecks().register("couchbase", couchbaseHealthCheck);
        environment.jersey().register(new AccessorProvider(factory));
    }

}