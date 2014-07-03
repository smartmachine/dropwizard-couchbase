package io.smartmachine.couchbase.spi;


import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.*;
import io.smartmachine.couchbase.CouchbaseClientFactory;
import io.smartmachine.couchbase.GenericAccessor;
import io.smartmachine.couchbase.api.TestAccessor;
import io.smartmachine.couchbase.test.UnitTests;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
public class AccessorFactoryTest {

    private static Logger log = LoggerFactory.getLogger(AccessorFactoryTest.class);
    private CouchbaseClientFactory factory;

    @Before
    public void setup() {
        factory = mock(CouchbaseClientFactory.class);
        CouchbaseClient client = mock(CouchbaseClient.class);
        ViewResponse response = mock(ViewResponse.class);
        when(factory.client()).thenReturn(client);
        when(client.getView("TEST", "findAll")).thenReturn(new View("", "", "findAll", false, false));
        when(client.query(any(), any())).thenReturn(response);
        when(response.iterator()).thenReturn(new ArrayList<ViewRow>().iterator());
    }


    @Test
    public void testAccessorFactory() {
        TestAccessor accessor = AccessorFactory.getAccessor(TestAccessor.class, factory);
        assertThat(accessor, is(notNullValue()));
        assertThat(accessor, instanceOf(GenericAccessor.class));
        assertThat(accessor.findAll(), instanceOf(List.class));
    }

}
