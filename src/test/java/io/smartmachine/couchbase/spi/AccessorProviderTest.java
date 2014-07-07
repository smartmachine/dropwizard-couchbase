package io.smartmachine.couchbase.spi;

import com.sun.jersey.core.spi.component.ComponentScope;
import io.smartmachine.couchbase.CouchbaseClientFactory;
import io.smartmachine.couchbase.test.UnitTests;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Category(UnitTests.class)
public class AccessorProviderTest {

    @Mock
    private CouchbaseClientFactory factory;

    private AccessorProvider provider;

    @Before
    public void setup() {
        provider = new AccessorProvider(factory);
    }

    @Test
    public void testComponentScope() {
        assertThat(provider.getScope(), is(equalTo(ComponentScope.Singleton)));
     }

}
