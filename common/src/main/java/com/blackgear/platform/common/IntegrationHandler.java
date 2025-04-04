package com.blackgear.platform.common;

import com.blackgear.platform.common.integration.BlockIntegration;
import com.blackgear.platform.common.integration.Interaction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

@Deprecated(forRemoval = true)
public final class IntegrationHandler {
    public static void setShearable(Block target, float destroySpeed) {
        BlockIntegration.registerIntegrations(event -> event.registerSheareableBlock(target, destroySpeed));
    }

    public static void setShearable(BlockState target, float destroySpeed) {
        BlockIntegration.registerIntegrations(event -> event.registerSheareableBlock(target, destroySpeed));
    }

    public static void setFlattenable(Block target, BlockState result) {
        BlockIntegration.registerIntegrations(event -> event.registerFlattenableBlock(target, result));
    }

    public static void setFlammable(Block target, int encouragement, int flammability) {
        BlockIntegration.registerIntegrations(event -> event.registerFlammableBlock(target, encouragement, flammability));
    }

    public static void setCompostable(ItemLike entry, float chance) {
        BlockIntegration.registerIntegrations(event -> event.registerCompostableItem(entry, chance));
    }

    public static void setStrippable(Block target, Block result) {
        BlockIntegration.registerIntegrations(event -> event.registerStrippableBlock(target, result));
    }

    public static void addFuel(ItemLike item, int burnTime) {
        BlockIntegration.registerIntegrations(event -> event.registerFuelItem(item, burnTime));
    }

    public static void addDispenserBehavior(ItemLike item, DispenseItemBehavior behavior) {
        BlockIntegration.registerIntegrations(event -> event.registerDispenserBehavior(item, behavior));
    }

    public static void addInteraction(Interaction interaction) {
        BlockIntegration.registerIntegrations(event -> event.registerBlockInteraction(interaction));
    }
}