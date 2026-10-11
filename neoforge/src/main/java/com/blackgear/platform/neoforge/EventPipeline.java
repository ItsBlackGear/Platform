package com.blackgear.platform.neoforge;

import net.neoforged.bus.api.ICancellableEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventPipeline<T> {
    private final List<Consumer<T>> consumers = new CopyOnWriteArrayList<>();
    
    public void add(Consumer<T> consumer) {
        this.consumers.add(consumer);
    }
    
    public void dispatch(T event) {
        for (Consumer<T> consumer : consumers) {
            consumer.accept(event);
        }
    }
    
    public void dispatchCancelable(T event) {
        if (event instanceof ICancellableEvent cancelable) {
            for (Consumer<T> consumer : consumers) {
                consumer.accept(event);
                if (cancelable.isCanceled()) break;
            }
        } else {
            this.dispatch(event);
        }
    }
}