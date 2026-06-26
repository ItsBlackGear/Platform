package com.blackgear.platform.core.util.config.fabric;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.events.ServerLifecycleEvents;
import com.blackgear.platform.core.network.listener.ServerListenerEvents;
import com.blackgear.platform.core.util.config.ConfigLoader;
import com.blackgear.platform.core.util.config.ModConfig;

public class ConfigLoaderImpl {
    public static void bootstrap() {
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.COMMON, Environment.getConfigDir());
        if (Environment.isClientSide()) {
            ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.CLIENT, Environment.getConfigDir());
        }
        
        ServerLifecycleEvents.STARTING.register(server -> ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, ConfigLoader.getServerConfigDirectory(server)));
        ServerLifecycleEvents.STOPPING.register(server -> ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER, ConfigLoader.getServerConfigDirectory(server)));
        ServerListenerEvents.JOIN.register((connection, player) -> ConfigTracker.INSTANCE.syncConfigs(Environment.isClientSide()));
    }
}
