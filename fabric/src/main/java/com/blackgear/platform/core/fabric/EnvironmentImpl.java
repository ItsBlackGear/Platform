package com.blackgear.platform.core.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class EnvironmentImpl {
    public static boolean isClientSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    public static boolean hasModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}