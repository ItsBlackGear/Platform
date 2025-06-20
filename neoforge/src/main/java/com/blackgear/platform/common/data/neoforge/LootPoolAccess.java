package com.blackgear.platform.common.data.neoforge;

import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

import java.util.List;

public interface LootPoolAccess {
    List<LootPoolEntryContainer> getEntries();
    void setEntries(List<LootPoolEntryContainer> entries);
}