package com.blackgear.platform.core.mixin.fabric.loot;

import com.blackgear.platform.Platform;

import com.blackgear.platform.common.data.fabric.LootPoolAccess;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import com.google.common.collect.Lists;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(LootPool.class)
public class LootPoolMixin implements LootPoolAccess {
    @Shadow @Final public NumberProvider rolls;
    @Shadow @Final public NumberProvider bonusRolls;
    @Shadow @Final public List<LootItemCondition> conditions;
    @Shadow @Final public List<LootItemFunction> functions;
    @Shadow @Final public List<LootPoolEntryContainer> entries;

    @Invoker("<init>")
    static LootPool create(List<LootPoolEntryContainer> entries, List<LootItemCondition> conditions, List<LootItemFunction> functions, NumberProvider rolls, NumberProvider bonusRolls) {
        throw new AssertionError();
    }

    @Override
    public LootPool mergeEntries(List<LootPoolEntryContainer> contents) {
        final List<LootPoolEntryContainer> merged = Lists.newArrayList(entries);
        merged.addAll(contents);

        return create(
            merged,
            this.conditions,
            this.functions,
            this.rolls,
            this.bonusRolls
        );
    }
}