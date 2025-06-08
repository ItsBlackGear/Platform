package com.blackgear.platform.common.data.fabric;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

import java.util.List;

public interface LootPoolAccess {
    LootPool mergeEntries(List<LootPoolEntryContainer> entries);
}