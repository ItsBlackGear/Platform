package com.blackgear.platform.core.mixin.neoforge;

import com.blackgear.platform.common.integration.neoforge.BlockIntegrationImpl.ForgeBlockIntegration;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ComposterBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin {
    @Inject(method = "getValue", at = @At("HEAD"), cancellable = true)
    private static void platform$getCompostableValue(ItemStack item, CallbackInfoReturnable<Float> cir) {
        float chance = ForgeBlockIntegration.COMPOSTABLES.getFloat(item.getItem());
        if (chance > 0.0F) cir.setReturnValue(chance);
    }
}