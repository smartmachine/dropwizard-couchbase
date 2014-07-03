package io.smartmachine.couchbase;


import io.smartmachine.couchbase.test.UnitTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Category(UnitTests.class)
public class FooTest {

    private static Logger log = LoggerFactory.getLogger(FooTest.class);

    @Test
    public void thisAlwaysPasses() {
        log.info("This test always passes.");
    }

}
