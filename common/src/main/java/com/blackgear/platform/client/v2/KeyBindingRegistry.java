package com.blackgear.platform.client.v2;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.KeyMapping;

public class KeyBindingRegistry {
    @ExpectPlatform
    public static void register(KeyMapping mapping) {
        throw new AssertionError();
    }
}
