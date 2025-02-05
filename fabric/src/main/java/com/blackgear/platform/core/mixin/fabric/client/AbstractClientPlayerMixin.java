package com.blackgear.platform.core.mixin.fabric.client;

import com.blackgear.platform.client.event.FovRendering;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {
    public AbstractClientPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
        super(level, pos, yRot, gameProfile, profilePublicKey);
    }

    @Inject(
        method = "getFieldOfViewModifier",
        at = @At(
            value = "RETURN",
            ordinal = 0
        ),
        cancellable = true
    )
    public void updateFov(CallbackInfoReturnable<Float> cir, @Local float currentFov) {
        float fov = FovRendering.EVENT.invoker().setFov(this, currentFov);
        if (fov != currentFov) cir.setReturnValue(fov);
    }
}