package com.smartcore.dw.couchbase;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface CouchbaseBundleConfiguration {

    @JsonProperty
    public CouchbaseConfiguration getCouchbaseConfiguration();

}
