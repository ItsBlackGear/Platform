package com.blackgear.platform.core.events;

import com.blackgear.platform.Platform;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.*;

@Mod.EventBusSubscriber(modid = Platform.MOD_ID)
public class ServerLifecycle {
    @SubscribeEvent
    public static void onServerStartup(FMLServerStartingEvent event) {
        ServerLifecycleEvents.STARTING.invoker().starting(event.getServer());
    }
    
    @SubscribeEvent
    public static void onServerStarted(FMLServerStartedEvent event) {
        ServerLifecycleEvents.STARTED.invoker().started(event.getServer());
    }
    
    @SubscribeEvent
    public static void onServerStopping(FMLServerStoppingEvent event) {
        ServerLifecycleEvents.STOPPING.invoker().stopping(event.getServer());
    }
    
    @SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent event) {
        ServerLifecycleEvents.STOPPED.invoker().stopped(event.getServer());
    }
}