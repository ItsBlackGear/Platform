package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.client.event.FovRendering;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {
    public AbstractClientPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Inject(
        method = "getFieldOfViewModifier",
        at = @At(
            value = "RETURN",
            ordinal = 1
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void updateFov(CallbackInfoReturnable<Float> cir, float currentFov) {
        float fov = FovRendering.EVENT.invoker().setFov(this, currentFov);
        if (fov != currentFov) cir.setReturnValue(fov);
    }
}