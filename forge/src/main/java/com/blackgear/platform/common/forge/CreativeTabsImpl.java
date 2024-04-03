package com.blackgear.platform.common.forge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CreativeTabsImpl {
    public static CreativeModeTab create(ResourceLocation location, Supplier<ItemStack> icon) {
        return new CreativeModeTab(location.toString().replace(":", ".")) {
            @Override public @NotNull ItemStack makeIcon() {
                return icon.get();
            }
        };
    }
}
