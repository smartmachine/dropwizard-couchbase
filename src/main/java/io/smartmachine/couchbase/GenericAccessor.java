package io.smartmachine.couchbase;

import java.util.concurrent.Future;

public interface GenericAccessor<T>  {

    void create(String id, T newinstance);
    Future<T> read(String id);
    void update(String id, T object);
    void delete(String id);
    void set(String id, T object);

}
