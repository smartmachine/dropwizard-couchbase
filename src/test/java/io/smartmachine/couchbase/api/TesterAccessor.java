package io.smartmachine.couchbase.api;

import io.smartmachine.couchbase.GenericAccessor;
import io.smartmachine.couchbase.ViewQuery;

import java.util.List;

public interface TesterAccessor extends GenericAccessor<Tester> {

    @ViewQuery("(/^TEST/).test(meta.id)")
    List<Tester> findAll();

}
