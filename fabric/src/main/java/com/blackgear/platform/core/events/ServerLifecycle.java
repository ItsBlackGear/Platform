package com.blackgear.platform.core.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import static com.blackgear.platform.core.events.ServerLifecycleEvents.*;

public class ServerLifecycle {
    public static void bootstrap() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> STARTING.invoker().onLifecycle(server));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> STARTED.invoker().onLifecycle(server));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> STOPPING.invoker().onLifecycle(server));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> STOPPED.invoker().onLifecycle(server));
    }
}