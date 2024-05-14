package com.blackgear.platform.core.events;

import com.blackgear.platform.core.util.config.ModConfig;
import com.blackgear.platform.core.util.event.Event;
import com.blackgear.platform.core.util.event.EventFactory;

public class ConfigEvent {
    public static final Event<Loading> LOADING = EventFactory.create(Loading.class, listeners -> config -> {
        for (Loading event : listeners) {
            event.onModConfigLoading(config);
        }
    });
    
    public static final Event<Reloading> RELOADING = EventFactory.create(Reloading.class, listeners -> config -> {
        for (Reloading event : listeners) {
            event.onModConfigReloading(config);
        }
    });
    
    @FunctionalInterface
    public interface Loading {
        void onModConfigLoading(ModConfig config);
    }
    
    @FunctionalInterface
    public interface Reloading {
        void onModConfigReloading(ModConfig config);
    }
}