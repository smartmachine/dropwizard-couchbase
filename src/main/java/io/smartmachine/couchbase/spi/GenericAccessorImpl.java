package io.smartmachine.couchbase.spi;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import io.smartmachine.couchbase.CouchbaseClientFactory;
import io.smartmachine.couchbase.GenericAccessor;
import io.smartmachine.couchbase.ViewQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericAccessorImpl<T> implements GenericAccessor<T>, FinderExecutor<T> {

    private static Logger log = LoggerFactory.getLogger(GenericAccessorImpl.class);

    final private Class<T> type;
    final private CouchbaseClient client;
    private Map<String, View>  views = new HashMap<>();
    private ObjectMapper mapper = Jackson.newObjectMapper();

    public GenericAccessorImpl(Class<T> type, CouchbaseClientFactory factory) {
        this.type = type;
        this.client = factory.client();
    }

    private T deserialize(Object json) {
        if (json == null) {
            return null;
        }
        try {
            return mapper.readValue((String) json, type);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot convert JSON: " + json + " to " + type.getSimpleName());
        }
    }

    private String serialize(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot convert " + type.getSimpleName() + " to JSON.", e);
        }
    }

    @Override
    public void create(String id, T newinstance) {
        log.info("Create: " + type.getSimpleName());
        client.add(makeKey(id), serialize(newinstance)).addListener(future ->
                        log.info("Create status: " + future.getStatus())
        );
    }

    @Override
    public T read(String id) {
        log.info("Reading : " + type.getSimpleName());
        return deserialize(client.get(makeKey(id)));
    }

    @Override
    public void update(String id, T object) {
        log.info("Updating: " + type.getSimpleName());
        client.replace(makeKey(id), serialize(object)).addListener(future ->
                        log.info("Update status: " + future.getStatus())
        );
    }

    @Override
    public void delete(String id) {
        log.info("Delete: " + type.getSimpleName());
        client.delete(makeKey(id)).addListener(future ->
                        log.info("Delete status: " + future.getStatus())
        );
    }

    @Override
    public void set(String id, T object) {
        log.info("Set: " + type.getSimpleName());
        client.set(makeKey(id), serialize(object)).addListener(future ->
                        log.info("Set status: " + future.getStatus())
        );
    }

    @Override
    public List<T> executeFinder(Method method, Object[] queryArgs) {
        List<T> list = new ArrayList<>();
        View view = views.get(method.getName());
        if (view == null) {
            throw new IllegalStateException("You must annotate your Accessor interface method with ViewQuery!");
        }
        Query query = new Query();
        query.setIncludeDocs(true);
        query.setStale(Stale.FALSE);
        ViewResponse response = client.query(view, query);
        ObjectMapper mapper = new ObjectMapper();
        for (ViewRow row : response) {
            String json = (String) row.getDocument();
            try {
                list.add(mapper.readValue(json, type));
            } catch (IOException e) {
                throw new IllegalStateException("Cannot convert JSON to " + type.getSimpleName());
            }
        }
        return list;
    }

    public void cacheViews(Class accessorClass) {
        views.clear();
        log.info("Scanning " + accessorClass.getName() +" for ViewQuery annotated methods ...");
        for (Method method : accessorClass.getMethods()) {
            ViewQuery vq = method.getDeclaredAnnotation(ViewQuery.class);
            if (vq == null) {
                continue;
            }
            View view = getOrCreateView(method, vq);
            log.info("Caching view: " + view.getViewName());
            views.put(method.getName(),  getOrCreateView(method, vq));
        }
    }

    private View getOrCreateView(Method method, ViewQuery vq) {
        // Construct the document and view names.
        // TODO Pluggable naming strategies??
        String docName = type.getSimpleName().toUpperCase();
        String viewName = method.getName();

        try {
            return client.getView(docName, viewName);
        } catch (InvalidViewException e) {
            log.info("View " + viewName + " does not exist, creating it.");
        }
        DesignDocument doc;
        try {
            doc = client.getDesignDoc(docName);
        } catch (InvalidViewException e) {
            log.info("Design document " + docName + " does not exist, creating it.");
            doc = new DesignDocument(docName);
        }
        StringBuilder mapBuilder = new StringBuilder();
        mapBuilder.append("function (doc, meta) {\n")
                .append("  if (")
                .append(vq.value())
                .append(") {\n")
                .append("    ").append(vq.emit()).append(";\n")
                .append("  }\n")
                .append("}");
        ViewDesign viewDesign = new ViewDesign(viewName, mapBuilder.toString());
        doc.setView(viewDesign);
        client.createDesignDoc(doc);
        return client.getView(docName, viewName);
    }

    private String makeKey(String id) {
        return type.getSimpleName().toUpperCase() + ":" + id;
    }


}
