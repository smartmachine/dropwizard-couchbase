package io.smartmachine.couchbase.cli;

import io.dropwizard.Configuration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import io.smartmachine.couchbase.CouchbaseClientFactory;
import io.smartmachine.couchbase.CouchbaseConfiguration;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

public class CouchbaseTestCommand<T extends Configuration> extends ConfiguredCommand<T> {

    private CouchbaseConfiguration<T> strategy = null;
    private Class<T> klass = null;

    public CouchbaseTestCommand(String name, String description, CouchbaseConfiguration<T> strategy, Class<T> klass) {
        super(name, description);
        this.strategy = strategy;
        this.klass = klass;
    }

    @Override
    protected Class<T> getConfigurationClass() {
        return klass;
    }

    @Override
    public void configure(Subparser subparser) {
        super.configure(subparser);

    }

    @Override
    protected void run(Bootstrap<T> bootstrap, Namespace namespace, T configuration) throws Exception {
        CouchbaseClientFactory factory = strategy.getCouchbaseClientFactory(configuration);
        factory.start();
        Thread.sleep(1000);
        factory.stop();
    }
}
