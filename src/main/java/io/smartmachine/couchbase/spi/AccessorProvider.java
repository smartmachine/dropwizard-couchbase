package io.smartmachine.couchbase.spi;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import io.smartmachine.couchbase.Accessor;
import io.smartmachine.couchbase.CouchbaseClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

@Provider
public class AccessorProvider implements InjectableProvider<Accessor, Type> {

    private static Logger log = LoggerFactory.getLogger(AccessorProvider.class);

    final private CouchbaseClientFactory factory;
    private AccessorFactory af = AccessorFactory.getFactory();

    public AccessorProvider(CouchbaseClientFactory factory) {
        this.factory = factory;
    }

    AccessorProvider(AccessorFactory af, CouchbaseClientFactory factory) {
        this(factory);
        this.af = af;
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
            Class<?> accessorClass = ((Field) ic.getAccesibleObject()).getType();
            if (accessorClass.equals(CouchbaseClientFactory.class)) {
                return factory;
            }
            return af.getAccessor(accessorClass, factory);
        };
    }

}
