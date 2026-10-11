package com.blackgear.platform.core.mixin.neoforge;

import com.blackgear.platform.common.integration.neoforge.BlockIntegrationImpl.ForgeBlockIntegration;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.DataMapHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DataMapHooks.class)
public class DataMapHooksMixin {
    @Inject(method = "getNextOxidizedStage", at = @At("RETURN"), cancellable = true, remap = false)
    private static void injectNextOxidizedStage(Block block, CallbackInfoReturnable<Block> cir) {
        if (cir.getReturnValue() == null) {
            Block next = ForgeBlockIntegration.WEATHERING.get(block);
            if (next != null) cir.setReturnValue(next);
        }
    }
    
    @Inject(method = "getPreviousOxidizedStage", at = @At("RETURN"), cancellable = true, remap = false)
    private static void injectPreviousOxidizedStage(Block block, CallbackInfoReturnable<Block> cir) {
        if (cir.getReturnValue() == null) {
            Block prev = ForgeBlockIntegration.WEATHERING.inverse().get(block);
            if (prev != null) cir.setReturnValue(prev);
        }
    }
    
    @Inject(method = "getBlockWaxed", at = @At("RETURN"), cancellable = true, remap = false)
    private static void injectBlockWaxed(Block block, CallbackInfoReturnable<Block> cir) {
        if (cir.getReturnValue() == null) {
            Block waxed = ForgeBlockIntegration.WAXABLE.get(block);
            if (waxed != null) cir.setReturnValue(waxed);
        }
    }
    
    @Inject(method = "getBlockUnwaxed", at = @At("RETURN"), cancellable = true, remap = false)
    private static void injectBlockUnwaxed(Block block, CallbackInfoReturnable<Block> cir) {
        if (cir.getReturnValue() == null) {
            Block unwaxed = ForgeBlockIntegration.WAXABLE.inverse().get(block);
            if (unwaxed != null) cir.setReturnValue(unwaxed);
        }
    }
}