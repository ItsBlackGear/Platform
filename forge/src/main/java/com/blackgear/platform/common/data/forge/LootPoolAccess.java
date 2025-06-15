package com.blackgear.platform.common.data.forge;

import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

public interface LootPoolAccess {
    LootPoolEntryContainer[] getEntries();
    void setEntries(LootPoolEntryContainer[] entries);
}