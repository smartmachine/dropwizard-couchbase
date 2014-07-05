package io.smartmachine.couchbase.spi;

import io.smartmachine.couchbase.CouchbaseClientFactory;

import io.smartmachine.couchbase.api.TestAccessor;
import io.smartmachine.couchbase.api.TestAccessorImpl;
import io.smartmachine.couchbase.test.UnitTests;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.isA;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Mockito.mock;

@Category(UnitTests.class)
public class AccessorInvokerTest {

    private static Logger log = LoggerFactory.getLogger(AccessorInvokerTest.class);

    private TestAccessor accessor;
    private CouchbaseClientFactory factory;

    @Before
    public void setup() {
        factory = mock(CouchbaseClientFactory.class);
        accessor = (TestAccessor) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{io.smartmachine.couchbase.api.TestAccessor.class},
                new AccessorInvoker(mock(GenericAccessorImpl.class)));
    }

    @Test
    public void testProxy() {
        assertThat(accessor, isA(TestAccessor.class));
    }

    @Test
    public void testFallThrough() {
        log.info("Accessor object: " + accessor.toString());
        accessor.findAll();
    }



}
