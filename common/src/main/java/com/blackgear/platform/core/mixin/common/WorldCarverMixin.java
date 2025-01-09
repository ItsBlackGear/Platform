package com.blackgear.platform.core.mixin.common;

import com.blackgear.platform.core.tags.PBlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldCarver.class)
public class WorldCarverMixin {
    @Inject(
        method = "canReplaceBlock(Lnet/minecraft/world/level/block/state/BlockState;)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$extendCarvingBlocks(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(PBlockTags.CARVERS_CAN_REPLACE)) {
            cir.setReturnValue(true);
        }
    }
}