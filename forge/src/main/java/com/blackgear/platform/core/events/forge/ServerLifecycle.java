package com.blackgear.platform.core.events.forge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.events.ServerLifecycleEvents;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Platform.MOD_ID)
public class ServerLifecycle {
    @SubscribeEvent
    public static void onServerStartup(ServerStartingEvent event) {
        ServerLifecycleEvents.STARTING.invoker().starting(event.getServer());
    }
    
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ServerLifecycleEvents.STARTED.invoker().started(event.getServer());
    }
    
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ServerLifecycleEvents.STOPPING.invoker().stopping(event.getServer());
    }
    
    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        ServerLifecycleEvents.STOPPED.invoker().stopped(event.getServer());
    }
}