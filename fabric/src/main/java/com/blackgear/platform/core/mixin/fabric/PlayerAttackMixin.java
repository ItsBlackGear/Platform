package com.blackgear.platform.core.mixin.fabric;

import com.blackgear.platform.common.events.EntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerAttackMixin {
    @Inject(
        method = "hurt",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$onAttack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!EntityEvents.ON_ATTACK.invoker().onAttack((Player) (Object) this, source, amount)) {
            cir.setReturnValue(false);
        }
    }
}