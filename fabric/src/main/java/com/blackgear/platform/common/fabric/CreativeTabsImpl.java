package com.blackgear.platform.common.fabric;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class CreativeTabsImpl {
    public static CreativeModeTab create(ResourceLocation location, Supplier<ItemStack> icon) {
        return FabricItemGroupBuilder.build(location, icon);
    }
}