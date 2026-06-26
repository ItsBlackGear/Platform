package com.blackgear.platform.core.mixin.fabric.server;

import com.blackgear.platform.core.events.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {
    @Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/GameProfileCache;setUsesAuthentication(Z)V", shift = At.Shift.AFTER))
    private void platform$preStart(CallbackInfoReturnable<Boolean> cir) {
        ServerLifecycleEvents.PRE_STARTING.invoker().onLifecycle((MinecraftServer)(Object)this);
    }
}