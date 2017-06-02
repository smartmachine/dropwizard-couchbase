package io.smartmachine.couchbase.spi;


import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.bucket.BucketManager;
import com.couchbase.client.java.view.*;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
public class AccessorFactoryTest {

    private CouchbaseClientFactory factory;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {

        List<View> views = new ArrayList<>();
        String mapBuilder = "function (doc, meta) {\n" +
                "  if ((/^TESTER/).test(meta.id)) {\n" +
                "    emit(meta.id, null);\n" +
                "  }\n" +
                "}";
        View view = DefaultView.create("findAll", mapBuilder);
        views.add(view);

        factory = mock(CouchbaseClientFactory.class);
        BucketManager mgr = mock(BucketManager.class);
        DesignDocument design = mock(DesignDocument.class);
        Bucket bucket = mock(Bucket.class);
        ViewResult result = mock(ViewResult.class);
        List<ViewRow> rows = new ArrayList<>();
        ViewRow row = mock(ViewRow.class);
        rows.add(row);


        when(factory.bucket()).thenReturn(bucket);
        when(bucket.bucketManager()).thenReturn(mgr);
        when(mgr.getDesignDocument("TESTER")).thenReturn(design);
        when(design.views()).thenReturn(views);
        when(bucket.query(ViewQuery.from("TESTER", "findAll"))).thenReturn(result);
        when(result.allRows()).thenReturn(rows);
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
