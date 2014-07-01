package io.smartmachine.couchbase;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface CouchbaseBundleConfiguration {

    @JsonProperty("couchbase")
    public CouchbaseConfiguration getCouchbaseConfiguration();

}
