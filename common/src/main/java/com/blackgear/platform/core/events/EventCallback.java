package com.blackgear.platform.core.events;

import java.util.function.Function;

public class EventCallback {
    private boolean cancelled = false;
    
    public static boolean invoke(Function<EventCallback, Runnable> factory) {
        EventCallback callback = new EventCallback();
        factory.apply(callback);
        return callback.isCancelled();
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void cancel() {
        this.cancelled = true;
    }
}