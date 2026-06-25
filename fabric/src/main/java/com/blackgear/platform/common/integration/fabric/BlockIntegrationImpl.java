package com.blackgear.platform.common.integration.fabric;

import com.blackgear.platform.common.integration.BlockInteraction;
import com.blackgear.platform.common.integration.BlockIntegration;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class BlockIntegrationImpl {
    public static void registerIntegrations(Consumer<BlockIntegration.Event> listener) {
        listener.accept(new BlockIntegration.Event() {
            @Override
            public void registerBlockInteraction(BlockInteraction interaction) {
                UseBlockCallback.EVENT.register((player, level, hand, hit) -> interaction.onUse(new UseOnContext(player, hand, hit)));
            }

            @Override
            public void registerFuelItem(ItemLike item, int burnTime) {
                FuelRegistry.INSTANCE.add(item, burnTime);
            }

            @Override
            public void registerCompostableItem(ItemLike item, float chance) {
                CompostingChanceRegistry.INSTANCE.add(item, chance);
            }

            @Override
            public void registerOxidableBlock(Block less, Block more) {
                OxidizableBlocksRegistry.registerOxidizableBlockPair(less, more);
            }

            @Override
            public void registerWaxableBlock(Block unwaxed, Block waxed) {
                OxidizableBlocksRegistry.registerWaxableBlockPair(unwaxed, waxed);
            }
        });
    }
}