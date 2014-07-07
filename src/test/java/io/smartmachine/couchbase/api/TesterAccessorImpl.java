package io.smartmachine.couchbase.api;


import io.smartmachine.couchbase.CouchbaseClientFactory;
import io.smartmachine.couchbase.spi.GenericAccessorImpl;

import java.util.List;

public class TesterAccessorImpl extends GenericAccessorImpl<Tester> implements TesterAccessor {

    public TesterAccessorImpl(Class<Tester> type, CouchbaseClientFactory factory) {
        super(type, factory);
    }

    @Override
    public List<Tester> findAll() {
        return null;
    }

}
