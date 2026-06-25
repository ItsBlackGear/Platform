package com.blackgear.platform.client.v2.fabric;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class KeyBindingRegistryImpl {
    public static void register(KeyMapping mapping) {
        KeyBindingHelper.registerKeyBinding(mapping);
    }
}
