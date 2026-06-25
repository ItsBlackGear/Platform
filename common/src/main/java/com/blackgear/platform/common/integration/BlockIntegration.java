package com.blackgear.platform.common.integration;

import com.google.common.collect.Maps;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class BlockIntegration {
    @ExpectPlatform
    public static void registerIntegrations(Consumer<Event> listener) {
        throw new AssertionError();
    }

    public interface Event {
        void registerBlockInteraction(BlockInteraction interaction);

        void registerFuelItem(ItemLike item, int burnTime);

        void registerCompostableItem(ItemLike item, float chance);

        void registerWaxableBlock(Block unwaxed, Block waxed);

        void registerOxidableBlock(Block less, Block more);

        default void registerDispenserBehavior(ItemLike item, DispenseItemBehavior behavior) {
            DispenserBlock.registerBehavior(item, behavior);
        }

        default void registerStrippableBlock(Block target, Block result) {
            AxeItem.STRIPPABLES = Maps.newHashMap(AxeItem.STRIPPABLES);
            AxeItem.STRIPPABLES.putIfAbsent(target, result);
        }

        default void registerFlattenableBlock(Block target, BlockState result) {
            ShovelItem.FLATTENABLES.putIfAbsent(target, result);
        }

        default void registerFlattenableBlock(Block target, Block result) {
            registerFlattenableBlock(target, result.defaultBlockState());
        }

        default void registerFlammableBlock(Block target, int encouragement, int flammability) {
            ((FireBlock) Blocks.FIRE).setFlammable(target, encouragement, flammability);
        }
    }
}