package io.smartmachine.couchbase.spi;

import io.smartmachine.couchbase.CouchbaseClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public class AccessorFactory {

    private static Logger log = LoggerFactory.getLogger(AccessorFactory.class);

    @SuppressWarnings("unchecked")
    public static <T> T getAccessor(Class<T> t, CouchbaseClientFactory factory) {
        log.info("DAO class name: " + t.getName());
        Type type = t.getGenericInterfaces()[0];
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            log.info("Parameterized Type: " + pType.toString());
            Type[] args = pType.getActualTypeArguments();
            Class<?> accessorClass = (Class<?>) args[0];
            log.info("Accessor Class: " + accessorClass.toString());
            return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] {t},
                    new AccessorInvoker(getAccessorImpl(accessorClass, factory)));
        }
        throw new IllegalArgumentException("Your accessor interface has to extend GenericAccessor<ModelClass>");
    }

    private static <E> GenericAccessorImpl<?> getAccessorImpl(Class<E> accessorClass, CouchbaseClientFactory factory) {
        return new GenericAccessorImpl<>(accessorClass, factory);
    }

}
