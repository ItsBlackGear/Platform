package com.blackgear.platform.client.v2.forge;

import com.blackgear.platform.core.util.EventBus;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

public class KeyBindingRegistryImpl {
    public static void register(KeyMapping mapping) {
        EventBus.get(EventBus.MOD).<RegisterKeyMappingsEvent>addListener(event -> event.register(mapping));
    }
}