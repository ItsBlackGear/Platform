package com.blackgear.platform.common.integration.neoforge;

import com.blackgear.platform.common.integration.BlockIntegration;
import com.blackgear.platform.common.integration.BlockInteraction;
import com.blackgear.platform.neoforge.CommonLoaderPipelines;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.Util;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public final class BlockIntegrationImpl {
    public static void registerIntegrations(Consumer<BlockIntegration.Event> listener) {
        listener.accept(ForgeBlockIntegration.INSTANCE);
    }
    
    public static class ForgeBlockIntegration implements BlockIntegration.Event {
        public static final ForgeBlockIntegration INSTANCE = new ForgeBlockIntegration();
        
        public static final Object2FloatMap<ItemLike> COMPOSTABLES = Util.make(new Object2FloatOpenHashMap<>(), map -> map.defaultReturnValue(-1.0F));
        public static final BiMap<Block, Block> WEATHERING = HashBiMap.create();
        public static final BiMap<Block, Block> WAXABLE = HashBiMap.create();
        
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
            COMPOSTABLES.putIfAbsent(item.asItem(), chance);
        }
        
        @Override
        public void registerWaxableBlock(Block unwaxed, Block waxed) {
            WAXABLE.put(unwaxed, waxed);
        }
        
        @Override
        public void registerOxidableBlock(Block less, Block more) {
            WEATHERING.put(less, more);
        }
    }
}