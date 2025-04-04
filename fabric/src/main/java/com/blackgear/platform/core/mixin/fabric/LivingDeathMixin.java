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

@Mixin(
    value = {
        LivingEntity.class,
        Player.class,
        ServerPlayer.class
    }
)
public class LivingDeathMixin {
    @Inject(
        method = "die",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$onDeath(DamageSource source, CallbackInfo ci) {
        if (!EntityEvents.ON_DEATH.invoker().onDeath((LivingEntity) (Object) this, source)) {
            ci.cancel();
        }
    }
}