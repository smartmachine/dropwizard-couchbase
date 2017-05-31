package io.smartmachine.couchbase.spi;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.CouchbaseAsyncCluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CouchbaseDriver  {

    private static final Logger log = LoggerFactory.getLogger(CouchbaseDriver.class);

    private volatile CouchbaseAsyncCluster cluster;
    private volatile AsyncBucket bucket;

    private static volatile CouchbaseDriver INSTANCE = null;

    private CouchbaseDriver() {}

    private CouchbaseDriver(List<String> seedNodes) {
        this.cluster = CouchbaseAsyncCluster.create(seedNodes);
    }

    private void setBucket(AsyncBucket bucket) {
        this.bucket = bucket;
    }

    public static CouchbaseDriver create(List<String> seedNodes, String bucketName, String password) {
        if (INSTANCE == null) {
            synchronized(CouchbaseDriver.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CouchbaseDriver(seedNodes);
                    INSTANCE.openBucket(bucketName, password).whenComplete((bucket, exception) -> {
                        if (exception == null) {
                            INSTANCE.setBucket(bucket);
                        } else {
                            throw new IllegalStateException("Unable to open bucket: " + bucketName, exception);
                        }
                    });
                }
            }
        }
        return INSTANCE;
    }

    private CompletableFuture<AsyncBucket> openBucket(String bucketName, String password) {
        log.info("Opening Bucket: " + bucketName);
        final CompletableFuture<AsyncBucket> future = new CompletableFuture<>();
        cluster.openBucket(bucketName, password)
                .doOnError(future::completeExceptionally)
                .single()
                .forEach(future::complete);
        return future;
    }

    public CompletableFuture<Boolean> close() {
        final CompletableFuture<Boolean> result = new CompletableFuture<>();
        cluster.disconnect()
                .doOnError(result::completeExceptionally)
                .single()
                .forEach(result::complete);
        return result;
    }

    public AsyncBucket bucket() {
        return bucket;
    }
}
