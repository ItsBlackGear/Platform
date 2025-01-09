package com.blackgear.platform.core.mixin.fabric.common;

import com.blackgear.platform.common.events.EntityEvents;
import com.blackgear.platform.core.events.EventCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Shadow protected abstract boolean isUUIDUsed(Entity entity);
    
    @Inject(
        method = "addPlayer",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$addPlayer(ServerPlayer player, CallbackInfo ci) {
        if (EventCallback.invoke(callback -> () -> EntityEvents.ON_SPAWN.invoker().spawn(player, (ServerLevel) (Object) this, callback))) {
            ci.cancel();
        }
    }
    
    @Inject(
        method = "addEntity",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$addEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!entity.removed && !this.isUUIDUsed(entity)) {
            if (EventCallback.invoke(callback -> () -> EntityEvents.ON_SPAWN.invoker().spawn(entity, (ServerLevel) (Object) this, callback))) {
                cir.setReturnValue(false);
            }
        }
    }
    
    @Inject(
        method = "loadFromChunk",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$loadFromChunk(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (EventCallback.invoke(callback -> () -> EntityEvents.ON_SPAWN.invoker().spawn(entity, (ServerLevel) (Object) this, callback))) {
            cir.setReturnValue(false);
        }
    }
}