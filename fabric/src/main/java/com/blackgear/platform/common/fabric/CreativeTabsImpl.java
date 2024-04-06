package com.blackgear.platform.common.fabric;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CreativeTabsImpl {
    public static CreativeModeTab create(ResourceLocation location, Supplier<ItemStack> icon) {
        return FabricItemGroupBuilder.create(location)
            .icon(icon)
            .build();
    }

    public static CreativeModeTab create(ResourceLocation location, Supplier<ItemStack> icon, Consumer<List<ItemStack>> display) {
        return FabricItemGroupBuilder.create(location)
            .icon(icon)
            .appendItems(display)
            .build();
    }
}