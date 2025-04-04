package com.blackgear.platform.common.integration;

import com.google.common.collect.Maps;
import dev.architectury.injectables.annotations.ExpectPlatform;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class BlockIntegration {
    public static final Object2FloatMap<BlockState> SHEARABLES = new Object2FloatOpenHashMap<>();

    @ExpectPlatform
    public static void registerIntegrations(Consumer<Event> listener) {
        throw new AssertionError();
    }

    public interface Event {
        void registerBlockInteraction(Interaction interaction);

        void registerFuelItem(ItemLike item, int burnTime);

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

        default void registerSheareableBlock(BlockState target, float destroySpeed) {
            SHEARABLES.putIfAbsent(target, destroySpeed);
        }

        default void registerSheareableBlock(Block target, float destroySpeed) {
            registerSheareableBlock(target.defaultBlockState(), destroySpeed);
        }

        default void registerFlammableBlock(Block target, int encouragement, int flammability) {
            ((FireBlock) Blocks.FIRE).setFlammable(target, encouragement, flammability);
        }

        default void registerCompostableItem(ItemLike item, float chance) {
            ComposterBlock.COMPOSTABLES.putIfAbsent(item, chance);
        }
    }
}