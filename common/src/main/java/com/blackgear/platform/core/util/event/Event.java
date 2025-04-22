package com.blackgear.platform.core.util.event;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Function;

@SuppressWarnings({"unchecked", "SuspiciousInvocationHandlerImplementation"})
public abstract class Event<T> {
    protected volatile T invoker;
    
    public T invoker() {
        return this.invoker;
    }
    
    public abstract void register(T listener);
    
    public static <T> Event<T> create(Class<? super T> clazz, Function<T[], T> factory) {
        return new SimpleEvent<>(clazz, factory);
    }
    
    public static <T> Event<T> create(Class<? super T> type) {
        return create(type, callbacks -> (T) Proxy.newProxyInstance(Event.class.getClassLoader(), new Class[] { type }, (proxy, method, args) -> {
            for (Object callback : callbacks) {
                invokeFast(callback, method, args);
            }
            return null;
        }));
    }
    
    public static <T> Event<T> cancellable(Class<? super T> type) {
        return create(type, callbacks -> (T) Proxy.newProxyInstance(Event.class.getClassLoader(), new Class[] { type }, (proxy, method, args) -> {
            for (Object callback : callbacks) {
                boolean result = invokeFast(callback, method, args);
                if (!result) {
                    return false;
                }
            }
            
            return true;
        }));
    }
    
    private static <T, S> S invokeFast(T callback, Method method, Object[] args) throws Throwable {
        return (S) MethodHandles.lookup().unreflect(method).bindTo(callback).invokeWithArguments(args);
    }
}