package com.blackgear.platform.core.mixin.fabric;

import com.blackgear.platform.common.events.EntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Inject(
        method = "addPlayer",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$addPlayer(ServerPlayer player, CallbackInfo ci) {
        if (!EntityEvents.ON_SPAWN.invoker().onSpawn(player, (ServerLevel) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(
        method = "addEntity",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$addEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!EntityEvents.ON_SPAWN.invoker().onSpawn(entity, (ServerLevel) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}