package com.blackgear.platform.core.mixin.neoforge;

import com.blackgear.platform.common.data.neoforge.LootPoolAccess;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LootPool.class)
public class LootPoolMixin implements LootPoolAccess {
    @Mutable @Shadow @Final private List<LootPoolEntryContainer> entries;

    @Override
    public List<LootPoolEntryContainer> getEntries() {
        return this.entries;
    }

    @Override
    public void setEntries(List<LootPoolEntryContainer> entries) {
        this.entries = entries;
    }
}