package io.smartmachine.couchbase;


import io.dropwizard.Configuration;

public interface CouchbaseConfiguration<T extends Configuration> {

    public CouchbaseClientFactory getCouchbaseClientFactory(T configuration);

}
