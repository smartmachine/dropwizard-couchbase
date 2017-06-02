package io.smartmachine.couchbase;


import io.smartmachine.couchbase.spi.AccessorFactoryTest;
import io.smartmachine.couchbase.test.IntegrationTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Category(IntegrationTests.class)
public class CouchbaseClientFactoryTest {

    private static Logger log = LoggerFactory.getLogger(AccessorFactoryTest.class);

    @Test
    public void clientTest() throws Exception {
        CouchbaseClientFactory factory = new CouchbaseClientFactory();
        List<String> pool = new ArrayList<>();
        pool.add("http://localhost:8091/pools");
        factory.setBucketName("integration_test");
        factory.setHosts(pool);
        factory.setPassword("goliath");
        factory.start();
        factory.stop();

    }


}
