package com.blackgear.platform.core.util;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;

public enum EventBus {
    LOADER, MOD;
    
    public static IEventBus get(String modId) {
        return ModList.get().getModContainerById(modId).orElseThrow(() -> new IllegalArgumentException("Invalid mod: " + modId)).getEventBus();
    }
    
    public static IEventBus get(EventBus bus) {
        return switch (bus) {
            case LOADER -> NeoForge.EVENT_BUS;
            case MOD -> ModLoadingContext.get().getActiveContainer().getEventBus();
        };
    }
}