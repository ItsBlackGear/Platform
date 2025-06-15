package com.blackgear.platform.core.mixin.forge;

import com.blackgear.platform.common.data.forge.LootPoolAccess;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LootPool.class)
public class LootPoolMixin implements LootPoolAccess {
    @Mutable @Shadow @Final LootPoolEntryContainer[] entries;

    @Override
    public LootPoolEntryContainer[] getEntries() {
        return this.entries;
    }

    @Override
    public void setEntries(LootPoolEntryContainer[] entries) {
        this.entries = entries;
    }
}
