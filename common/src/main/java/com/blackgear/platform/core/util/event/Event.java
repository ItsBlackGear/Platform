package com.blackgear.platform.core.util.event;

public abstract class Event<T> {
    protected volatile T invoker;
    
    public T invoker() {
        return this.invoker;
    }
    
    public abstract void register(T listener);
}