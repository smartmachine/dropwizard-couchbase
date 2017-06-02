package io.smartmachine.couchbase;

public interface GenericAccessor<T>  {

    void create(String id, T newinstance);
    T read(String id);
    void update(String id, T object);
    void delete(String id);
    void set(String id, T object);

}
