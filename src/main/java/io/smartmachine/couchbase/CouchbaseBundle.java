package io.smartmachine.couchbase;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Generics;
import io.smartmachine.couchbase.cli.CouchbaseInitCommand;
import io.smartmachine.couchbase.cli.CouchbaseTestCommand;
import io.smartmachine.couchbase.health.CouchbaseHealthCheck;
import io.smartmachine.couchbase.spi.AccessorResolver;

public abstract class CouchbaseBundle<T extends Configuration> implements ConfiguredBundle<T>, CouchbaseConfiguration<T> {


    @Override
    public final void initialize(Bootstrap<?> bootstrap) {
        final Class<T> klass = Generics.getTypeParameter(getClass(), Configuration.class);
        bootstrap.addCommand(new CouchbaseTestCommand<>("cbtest", "Tests the connection to Couchbase.", this, klass));
        bootstrap.addCommand(new CouchbaseInitCommand<>("cbinit", "Syncs all design documents with Couchbase.", this, klass));
    }

    @Override
    public final void run(T bundleConfig, Environment environment) throws Exception {
        final CouchbaseClientFactory factory = getCouchbaseClientFactory(bundleConfig);
        final CouchbaseHealthCheck couchbaseHealthCheck = new CouchbaseHealthCheck(factory);
        environment.lifecycle().manage(factory);
        environment.healthChecks().register("couchbase", couchbaseHealthCheck);
        environment.jersey().register(new AccessorResolver.Binder(factory));
    }

}