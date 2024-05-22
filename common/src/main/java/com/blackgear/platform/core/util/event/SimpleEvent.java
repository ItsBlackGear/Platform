package com.blackgear.platform.core.util.event;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public final class SimpleEvent<T> extends Event<T> {
    private final Function<T[], T> factory;
    private final Lock lock = new ReentrantLock();
    private T[] handlers;
    
    @SuppressWarnings("unchecked")
    public SimpleEvent(Class<? super T> type, Function<T[], T> factory) {
        this.factory = factory;
        this.handlers = (T[]) Array.newInstance(type, 0);
        this.invoker = this.factory.apply(this.handlers);
    }
    
    @Override
    public void register(T listener) {
        Objects.requireNonNull(listener, "Tried to register a null listener");
        
        this.lock.lock();
        try {
            this.handlers = Arrays.copyOf(this.handlers, this.handlers.length + 1); // Expands the array by 1 and inserts the listener into it
            this.handlers[this.handlers.length - 1] = listener;
            this.invoker = this.factory.apply(this.handlers);
        } finally {
            this.lock.unlock();
        }
    }
}