package com.blackgear.platform.core.helper;

import com.blackgear.platform.common.block.BlockProperties;
import com.blackgear.platform.core.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockRegistry {
    private final CoreRegistry<Block> blocks;
    private final CoreRegistry<Item> items;
    private final CreativeModeTab tab;

    private BlockRegistry(String modid, CreativeModeTab tab) {
        this.blocks = CoreRegistry.create(Registry.BLOCK, modid);
        this.items = CoreRegistry.create(Registry.ITEM, modid);
        this.tab = tab;
    }
    
    public static BlockRegistry create(String modid) {
        return new BlockRegistry(modid, null);
    }

    public static BlockRegistry create(String modid, CreativeModeTab tab) {
        return new BlockRegistry(modid, tab);
    }
    
    public Supplier<Block> register(
        String name,
        Function<BlockBehaviour.Properties, Block> blockFactory,
        BlockProperties.Builder blockProperties,
        BiFunction<Block, Item.Properties, Item> itemFactory,
        Item.Properties itemProperties
    ) {
        return this.register(name, blockFactory, blockProperties, name, itemFactory, itemProperties);
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
        BlockProperties.Builder blockProperties,
        String itemName,
        BiFunction<Block, Item.Properties, Item> itemFactory,
        Item.Properties itemProperties
    ) {
        return this.register(blockName, blockFactory, blockProperties.build(), itemName, itemFactory, itemProperties);
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
        Function<BlockBehaviour.Properties, Block> factory,
        BlockProperties.Builder properties,
        String itemName,
        Function<Supplier<Block>, Item> item
    ) {
        Supplier<Block> entry = this.registerNoItem(blockName, factory, properties);
        this.items.register(itemName, () -> item.apply(entry));
        return entry;
    }
    
    public Supplier<Block> register(String blockName, Supplier<Block> block, String itemName, Function<Supplier<Block>, Item> item) {
        Supplier<Block> entry = this.registerNoItem(blockName, block);
        this.items.register(itemName, () -> item.apply(entry));
        return entry;
    }
    
    public Supplier<Block> register(String name, Supplier<Block> block, Function<Supplier<Block>, Item> item) {
        Supplier<Block> entry = this.registerNoItem(name, block);
        this.items.register(name, () -> item.apply(entry));
        return entry;
    }
    
    public Supplier<Block> register(String name, BlockProperties.Builder builder) {
        return this.register(name, () -> new Block(builder.build()));
    }
    
    public Supplier<Block> register(String name, BlockBehaviour.Properties properties) {
        return this.register(name, () -> new Block(properties));
    }
    
    public Supplier<Block> register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockProperties.Builder builder) {
        return this.register(name, factory, builder.build());
    }
    
    public Supplier<Block> register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        return this.register(name, () -> factory.apply(properties));
    }

    public Supplier<Block> register(String name, Supplier<Block> block) {
        return this.register(name, block, entry -> {
            Item.Properties properties = (this.tab != null)
                ? new Item.Properties().tab(this.tab)
                : new Item.Properties();
            return new BlockItem(entry.get(), properties);
        });
    }
    
    public Supplier<Block> registerNoItem(String name, BlockProperties.Builder builder) {
        return this.registerNoItem(name, () -> new Block(builder.build()));
    }
    
    public Supplier<Block> registerNoItem(String name, BlockBehaviour.Properties properties) {
        return this.registerNoItem(name, () -> new Block(properties));
    }
    
    public Supplier<Block> registerNoItem(String name, Function<BlockBehaviour.Properties, Block> factory, BlockProperties.Builder builder) {
        return this.registerNoItem(name, factory, builder.build());
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