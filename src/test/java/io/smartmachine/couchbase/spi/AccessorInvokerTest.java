package io.smartmachine.couchbase.spi;

import io.smartmachine.couchbase.api.Tester;
import io.smartmachine.couchbase.api.TesterAccessor;
import io.smartmachine.couchbase.api.TesterAccessorBroken;
import io.smartmachine.couchbase.test.UnitTests;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

@Category(UnitTests.class)
public class AccessorInvokerTest {

    private static Logger log = LoggerFactory.getLogger(AccessorInvokerTest.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private GenericAccessorImpl<Tester> accessor;

    private Method TO_STRING;
    private Method FIND_ALL;
    private Method GET_ALL;


    private AccessorInvoker invoker;
    private Class[] NO_ARGS = new Class[] {};


    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        invoker = new AccessorInvoker(accessor);

        TO_STRING = Object.class.getMethod("toString");
        FIND_ALL = TesterAccessor.class.getMethod("findAll");
        GET_ALL = TesterAccessorBroken.class.getMethod("getAll");
    }

    @Test
    public void testFallThrough() throws Throwable {
        invoker.invoke(accessor, TO_STRING, NO_ARGS);
    }

    @Test
    public void testFinder() throws Throwable {
        invoker.invoke(accessor, FIND_ALL, NO_ARGS);
    }

    @Test
    public void testFunk() throws Throwable {
        exception.expect(IllegalArgumentException.class);
        invoker.invoke(accessor, GET_ALL, NO_ARGS);
    }



}
