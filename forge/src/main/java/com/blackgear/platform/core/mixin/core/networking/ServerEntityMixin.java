package com.blackgear.platform.core.mixin.core.networking;

import com.blackgear.platform.core.util.network.EntityTrackingEvents;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
    @Shadow @Final private Entity entity;
    
    @Inject(
        method = "addPairing",
        at = @At("HEAD")
    )
    private void platform$addPairing(ServerPlayer player, CallbackInfo ci) {
        EntityTrackingEvents.START_TRACKING.invoker().onStartTracking(this.entity, player);
    }
    
    @Inject(
        method = "removePairing",
        at = @At("TAIL")
    )
    private void platform$removePairing(ServerPlayer player, CallbackInfo ci) {
        EntityTrackingEvents.STOP_TRACKING.invoker().onStopTracking(this.entity, player);
    }
}