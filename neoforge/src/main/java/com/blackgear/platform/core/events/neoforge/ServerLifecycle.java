package com.blackgear.platform.core.events.neoforge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.events.ServerLifecycleEvents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@EventBusSubscriber(modid = Platform.MOD_ID)
public class ServerLifecycle {
    @SubscribeEvent
    public static void onServerStartup(ServerAboutToStartEvent event) {
        ServerLifecycleEvents.STARTING.invoker().onLifecycle(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ServerLifecycleEvents.STARTED.invoker().onLifecycle(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ServerLifecycleEvents.STOPPING.invoker().onLifecycle(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        ServerLifecycleEvents.STOPPED.invoker().onLifecycle(event.getServer());
    }
}