package com.blackgear.platform.common.forge;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CreativeTabsImpl {
    public static CreativeModeTab create(ResourceLocation location, Supplier<ItemStack> icon) {
        return new CreativeModeTab(location.toString().replace(":", ".")) {
            @Override public ItemStack makeIcon() {
                return icon.get();
            }
        };
    }

    public static CreativeModeTab create(ResourceLocation location, Supplier<ItemStack> icon, Consumer<List<ItemStack>> display) {
        return new CreativeModeTab(location.toString().replace(":", ".")) {
            @Override public ItemStack makeIcon() {
                return icon.get();
            }

            @Override
            public void fillItemList(NonNullList<ItemStack> items) {
                if (display != null) {
                    display.accept(items);
                    return;
                }

                super.fillItemList(items);
            }
        };
    }
}
