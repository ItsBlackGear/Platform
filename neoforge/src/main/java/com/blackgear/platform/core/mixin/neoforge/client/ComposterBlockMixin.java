package com.blackgear.platform.core.mixin.neoforge.client;

import com.blackgear.platform.common.integration.neoforge.BlockIntegrationImpl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ComposterBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin {
    @Inject(method = "bootStrap", at = @At("HEAD"))
    private static void vb$bootstrap(CallbackInfo ci) {
        BlockIntegrationImpl.COMPOSTABLES.defaultReturnValue(-1.0F);
    }

    @Inject(method = "getValue", at = @At("HEAD"), cancellable = true)
    private static void vb$getCompostableValue(ItemStack item, CallbackInfoReturnable<Float> cir) {
        if (BlockIntegrationImpl.COMPOSTABLES.containsKey(item.getItem())) {
            cir.setReturnValue(BlockIntegrationImpl.COMPOSTABLES.getFloat(item.getItem()));
        }
    }
}