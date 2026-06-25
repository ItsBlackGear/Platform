package com.blackgear.platform.core.mixin.neoforge;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WeatheringCopper.class)
public interface WeatheringCopperMixin {
    @Inject(method = "method_34740", at = @At("RETURN"), cancellable = true)
    private static void createOxidationLevelIncreasesMap(CallbackInfoReturnable<BiMap<Block, Block>> cir) {
        cir.setReturnValue(HashBiMap.create(cir.getReturnValue()));
    }
}