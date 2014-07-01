package io.smartmachine.couchbase.spi;


import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.View;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smartmachine.couchbase.CouchbaseClientFactory;
import io.smartmachine.couchbase.GenericAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
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
        View view = factory.client().getView(type.getSimpleName().toUpperCase(), method.getName());
        Query query = new Query();
        query.setIncludeDocs(true);

        throw new IllegalStateException("Finder methods not implemented yet.");
    }

    private String makeKey(String id) {
        return type.getSimpleName().toUpperCase() + ":" + id;
    }


}
