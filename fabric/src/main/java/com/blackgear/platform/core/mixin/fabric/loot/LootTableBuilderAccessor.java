package com.blackgear.platform.core.mixin.fabric.loot;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootTable.Builder.class)
public interface LootTableBuilderAccessor {
    @Accessor ImmutableList.Builder<LootPool> getPools();

    @Accessor("pools")
    void setPools(ImmutableList.Builder<LootPool> pools);
}