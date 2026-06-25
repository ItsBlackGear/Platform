package com.blackgear.platform.core.helper.v2;

import com.blackgear.platform.core.CoreRegistry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;
import java.util.function.Supplier;

public class ItemRegistry {
    private final CoreRegistry<Item> items;

    private ItemRegistry(String modId) {
        this.items = CoreRegistry.create(BuiltInRegistries.ITEM, modId);
    }

    public static ItemRegistry create(String modId) {
        return new ItemRegistry(modId);
    }

    public <T extends Item> Supplier<T> register(String name, Supplier<T> item) {
        return new ItemEntry<>(this.items.register(name, item));
    }

    public Supplier<Item> register(String name) {
        return register(name, new Item.Properties());
    }

    public Supplier<Item> register(String name, Item.Properties properties) {
        return register(name, Item::new, properties);
    }

    public Supplier<Item> register(String name, Function<Item.Properties, Item> function) {
        return register(name, function, new Item.Properties());
    }

    public Supplier<Item> register(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
        return this.items.register(name, () -> factory.apply(properties));
    }

    public void register() {
        this.items.register();
    }

    public CoreRegistry<Item> registry() {
        return this.items;
    }
}