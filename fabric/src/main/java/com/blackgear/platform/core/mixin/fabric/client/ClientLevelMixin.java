package com.blackgear.platform.core.mixin.fabric.client;

import com.blackgear.platform.common.events.EntityEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(
        method = "addEntity",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$addEntity(Entity entity, CallbackInfo ci) {
        if (EntityEvents.ON_SPAWN.invoker().onSpawn(entity, (ClientLevel) (Object) this).isCancelled()) {
            ci.cancel();
        }
    }
}