package com.blackgear.platform.core.mixin.forge.client;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.util.config.ConfigTracker;
import com.blackgear.platform.core.util.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @WrapOperation(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"
        )
    )
    public Thread platform$loadConfigs(Operation<Thread> original) {
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.CLIENT, Environment.getConfigDir());
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.COMMON, Environment.getConfigDir());
        return original.call();
    }
}