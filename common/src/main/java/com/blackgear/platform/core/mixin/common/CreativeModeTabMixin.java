package com.blackgear.platform.core.mixin.common;

import com.blackgear.platform.common.CreativeTabs;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {
    @Inject(
        method = "fillItemList",
        at = @At("TAIL")
    )
    private void fillItemList(NonNullList<ItemStack> items, CallbackInfo ci) {
        CreativeTabs.MODIFICATIONS.forEach(consumer -> {
            for (int i = 0; i < items.size(); i++) {
                ItemStack stack = items.get(i);
                List<ItemStack> stacks = new ArrayList<>();
                consumer.accept(stack, stacks);
                items.addAll(i + 1, stacks);
                i += stacks.size();
            }
        });
    }
}