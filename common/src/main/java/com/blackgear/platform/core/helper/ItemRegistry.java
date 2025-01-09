package com.blackgear.platform.core.helper;

import com.blackgear.platform.core.CoreRegistry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;
import java.util.function.Supplier;

public class ItemRegistry {
    private final CoreRegistry<Item> items;
    private final CreativeModeTab tab;
    
    private ItemRegistry(String modid, CreativeModeTab tab) {
        this.items = CoreRegistry.create(Registry.ITEM, modid);
        this.tab = tab;
    }
    
    public static ItemRegistry create(String modid) {
        return new ItemRegistry(modid, null);
    }

    public static ItemRegistry create(String modid, CreativeModeTab tab) {
        return new ItemRegistry(modid, tab);
    }
    
    public <T extends Item> Supplier<T> register(String name, Supplier<T> item) {
        return this.items.register(name, item);
    }
    
    public Supplier<Item> register(String name) {
        return register(
            name,
            new Item.Properties()
        );
    }
    
    public Supplier<Item> register(String name, Item.Properties properties) {
        return register(
            name,
            Item::new,
            properties
        );
    }
    
    public Supplier<Item> register(String name, Function<Item.Properties, Item> function) {
        return register(
            name,
            function,
            new Item.Properties()
        );
    }
    
    public Supplier<Item> register(String name, Function<Item.Properties, Item> function, Item.Properties properties) {
        Item item = function.apply(
            (this.tab != null)
                ? properties.tab(this.tab)
                : properties
        );
        return this.items.register(name, () -> item);
    }
    
    public <T extends Mob> Supplier<Item> spawnEgg(String name, Supplier<EntityType<T>> entity, int primaryColor, int secondaryColor, Item.Properties properties) {
        return this.items.register(
            name,
            () -> createSpawnEgg(
                entity,
                primaryColor,
                secondaryColor,
                (this.tab != null)
                    ? properties.tab(this.tab)
                    : properties
            )
        );
    }
    
    public Supplier<Item> fromBlock(Supplier<Block> block) {
        return () -> block.get().asItem();
    }
    
    public void register() {
        this.items.register();
    }
    
    @ExpectPlatform
    public static <T extends Mob> Item createSpawnEgg(Supplier<EntityType<T>> entity, int primaryColor, int secondaryColor, Item.Properties properties) {
        throw new AssertionError();
    }
}