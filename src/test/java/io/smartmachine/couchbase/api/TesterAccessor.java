package io.smartmachine.couchbase.api;

import io.smartmachine.couchbase.CouchbaseView;
import io.smartmachine.couchbase.GenericAccessor;

import java.util.List;

public interface TesterAccessor extends GenericAccessor<Tester> {

    @CouchbaseView("(/^TEST/).test(meta.id)")
    List<Tester> findAll();

}
