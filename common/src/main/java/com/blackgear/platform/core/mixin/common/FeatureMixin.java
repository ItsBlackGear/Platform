package com.blackgear.platform.core.mixin.common;

import com.blackgear.platform.core.tags.PBlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Feature.class)
public class FeatureMixin {
    @Inject(
        method = "isDirt",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void isDirt(Block block, CallbackInfoReturnable<Boolean> cir) {
        if (block.is(PBlockTags.DIRT)) {
            cir.setReturnValue(true);
        }
    }
}