package com.blackgear.platform.core.util.event;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Function;

@SuppressWarnings({"unchecked", "SuspiciousInvocationHandlerImplementation"})
public class EventFactory {
    public static <T> Event<T> create(Class<? super T> clazz, Function<T[], T> factory) {
        return new SimpleEvent<>(clazz, factory);
    }
    
    public static <T> Event<T> create(Class<? super T> type) {
        return create(type, callbacks -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(), new Class[] { type }, (proxy, method, args) -> {
            for (Object callback : callbacks) {
                invoke(callback, method, args);
            }
            return null;
        }));
    }
    
    private static <T> void invoke(T object, Method method, Object[] args) throws Throwable {
        MethodHandles.lookup().unreflect(method).bindTo(object).invokeWithArguments(args);
    }
}