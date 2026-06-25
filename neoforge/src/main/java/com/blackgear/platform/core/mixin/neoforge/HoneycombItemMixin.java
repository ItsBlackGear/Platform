package com.blackgear.platform.core.mixin.neoforge;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoneycombItem.class)
public class HoneycombItemMixin {
    @Inject(method = "method_34723", at = @At("RETURN"), cancellable = true)
    private static void createUnwaxedToWaxedMap(CallbackInfoReturnable<BiMap<Block, Block>> cir) {
        cir.setReturnValue(HashBiMap.create(cir.getReturnValue()));
    }
}