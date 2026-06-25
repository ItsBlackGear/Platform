package com.blackgear.platform.core.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

public enum EventBus {
    LOADER, MOD;

    public static IEventBus get(String modId) {
        return ((FMLModContainer) ModList.get().getModContainerById(modId).orElseThrow(() -> new IllegalArgumentException("Invalid mod: " + modId))).getEventBus();
    }
    
    public static IEventBus get(EventBus bus) {
        return switch (bus) {
            case LOADER -> MinecraftForge.EVENT_BUS;
            case MOD -> FMLJavaModLoadingContext.get().getModEventBus();
        };
    }
}