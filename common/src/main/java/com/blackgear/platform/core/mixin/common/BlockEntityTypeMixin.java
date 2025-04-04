package com.blackgear.platform.core.mixin.common;

import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {
    @Inject(
        method = "isValid",
        at = @At("HEAD"),
        cancellable = true
    )
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    private void platform$isValid(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (BlockEntityType.SIGN.equals(this) && (state.getBlock() instanceof SignBlock || state.getBlock() instanceof WallSignBlock)) {
            cir.setReturnValue(true);
        }

        if (BlockEntityType.HANGING_SIGN.equals(this) && (state.getBlock() instanceof CeilingHangingSignBlock || state.getBlock() instanceof WallHangingSignBlock)) {
            cir.setReturnValue(true);
        }
    }
}