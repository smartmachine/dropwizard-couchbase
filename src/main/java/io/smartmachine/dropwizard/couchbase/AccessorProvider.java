package io.smartmachine.dropwizard.couchbase;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;

@Provider
public class AccessorProvider implements InjectableProvider<Accessor, Type> {

    private static Logger log = LoggerFactory.getLogger(AccessorProvider.class);

    final private CouchbaseClientFactory factory;

    public AccessorProvider(CouchbaseClientFactory factory) {
        this.factory = factory;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.Singleton;
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, Accessor accessor, Type type) {
        return () -> {
            log.info("getInjectable() called:");
            for (Annotation ann : ic.getAnnotations()) {
                log.info("Found annotation: " + ann.annotationType().getSimpleName());
            }
            log.info("AccessibleObject: " + ic.getAccesibleObject().toString());
            log.info("Accessor: " + accessor.toString());
            log.info("Type: " + type.getTypeName());
            log.info("Type: " + type.toString());
            Class<?> accessorClass = ((Field) ic.getAccesibleObject()).getType();
            Type t = accessorClass.getGenericInterfaces()[0];
            if ( t instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) t;
                log.info("Parametrized Type: " + pType);
                Type[] args = pType.getActualTypeArguments();
                Class<?> modelClass = (Class<?>) args[0];
                log.info("ModelClass: " + modelClass);
                return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{accessorClass},
                        (proxy, method, args1) -> null);
            }

        };
    }

    private static <E> GenericAccessorImpl<?> getAccessorImpl(Class<E> modelClass, CouchbaseClientFactory factory) {
        return new GenericAccessorImpl<>(modelClass, factory);
    }
}
