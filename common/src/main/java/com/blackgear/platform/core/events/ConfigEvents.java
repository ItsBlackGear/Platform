package com.blackgear.platform.core.events;

import com.blackgear.platform.core.util.config.ModConfig;
import com.blackgear.platform.core.util.event.Event;

public interface ConfigEvents {
    Event<ConfigEvents> LOADING = Event.create(ConfigEvents.class);
    Event<ConfigEvents> RELOADING = Event.create(ConfigEvents.class);
    Event<ConfigEvents> UNLOADING = Event.create(ConfigEvents.class);

    void onModConfig(ModConfig config);
}