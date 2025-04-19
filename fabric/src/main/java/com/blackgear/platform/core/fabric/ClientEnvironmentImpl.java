package com.blackgear.platform.core.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.util.thread.BlockableEventLoop;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ClientEnvironmentImpl {
    public static final Supplier<BlockableEventLoop<?>> CLIENT_EXECUTOR = () -> {
        try {
            return Minecraft.getInstance();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to access Minecraft client instance", exception);
        }
    };
}
