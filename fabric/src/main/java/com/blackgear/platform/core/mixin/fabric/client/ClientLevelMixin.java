package com.blackgear.platform.core.mixin.fabric.client;

import com.blackgear.platform.common.events.EntityEvents;
import com.blackgear.platform.core.events.EventCallback;
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
    private void platform$addEntity(int entityId, Entity entityToSpawn, CallbackInfo ci) {
        if (EventCallback.invoke(callback -> () -> EntityEvents.ON_SPAWN.invoker().spawn(entityToSpawn, (ClientLevel) (Object) this, callback))) {
            ci.cancel();
        }
    }
}