package com.blackgear.platform.common.data.forge;

import net.minecraft.world.level.storage.loot.LootPool;

import java.util.List;

public interface LootTableAccess {
    List<LootPool> getPools();
    void setPools(List<LootPool> pools);
}