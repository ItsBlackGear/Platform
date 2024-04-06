package com.blackgear.platform.common;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for creating and injecting into creative tabs.
 */
public class CreativeTabs {
    public static final List<Injectable> INJECTABLES = new ArrayList<>();

    @ExpectPlatform
    public static CreativeModeTab create(ResourceLocation location, Supplier<ItemStack> icon) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CreativeModeTab create(ResourceLocation location, Supplier<ItemStack> icon, Consumer<List<ItemStack>> display) {
        throw new AssertionError();
    }

    public static void addItemsAfter(Item target, Stream<ItemLike> items) {
        List<Item> entries = items.map(ItemLike::asItem).collect(Collectors.toList());
        Collections.reverse(entries);
        INJECTABLES.add(new Injectable(target, entries));
    }

    public static void addItemsAfter(Item target, ItemLike item) {
        INJECTABLES.add(new Injectable(target, List.of(item.asItem())));
    }

    public record Injectable(Item target, List<Item> items) {}
}