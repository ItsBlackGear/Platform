package com.blackgear.platform.core.mixin.fabric;

import com.blackgear.platform.common.events.EntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {
    LivingEntity.class,
    Player.class,
    ServerPlayer.class
})
public class LivingDeathMixin {
    @Inject(
        method = "die",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$onDeath(DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (EntityEvents.ON_REMOVE.invoker().onRemove(self, source).isCancelled()) {
            ci.cancel();
        }

        if (!EntityEvents.ON_DEATH.invoker().onDeath(self, source)) {
            ci.cancel();
        }
    }
}