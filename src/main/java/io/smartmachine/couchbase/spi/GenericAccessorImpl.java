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
import java.util.List;

public class GenericAccessorImpl<T> implements GenericAccessor<T>, FinderExecutor<T> {

    private static Logger log = LoggerFactory.getLogger(GenericAccessorImpl.class);

    final private Class<T> type;
    final private CouchbaseClientFactory factory;

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
        View view = getOrCreateView(method);
        Query query = new Query();
        query.setIncludeDocs(true);
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

    private View getOrCreateView(Method method) {
        // Construct the document and view names.
        // TODO Pluggable naming strategies??
        String docName = type.getSimpleName().toUpperCase();
        String viewName = method.getName();

        try {
            return factory.client().getView(docName, viewName);
        } catch (InvalidViewException e) {
            log.info("View does not exist.");
        }
        DesignDocument doc = null;
        try {
            doc = factory.client().getDesignDoc(docName);
        } catch (Exception e) {
            doc = new DesignDocument(docName);
        }
        ViewQuery vq = method.getDeclaredAnnotation(ViewQuery.class);
        if (vq == null) {
            throw new IllegalStateException("Your finder method must be annotated with ViewQuery");
        }
        StringBuilder mapBuilder = new StringBuilder();
        mapBuilder.append("function (doc, meta) {\n")
                .append("  if (")
                .append(vq.value())
                .append(") {\n")
                .append("    emit(meta.id, null);\n")
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
