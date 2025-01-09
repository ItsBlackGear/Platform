package com.blackgear.platform.common.data.loot;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.List;

/*
 * Inspired on Fabric's FabricLootSupplier
 */
public interface LootBuilder {
    default LootTable asVanilla() {
        return (LootTable) this;
    }

    List<LootPool> getPools();

    List<LootItemFunction> getFunctions();

    LootContextParamSet getType();
}