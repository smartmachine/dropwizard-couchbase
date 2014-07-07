package io.smartmachine.couchbase.spi;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import io.smartmachine.couchbase.Accessor;
import io.smartmachine.couchbase.CouchbaseClientFactory;
import io.smartmachine.couchbase.GenericAccessor;
import io.smartmachine.couchbase.api.TesterAccessor;
import io.smartmachine.couchbase.resources.TesterResource;
import io.smartmachine.couchbase.test.UnitTests;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
public class AccessorProviderTest {

    @Mock
    private CouchbaseClientFactory factory;

    @Mock
    private ComponentContext context;

    @Mock
    private AccessorFactory af;

    @Mock
    private TesterAccessor ta;

    private AccessorProvider provider;


    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        provider = new AccessorProvider(af, factory);
        when(context.getAnnotations()).thenReturn(new Annotation[] { new Accessor() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Accessor.class;
            }
        }});


    }

    @Test
    public void testComponentScope() {
        assertThat(provider.getScope(), is(equalTo(ComponentScope.Singleton)));
     }

    @Test
    public void testGetInjectableCouchbaseClientFactory() throws Exception {
        Field field = TesterResource.class.getField("factory");
        Accessor accessor = (Accessor) field.getDeclaredAnnotations()[0];
        when(context.getAccesibleObject()).thenReturn(field);
        Injectable injectable = provider.getInjectable(context, accessor, field.getGenericType());
        CouchbaseClientFactory receivedFactory = (CouchbaseClientFactory) injectable.getValue();
        assertThat(receivedFactory, is(equalTo(factory)));
        assertThat(receivedFactory, is(sameInstance(factory)));
    }

    @Test
    public void testGetInjectableAccessor() throws Exception {
        Field field = TesterResource.class.getField("accessor");
        Accessor accessor = (Accessor) field.getDeclaredAnnotations()[0];
        when(context.getAccesibleObject()).thenReturn(field);
        when(af.getAccessor(TesterAccessor.class, factory)).thenReturn(ta);
        Injectable injectable = provider.getInjectable(context, accessor, field.getGenericType());
        TesterAccessor ta = (TesterAccessor) injectable.getValue();
        assertThat(ta, isA(GenericAccessor.class));
    }

}
