package io.smartmachine.couchbase.spi;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.*;
import com.google.common.collect.Lists;
import io.smartmachine.couchbase.CouchbaseClientFactory;
import io.smartmachine.couchbase.api.BrokenTester;
import io.smartmachine.couchbase.api.Tester;
import io.smartmachine.couchbase.api.TesterAccessor;
import io.smartmachine.couchbase.test.UnitTests;
import net.spy.memcached.internal.OperationCompletionListener;
import net.spy.memcached.internal.OperationFuture;
import net.spy.memcached.ops.OperationStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
public class GenericAccessorImplTest {

    private GenericAccessorImpl<Tester> impl;

    @Mock
    private CouchbaseClientFactory factory;

    @Mock
    private CouchbaseClient client;

    @Mock
    private OperationFuture<Boolean> future;

    @Mock
    private View view;

    @Mock
    private ViewResponse viewResponse;

    @Mock
    private ViewRow viewRow;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(factory.client()).thenReturn(client);
        impl = new GenericAccessorImpl<>(Tester.class, factory);
    }

    @Test
    public void testCreate() throws Exception {
        when(client.add(anyString(), anyString())).thenReturn(future);
        final OperationCompletionListener[] listener = new OperationCompletionListener[1];
        when(future.addListener(any())).thenAnswer(invocation -> {
            listener[0] = (OperationCompletionListener) invocation.getArguments()[0];
            return future;
        });
        when(future.getStatus()).thenReturn(new OperationStatus(true, "Created."));
        impl.create("ABC123", new Tester("testerName", "testerOtherProperty"));
        verify(client).add("TESTER:ABC123", fixture("fixtures/tester.json"));
        verify(future).addListener(any(OperationCompletionListener.class));
        listener[0].onComplete(future);
    }

    @Test
    public void testRead() throws Exception {
        when(client.get(anyString())).thenReturn(fixture("fixtures/tester.json"));
        Tester tester = impl.read("ABC123");
        assertThat(tester.getName(), is("testerName"));
        assertThat(tester.getSomeOtherProperty(), is("testerOtherProperty"));
        verify(client).get("TESTER:ABC123");
    }

    @Test
    public void testUpdate() throws Exception {
        when(client.replace(anyString(), anyString())).thenReturn(future);
        final OperationCompletionListener[] listener = new OperationCompletionListener[1];
        when(future.addListener(any())).thenAnswer(invocation -> {
            listener[0] = (OperationCompletionListener) invocation.getArguments()[0];
            return future;
        });
        when(future.getStatus()).thenReturn(new OperationStatus(true, "Updated."));
        impl.update("ABC123", new Tester("testerName", "testerOtherProperty"));
        verify(client).replace("TESTER:ABC123", fixture("fixtures/tester.json"));
        verify(future).addListener(any(OperationCompletionListener.class));
        listener[0].onComplete(future);
    }

    @Test
    public void testDelete() throws Exception {
        when(client.delete(anyString())).thenReturn(future);
        final OperationCompletionListener[] listener = new OperationCompletionListener[1];
        when(future.addListener(any())).thenAnswer(invocation -> {
            listener[0] = (OperationCompletionListener) invocation.getArguments()[0];
            return future;
        });
        when(future.getStatus()).thenReturn(new OperationStatus(true, "Deleted."));
        impl.delete("ABC123");
        verify(client).delete("TESTER:ABC123");
        verify(future).addListener(any(OperationCompletionListener.class));
        listener[0].onComplete(future);
    }

    @Test
    public void testSet() throws Exception {
        when(client.set(anyString(), anyString())).thenReturn(future);
        final OperationCompletionListener[] listener = new OperationCompletionListener[1];
        when(future.addListener(any())).thenAnswer(invocation -> {
            listener[0] = (OperationCompletionListener) invocation.getArguments()[0];
            return future;
        });
        when(future.getStatus()).thenReturn(new OperationStatus(true, "Set."));
        impl.set("ABC123", new Tester("testerName", "testerOtherProperty"));
        verify(client).set("TESTER:ABC123", fixture("fixtures/tester.json"));
        verify(future).addListener(any(OperationCompletionListener.class));
        listener[0].onComplete(future);
    }

    @Test
    public void testReadException() throws Exception {
        when(client.get(anyString())).thenReturn("{ ke? }");
        exception.expect(IllegalStateException.class);
        impl.read("ABC123");
    }

    @Test
    public void testReadNull() throws Exception {
        when(client.get(anyString())).thenReturn(null);
        assertThat(impl.read("ABC123"), is(nullValue()));
    }

    @Test
    public void testCreateBroken() throws Exception {
        when(client.add(anyString(), anyString())).thenReturn(future);
        when(future.addListener(any())).thenReturn(future);
        GenericAccessorImpl<BrokenTester> borkedImpl = new GenericAccessorImpl<>(BrokenTester.class, factory);
        exception.expect(IllegalStateException.class);
        borkedImpl.create("ABC123", new BrokenTester("testerName", "testerOtherProperty"));
    }

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

}
