package com.blackgear.platform.core.mixin.common;

import com.blackgear.platform.core.tags.PBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BushBlock.class)
public class BushBlockMixin {
    @Inject(
        method = "mayPlaceOn",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(PBlockTags.DIRT)) {
            cir.setReturnValue(true);
        }
    }
}