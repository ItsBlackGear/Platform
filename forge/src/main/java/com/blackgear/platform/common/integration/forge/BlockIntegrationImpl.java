package com.blackgear.platform.common.integration.forge;

import com.blackgear.platform.common.integration.BlockIntegration;
import com.blackgear.platform.common.integration.BlockInteraction;
import com.blackgear.platform.forge.CommonLoaderPipelines;
import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.WeatheringCopper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockIntegrationImpl {
    private static final Map<Block, Block> OXIDABLES = new HashMap<>();
    private static final Map<Block, Block> WAXABLES = new HashMap<>();
    
    public static void registerIntegrations(Consumer<BlockIntegration.Event> listener) {
        listener.accept(new BlockIntegration.Event() {
            @Override
            public void registerBlockInteraction(BlockInteraction interaction) {
                CommonLoaderPipelines.BLOCK_INTERACTION.add(event -> {
                    InteractionResult result = interaction.onUse(new UseOnContext(event.getEntity(), event.getHand(), event.getHitVec()));
                    if (result != InteractionResult.PASS) {
                        event.setCanceled(true);
                        event.setCancellationResult(result);
                    }
                });
            }
            
            @Override
            public void registerFuelItem(ItemLike item, int burnTime) {
                CommonLoaderPipelines.FURNACE_FUEL.add(event -> {
                    if (event.getItemStack().is(item.asItem())) {
                        event.setBurnTime(burnTime);
                    }
                });
            }
            
            @Override
            public void registerFuelItem(TagKey<Item> tag, int burnTime) {
                CommonLoaderPipelines.FURNACE_FUEL.add(event -> {
                    if (event.getItemStack().is(tag)) {
                        event.setBurnTime(burnTime);
                    }
                });
            }
            
            @Override
            public void registerCompostableItem(ItemLike item, float chance) {
                ComposterBlock.COMPOSTABLES.putIfAbsent(item.asItem(), chance);
            }
            
            @Override
            public void registerOxidableBlock(Block less, Block more) {
                OXIDABLES.put(less, more);
            }
            
            @Override
            public void registerWaxableBlock(Block unwaxed, Block waxed) {
                WAXABLES.put(unwaxed, waxed);
            }
        });
        
        injectOxidables();
        injectWaxables();
    }
    
    @SuppressWarnings("unchecked")
    private static void injectOxidables() {
        if (OXIDABLES.isEmpty()) return;
        
        try {
            Field delegateField = WeatheringCopper.NEXT_BY_BLOCK.getClass().getDeclaredField("delegate");
            delegateField.setAccessible(true);
            
            Supplier<BiMap<Block, Block>> originalDelegate = (Supplier<BiMap<Block, Block>>) delegateField.get(WeatheringCopper.NEXT_BY_BLOCK);
            
            com.google.common.base.Supplier<BiMap<Block, Block>> newDelegate = () -> {
                ImmutableBiMap.Builder<Block, Block> builder = ImmutableBiMap.builder();
                builder.putAll(originalDelegate.get());
                
                OXIDABLES.forEach((less, more) -> {
                    try {
                        builder.put(less, more);
                    } catch (IllegalArgumentException e) {
                        /* NO-OP */
                    }
                });
                return builder.build();
            };
            
            delegateField.set(WeatheringCopper.NEXT_BY_BLOCK, newDelegate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject custom oxidable blocks into WeatheringCopper", e);
        }
    }
    
    private static void injectWaxables() {
        if (WAXABLES.isEmpty()) return;
        
        Supplier<BiMap<Block, Block>> originalWaxables = HoneycombItem.WAXABLES;
        
        HoneycombItem.WAXABLES = Suppliers.memoize(() -> {
            ImmutableBiMap.Builder<Block, Block> builder = ImmutableBiMap.builder();
            builder.putAll(originalWaxables.get());
            
            WAXABLES.forEach((unwaxed, waxed) -> {
                try {
                    builder.put(unwaxed, waxed);
                } catch (IllegalArgumentException e) {
                    /* NO-OP */
                }
            });
            
            return builder.build();
        });
    }
}