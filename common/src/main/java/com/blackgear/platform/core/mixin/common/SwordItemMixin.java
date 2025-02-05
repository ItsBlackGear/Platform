package com.blackgear.platform.core.mixin.common;

import com.blackgear.platform.common.IntegrationHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwordItem.class)
public class SwordItemMixin {
    @Inject(
        method = "getDestroySpeed",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$handleSwordables(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
        if (IntegrationHandler.SWORDABLES.containsKey(state)) {
            cir.setReturnValue(IntegrationHandler.SWORDABLES.getFloat(state));
        }
    }
}