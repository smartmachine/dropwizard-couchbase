package io.smartmachine.couchbase.api;

import io.smartmachine.couchbase.GenericAccessor;
import io.smartmachine.couchbase.ViewQuery;

import java.util.List;

public interface TestAccessor extends GenericAccessor<Test> {

    @ViewQuery("(/^TEST/).test(meta.id)")
    List<Test> findAll();

}
