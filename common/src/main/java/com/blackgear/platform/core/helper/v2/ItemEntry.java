package com.blackgear.platform.core.helper.v2;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public class ItemEntry<T extends Item> implements Supplier<T>, ItemLike {
    private final Supplier<T> factory;

    public ItemEntry(Supplier<T> factory) {
        this.factory = factory;
    }

    @Override
    public T get() {
        return this.factory.get();
    }

    @Override
    public Item asItem() {
        return this.get();
    }
}