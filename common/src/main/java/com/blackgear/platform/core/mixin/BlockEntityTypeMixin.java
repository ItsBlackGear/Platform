package com.blackgear.platform.core.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
    private void platform$isValid(Block block, CallbackInfoReturnable<Boolean> cir) {
        if (BlockEntityType.SIGN.equals(this) && (block instanceof SignBlock || block instanceof WallSignBlock)) {
            cir.setReturnValue(true);
        }
    }
}