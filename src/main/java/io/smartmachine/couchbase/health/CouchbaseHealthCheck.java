package io.smartmachine.couchbase.health;

import com.codahale.metrics.health.HealthCheck;
import io.smartmachine.couchbase.CouchbaseClientFactory;

public class CouchbaseHealthCheck extends HealthCheck {

    private CouchbaseClientFactory factory;

    public CouchbaseHealthCheck(CouchbaseClientFactory factory) {
        this.factory = factory;
    }

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
