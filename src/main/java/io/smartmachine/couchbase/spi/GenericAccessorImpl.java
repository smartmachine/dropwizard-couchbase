package io.smartmachine.couchbase.spi;


import com.couchbase.client.protocol.views.*;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    final private CouchbaseClientFactory factory;
    private Map<String, View> views;

    public GenericAccessorImpl(Class<T> type, CouchbaseClientFactory factory) {
        this.type = type;
        this.factory = factory;
    }

    @Override
    public void create(T newinstance) {
        log.info("Create: "+ type.getSimpleName());
    }

    @Override
    public T read(String id) {
        log.info("Reading : " + type.getSimpleName());
        String json = (String) factory.client().get(makeKey(id));
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot convert JSON to " + type.getSimpleName());
        }
    }

    @Override
    public T update(T object) {
        log.info("Updating: " + type.getSimpleName());
        return null;
    }

    @Override
    public void delete(T object) {
        log.info("Delete: " + type.getSimpleName());
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
        ViewResponse response = factory.client().query(view, query);
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
        views = new HashMap<>();
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
            return factory.client().getView(docName, viewName);
        } catch (InvalidViewException e) {
            log.info("View " + viewName + " does not exist, creating it.");
        }
        DesignDocument doc = null;
        try {
            doc = factory.client().getDesignDoc(docName);
        } catch (Exception e) {
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
        factory.client().createDesignDoc(doc);
        return factory.client().getView(docName, viewName);
    }

    private String makeKey(String id) {
        return type.getSimpleName().toUpperCase() + ":" + id;
    }


}
