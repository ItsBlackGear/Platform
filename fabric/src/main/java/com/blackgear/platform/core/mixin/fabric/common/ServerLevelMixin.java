package com.blackgear.platform.core.mixin.fabric.common;

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
        if (!EntityEvents.ON_SPAWN.invoker().spawn(player, (ServerLevel) (Object) this)) {
            ci.cancel();
        }
    }
    
    @Inject(
        method = "addEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getChunk(IILnet/minecraft/world/level/chunk/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/ChunkAccess;",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    private void platform$addEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!EntityEvents.ON_SPAWN.invoker().spawn(entity, (ServerLevel) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
    
    @Inject(
        method = "loadFromChunk",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;add(Lnet/minecraft/world/entity/Entity;)V",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    private void platform$loadFromChunk(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!EntityEvents.ON_SPAWN.invoker().spawn(entity, (ServerLevel) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}