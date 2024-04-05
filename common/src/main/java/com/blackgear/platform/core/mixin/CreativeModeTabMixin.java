package com.blackgear.platform.core.mixin;

import com.blackgear.platform.common.CreativeTabs;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {
    @Inject(
        method = "fillItemList",
        at = @At("TAIL")
    )
    private void fillItemList(NonNullList<ItemStack> items, CallbackInfo ci) {
        CreativeTabs.INJECTABLES.forEach(entry -> {
            List<Item> stream = entry.items();
            Collections.reverse(stream);

            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).is(entry.target())) {
                    int index = i;
                    stream.forEach(item -> items.add(index + 1, new ItemStack(item)));
                }
            }
        });
    }
}