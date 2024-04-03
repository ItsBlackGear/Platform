package com.blackgear.platform.core.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class EnvironmentImpl {
    public static boolean isClientSide() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }

    public static boolean hasModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}