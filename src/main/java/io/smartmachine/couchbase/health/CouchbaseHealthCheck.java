package io.smartmachine.couchbase.health;

import com.codahale.metrics.health.HealthCheck;
import io.smartmachine.couchbase.CouchbaseClientFactory;

import java.net.InetSocketAddress;

public class CouchbaseHealthCheck extends HealthCheck {

    private CouchbaseClientFactory factory;

    public CouchbaseHealthCheck(CouchbaseClientFactory factory) {
        this.factory = factory;
    }

    @Override
    protected Result check() throws Exception {
        StringBuilder versionString = new StringBuilder();
        factory.client().getVersions().entrySet().stream().forEach(version -> {
                    if (versionString.length() > 0) {
                        versionString.append("; ");
                    }
                    versionString.append("Host: ")
                            .append(((InetSocketAddress)version.getKey()).getHostString())
                            .append(", Version: ")
                            .append(version.getValue());
                }
        );
        if (versionString.length() > 0) {
            return Result.healthy(versionString.toString());
        }
        return Result.unhealthy("No servers connected.");
    }
}
