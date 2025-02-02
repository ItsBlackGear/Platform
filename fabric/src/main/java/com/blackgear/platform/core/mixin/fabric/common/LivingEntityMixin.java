package com.blackgear.platform.core.mixin.fabric.common;

import com.blackgear.platform.common.events.EntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(
        method = "hurt",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$onAttack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player) return;

        if (!EntityEvents.ON_ATTACK.invoker().attack((LivingEntity) (Object) this, source, amount)) {
            cir.setReturnValue(false);
        }
    }
}