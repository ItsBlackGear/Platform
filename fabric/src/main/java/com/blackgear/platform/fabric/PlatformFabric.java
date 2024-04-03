package com.blackgear.platform.fabric;

import com.blackgear.platform.Platform;
import net.fabricmc.api.ModInitializer;

public class PlatformFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Platform.bootstrap();
    }
}