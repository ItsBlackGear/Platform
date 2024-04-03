package com.blackgear.platform.core;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class Environment {
    @ExpectPlatform
    public static boolean isClientSide() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean hasModLoaded(String modId) {
        throw new AssertionError();
    }
}