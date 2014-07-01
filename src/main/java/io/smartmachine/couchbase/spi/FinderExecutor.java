package io.smartmachine.couchbase.spi;

import java.lang.reflect.Method;
import java.util.List;

public interface FinderExecutor<T> {

    public List<T> executeFinder(Method method, final Object[] queryArgs);

}
