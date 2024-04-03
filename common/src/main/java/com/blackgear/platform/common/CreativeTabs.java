package com.blackgear.platform.common;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class CreativeTabs {
    @ExpectPlatform
    public static CreativeModeTab create(ResourceLocation location, Supplier<ItemStack> icon) {
        throw new AssertionError();
    }
}