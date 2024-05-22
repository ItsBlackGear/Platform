package com.blackgear.platform.core.events;

import com.blackgear.platform.core.util.config.ModConfig;
import com.blackgear.platform.core.util.event.Event;

public class ConfigEvents {
    public static final Event<Loading> LOADING = Event.create(Loading.class);
    public static final Event<Reloading> RELOADING = Event.create(Reloading.class);
    
    @FunctionalInterface
    public interface Loading {
        void onModConfigLoading(ModConfig config);
    }
    
    @FunctionalInterface
    public interface Reloading {
        void onModConfigReloading(ModConfig config);
    }
}