package io.smartmachine.dropwizard.couchbase;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericAccessorImpl<T> implements GenericAccessor<T> {

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
        return null;
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
}
