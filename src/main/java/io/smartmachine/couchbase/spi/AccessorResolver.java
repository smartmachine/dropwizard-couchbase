package io.smartmachine.couchbase.spi;

import io.smartmachine.couchbase.Accessor;
import io.smartmachine.couchbase.CouchbaseClientFactory;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class AccessorResolver {

    private static Logger log = LoggerFactory.getLogger(AccessorFactory.class);

    private static CouchbaseClientFactory factory;

    public static class AccessorInjectionResolver implements InjectionResolver<Accessor> {


        @Override
        public Object resolve(Injectee injectee, ServiceHandle<?> handle) {
            Class requiredClass = (Class) injectee.getRequiredType();
            log.info("Asked to inject a " + requiredClass.getSimpleName());
            if (requiredClass == CouchbaseClientFactory.class) {
                return factory;
            }
            return AccessorFactory.getFactory().getAccessor(requiredClass, factory);
        }

        @Override
        public boolean isConstructorParameterIndicator() {
            return false;
        }

        @Override
        public boolean isMethodParameterIndicator() {
            return false;
        }
    }

    public static class Binder extends AbstractBinder {

        public Binder(CouchbaseClientFactory factory) {
            AccessorResolver.factory = factory;
        }

        @Override
        protected void configure() {
            bind(AccessorInjectionResolver.class)
                    .to(new TypeLiteral<InjectionResolver<Accessor>>(){})
                    .in(Singleton.class);

        }
    }

}
