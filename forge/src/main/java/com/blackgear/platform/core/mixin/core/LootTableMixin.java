package com.blackgear.platform.core.mixin.core;

import com.blackgear.platform.common.data.loot.LootBuilder;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Arrays;
import java.util.List;

@Mixin(LootTable.class)
public abstract class LootTableMixin implements LootBuilder {
    @Shadow @Final private List<LootPool> pools;
    @Shadow @Final private LootItemFunction[] functions;
    @Shadow @Final private LootContextParamSet paramSet;

    @Override
    public List<LootPool> getPools() {
        return this.pools;
    }

    @Override
    public List<LootItemFunction> getFunctions() {
        return Arrays.asList(this.functions);
    }

    @Override
    public LootContextParamSet getType() {
        return this.paramSet;
    }
}