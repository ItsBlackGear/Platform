package com.blackgear.platform.core.events;

import com.blackgear.platform.core.util.event.Event;
import net.minecraft.core.RegistryAccess;

public interface DataLifecycleEvents {
    Event<DataLifecycleEvents> DATA_RELOAD = Event.create(DataLifecycleEvents.class);

    void onReload(RegistryAccess access, boolean clientSide);
}