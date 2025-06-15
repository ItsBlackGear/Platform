package com.blackgear.platform.core.mixin.forge;

import com.blackgear.platform.common.data.forge.LootTableAccess;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LootTable.class)
public class LootTableMixin implements LootTableAccess {
    @Mutable @Shadow @Final private List<LootPool> pools;

    @Override
    public List<LootPool> getPools() {
        return this.pools;
    }

    @Override
    public void setPools(List<LootPool> pools) {
        this.pools = pools;
    }
}