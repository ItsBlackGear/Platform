package com.blackgear.platform.core.events;

import com.blackgear.platform.core.util.config.ModConfig;
import com.blackgear.platform.core.util.event.Event;
import com.blackgear.platform.core.util.event.EventFactory;

public class ConfigEvents {
    public static final Event<Loading> LOADING = EventFactory.create(Loading.class);
    public static final Event<Reloading> RELOADING = EventFactory.create(Reloading.class);
    
    @FunctionalInterface
    public interface Loading {
        void onModConfigLoading(ModConfig config);
    }
    
    @FunctionalInterface
    public interface Reloading {
        void onModConfigReloading(ModConfig config);
    }
}