package io.smartmachine.dropwizard.couchbase.spi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class AccessorInvoker implements InvocationHandler {

    private FinderExecutor executor;

    public AccessorInvoker(FinderExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().startsWith("find")) {
            return executor.executeFinder(method, args);
        }
        Method[] methods = executor.getClass().getMethods();
        for (Method targetMethod : methods) {
            if(targetMethod.getName().equals(method.getName())) {
                return method.invoke(executor, args);
            }
        }
        Class<?>[] interfaces = proxy.getClass().getInterfaces();
        if (interfaces.length > 0) {
            throw new IllegalArgumentException("Unable to match method " + method.getName() + " to interface "
                    + interfaces[0].getName() + ".");
        } else {
            throw new IllegalArgumentException("Unable to match method " + method.getName() + " to your DAO interface");
        }
    }

}
