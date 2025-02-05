package com.blackgear.platform.core.mixin.common;

import com.blackgear.platform.common.IntegrationHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShearsItem.class)
public class ShearsItemMixin {
    @Inject(
        method = "getDestroySpeed",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$handleShearables(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
        if (IntegrationHandler.SHEARABLES.containsKey(state)) {
            cir.setReturnValue(IntegrationHandler.SHEARABLES.getFloat(state));
        }
    }
}