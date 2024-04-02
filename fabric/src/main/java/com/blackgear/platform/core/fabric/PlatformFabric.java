package com.blackgear.platform.core.fabric;

import com.blackgear.platform.core.Platform;
import net.fabricmc.api.ModInitializer;

public class PlatformFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Platform.bootstrap();
    }
}