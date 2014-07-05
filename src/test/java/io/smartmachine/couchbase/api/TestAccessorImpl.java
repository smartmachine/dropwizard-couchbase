package io.smartmachine.couchbase.api;


import io.smartmachine.couchbase.CouchbaseClientFactory;
import io.smartmachine.couchbase.spi.GenericAccessorImpl;

import java.util.List;

public class TestAccessorImpl extends GenericAccessorImpl<Test> implements TestAccessor {

    public TestAccessorImpl(Class<Test> type, CouchbaseClientFactory factory) {
        super(type, factory);
    }

    @Override
    public List<Test> findAll() {
        return null;
    }

}
