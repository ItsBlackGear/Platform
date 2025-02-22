package com.blackgear.platform.core.mixin.forge.core.networking;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.util.config.ConfigTracker;
import com.blackgear.platform.core.util.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(
        method = "<init>",
        at = @At(value = "TAIL")
    )
    public void platform$loadConfigs(GameConfig config, CallbackInfo ci) {
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.CLIENT, Environment.getConfigDir());
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.COMMON, Environment.getConfigDir());
    }
}