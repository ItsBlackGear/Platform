package com.blackgear.platform.core.helper;

import com.blackgear.platform.core.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockRegistry {
    private final CoreRegistry<Block> blocks;
    private final CoreRegistry<Item> items;

    private BlockRegistry(String modid) {
        this.blocks = CoreRegistry.create(Registry.BLOCK, modid);
        this.items = CoreRegistry.create(Registry.ITEM, modid);
    }

    public static BlockRegistry create(String modid) {
        return new BlockRegistry(modid);
    }

    public Supplier<Block> register(
        String name,
        Function<BlockBehaviour.Properties, Block> blockFactory,
        BlockBehaviour.Properties blockProperties,
        BiFunction<Block, Item.Properties, Item> itemFactory,
        Item.Properties itemProperties
    ) {
        return this.register(name, blockFactory, blockProperties, name, itemFactory, itemProperties);
    }

    public Supplier<Block> register(
        String blockName,
        Function<BlockBehaviour.Properties, Block> blockFactory,
        BlockBehaviour.Properties blockProperties,
        String itemName,
        BiFunction<Block, Item.Properties, Item> itemFactory,
        Item.Properties itemProperties
    ) {
        Supplier<Block> block = this.registerNoItem(blockName, blockFactory, blockProperties);
        this.items.register(itemName, () -> itemFactory.apply(block.get(), itemProperties));
        return block;
    }

    public Supplier<Block> register(
        String blockName,
        Supplier<Block> block,
        String itemName,
        Function<Supplier<Block>, Item> item
    ) {
        Supplier<Block> entry = this.registerNoItem(blockName, block);
        this.items.register(itemName, () -> item.apply(entry));
        return entry;
    }

    public Supplier<Block> register(
        String name,
        Supplier<Block> block,
        Function<Supplier<Block>, Item> item
    ) {
        Supplier<Block> entry = this.registerNoItem(name, block);
        this.items.register(name, () -> item.apply(entry));
        return entry;
    }

    public Supplier<Block> register(String name, BlockBehaviour.Properties properties) {
        return this.register(name, () -> new Block(properties));
    }

    public Supplier<Block> register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        return this.register(name, () -> factory.apply(properties));
    }
    public Supplier<Block> register(String name, Supplier<Block> block) {
        return this.register(name, block, entry -> new BlockItem(entry.get(), new Item.Properties()));
    }

    public Supplier<Block> registerNoItem(String name, BlockBehaviour.Properties properties) {
        return this.registerNoItem(name, () -> new Block(properties));
    }

    public Supplier<Block> registerNoItem(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        return this.registerNoItem(name, () -> factory.apply(properties));
    }

    public Supplier<Block> registerNoItem(String name, Supplier<Block> block) {
        return this.blocks.register(name, block);
    }

    public void register() {
        this.blocks.register();
        this.items.register();
    }
}