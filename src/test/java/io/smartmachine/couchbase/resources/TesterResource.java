package io.smartmachine.couchbase.resources;


import io.smartmachine.couchbase.Accessor;
import io.smartmachine.couchbase.CouchbaseClientFactory;
import io.smartmachine.couchbase.api.TesterAccessor;

public class TesterResource {

    @Accessor
    public CouchbaseClientFactory factory;

    @Accessor
    public TesterAccessor accessor;

}
