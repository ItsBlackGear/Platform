package com.blackgear.platform.core.mixin.config;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.util.config.ConfigTracker;
import com.blackgear.platform.core.util.config.ModConfig;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class ServerMainMixin {
    @Inject(
        method = "main",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/Util;startTimerHackThread()V"
        )
    )
    private static void platform$loadConfigs(CallbackInfo ci) {
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, Environment.getConfigDir());
    }
}