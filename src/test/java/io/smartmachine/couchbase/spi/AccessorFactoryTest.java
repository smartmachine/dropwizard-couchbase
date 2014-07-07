package io.smartmachine.couchbase.spi;


import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.View;
import com.couchbase.client.protocol.views.ViewResponse;
import com.couchbase.client.protocol.views.ViewRow;
import io.smartmachine.couchbase.CouchbaseClientFactory;
import io.smartmachine.couchbase.GenericAccessor;
import io.smartmachine.couchbase.api.TesterAccessor;
import io.smartmachine.couchbase.test.UnitTests;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
public class AccessorFactoryTest {

    private CouchbaseClientFactory factory;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        factory = mock(CouchbaseClientFactory.class);
        CouchbaseClient client = mock(CouchbaseClient.class);
        ViewResponse response = mock(ViewResponse.class);
        when(factory.client()).thenReturn(client);
        when(client.getView("TESTER", "findAll")).thenReturn(new View("", "", "findAll", false, false));
        when(client.query(any(), any())).thenReturn(response);
        when(response.iterator()).thenReturn(new ArrayList<ViewRow>().iterator());
    }


    @Test
    public void testReturnAccessor() {
        TesterAccessor accessor = AccessorFactory.getFactory().getAccessor(TesterAccessor.class, factory);
        assertThat(accessor, is(notNullValue()));
        assertThat(accessor, isA(GenericAccessor.class));
        assertThat(accessor.findAll(), instanceOf(List.class));
    }

    @Test
    public void testReturnFailure() {
        exception.expect(IllegalArgumentException.class);
        AccessorFactory.getFactory().getAccessor(String.class, factory);
    }

    @Test
    public void testConstructor() throws Exception {
        Constructor<AccessorFactory> constructor = AccessorFactory.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
