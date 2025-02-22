package com.blackgear.platform.core.events;

import com.blackgear.platform.core.util.event.Event;
import net.minecraft.server.MinecraftServer;

public interface ServerLifecycleEvents {
    Event<ServerLifecycleEvents> STARTING = Event.create(ServerLifecycleEvents.class);
    Event<ServerLifecycleEvents> STARTED = Event.create(ServerLifecycleEvents.class);
    Event<ServerLifecycleEvents> STOPPING = Event.create(ServerLifecycleEvents.class);
    Event<ServerLifecycleEvents> STOPPED = Event.create(ServerLifecycleEvents.class);

    void onLifecycle(MinecraftServer server);
}