package io.smartmachine.couchbase.spi;

import io.smartmachine.couchbase.Accessor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

@Singleton
public class AccessorResolver {

    public static class AccessorInjectionResolver implements InjectionResolver<Accessor> {


        @Override
        public Object resolve(Injectee injectee, ServiceHandle<?> handle) {
            System.out.println(injectee);
            System.out.println(handle);
            return null;
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

        @Override
        protected void configure() {
            bind(new AccessorInjectionResolver())
                    .to(new TypeLiteral<InjectionResolver<Accessor>>(){});

        }
    }

}
