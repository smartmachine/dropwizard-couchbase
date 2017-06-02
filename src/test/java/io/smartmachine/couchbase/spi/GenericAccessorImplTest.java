package io.smartmachine.couchbase.spi;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.Document;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.view.View;
import com.couchbase.client.java.view.ViewRow;
import io.smartmachine.couchbase.CouchbaseClientFactory;
import io.smartmachine.couchbase.api.BrokenTester;
import io.smartmachine.couchbase.api.Tester;
import io.smartmachine.couchbase.test.UnitTests;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@FixMethodOrder(MethodSorters.JVM)
public class GenericAccessorImplTest {

    private GenericAccessorImpl<Tester> impl;

    @Mock
    private CouchbaseClientFactory factory;

    @Mock
    private Bucket bucket;

    @Mock
    private View view;

    @Mock
    private ViewRow viewRow;

    private RawJsonDocument doc;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(factory.bucket()).thenReturn(bucket);
        impl = new GenericAccessorImpl<>(Tester.class, factory);
        doc = RawJsonDocument.create("TESTER:ABC123", fixture("fixtures/tester.json"));
    }

    @Test
    public void testCreate() throws Exception {

        when(bucket.insert(any(RawJsonDocument.class))).thenReturn(any());
        impl.create("ABC123", new Tester("testerName", "testerOtherProperty"));
        verify(bucket).insert(doc);
    }

    @Test
    public void testRead() throws Exception {
        when(bucket.get("TESTER:ABC123", RawJsonDocument.class)).thenReturn(doc);
        Tester tester = impl.read("ABC123");
        assertThat(tester.getName(), is("testerName"));
        assertThat(tester.getSomeOtherProperty(), is("testerOtherProperty"));
        verify(bucket).get("TESTER:ABC123", RawJsonDocument.class);
    }

    @Test
    public void testUpdate() throws Exception {
        when(bucket.replace(any(Document.class))).thenReturn(any(Document.class));
        impl.update("ABC123", new Tester("testerName", "testerOtherProperty"));
        verify(bucket).replace(RawJsonDocument.create("TESTER:ABC123", fixture("fixtures/tester.json")));
    }

    @Test
    public void testDelete() throws Exception {
        when(bucket.remove(anyString())).thenReturn(any(JsonDocument.class));
        impl.delete("ABC123");
        verify(bucket).remove("TESTER:ABC123");
    }

    @Test
    public void testSet() throws Exception {
        when(bucket.upsert(doc)).thenReturn(doc);
        impl.set("ABC123", new Tester("testerName", "testerOtherProperty"));
        verify(bucket).upsert(doc);
    }

    @Test
    public void testReadException() throws Exception {
        when(bucket.get("TESTER:ABC123BROKEN", RawJsonDocument.class)).thenReturn(RawJsonDocument.create("TESTER:ABC123BROKEN", "{ \"suzie\": \"q\" }"));
        exception.expect(IllegalStateException.class);
        impl.read("ABC123BROKEN");
    }

    @Test
    public void testReadNull() throws Exception {
        when(bucket.get(anyString())).thenReturn(null);
        assertThat(impl.read("ABC123"), is(nullValue()));
    }

    @Test
    public void testCreateBroken() throws Exception {
        when(bucket.insert(doc)).thenReturn(doc);
        GenericAccessorImpl<BrokenTester> borkedImpl = new GenericAccessorImpl<>(BrokenTester.class, factory);
        exception.expect(IllegalStateException.class);
        borkedImpl.create("ABC123", new BrokenTester("testerName", "testerOtherProperty"));
    }

    /**
    @Test
    public void testCacheViews() throws Exception {
        when(client.getView(anyString(), anyString()))
                .thenThrow(new InvalidViewException("View not found."))
                .thenReturn(view);
        when(client.getDesignDoc("TESTER")).thenReturn(new DesignDocument("TESTER"));
        impl.cacheViews(TesterAccessor.class);
    }

    @Test
    public void testCacheViewsNoDesignDoc() throws Exception {
        when(client.getView(anyString(), anyString()))
                .thenThrow(new InvalidViewException("View not found."))
                .thenReturn(view);
        when(client.getDesignDoc("TESTER")).thenThrow(new InvalidViewException("Design document not found."));
        impl.cacheViews(TesterAccessor.class);
    }

    @Test
    public void testExecuteFinder() throws Exception {
        testCacheViews();
        when(client.query(any(), any())).thenReturn(viewResponse);
        when(viewResponse.iterator()).thenReturn(Lists.newArrayList(viewRow).iterator());
        when(viewRow.getDocument()).thenReturn(fixture("fixtures/tester.json"));
        List<Tester> testers = impl.executeFinder(TesterAccessor.class.getMethod("findAll"), null);
        assertThat(testers.size(), is(1));
        assertThat(testers.get(0).getName(), is("testerName"));
        assertThat(testers.get(0).getSomeOtherProperty(), is("testerOtherProperty"));
    }

    @Test
    public void testExecuteFinderMalformed() throws Exception {
        testCacheViews();
        when(client.query(any(), any())).thenReturn(viewResponse);
        when(viewResponse.iterator()).thenReturn(Lists.newArrayList(viewRow).iterator());
        when(viewRow.getDocument()).thenReturn("{ ke? }");
        exception.expect(IllegalStateException.class);
        impl.executeFinder(TesterAccessor.class.getMethod("findAll"), null);
    }

    @Test
    public void testExecuteFinderNotCached() throws Exception {
        exception.expect(IllegalStateException.class);
        impl.executeFinder(TesterAccessor.class.getMethod("findAll"), null);
    }

    */
}
