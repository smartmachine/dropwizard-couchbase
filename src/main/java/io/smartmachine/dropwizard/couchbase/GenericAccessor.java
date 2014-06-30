package io.smartmachine.dropwizard.couchbase;

public interface GenericAccessor<T>  {

    void create(T newinstance);
    T read(String id);
    T update(T object);
    void delete(T object);

}
