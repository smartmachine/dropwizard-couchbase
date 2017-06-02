package io.smartmachine.couchbase.spi;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CouchbaseDriver  {

    private static final Logger log = LoggerFactory.getLogger(CouchbaseDriver.class);

    private volatile CouchbaseCluster cluster;
    private volatile Bucket bucket;

    private static volatile CouchbaseDriver INSTANCE = null;

    private CouchbaseDriver() {}

    private CouchbaseDriver(List<String> seedNodes) {
        this.cluster = CouchbaseCluster.create(seedNodes);
    }

    private void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    public static CouchbaseDriver create(List<String> seedNodes, String bucketName, String password) {
        if (INSTANCE == null) {
            synchronized(CouchbaseDriver.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CouchbaseDriver(seedNodes);
                    INSTANCE.setBucket(INSTANCE.openBucket(bucketName, password));
                }
            }
        }
        return INSTANCE;
    }

    private Bucket openBucket(String bucketName, String password) {
        log.info("Opening Bucket: " + bucketName);
        return cluster.openBucket(bucketName, password);
    }

    public boolean close() {
        return cluster.disconnect();
    }

    public Bucket bucket() {
        return bucket;
    }
}
