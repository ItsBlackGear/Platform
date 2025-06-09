package com.blackgear.platform.core.mixin.fabric;

import com.blackgear.platform.common.data.fabric.LootPoolAccess;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(LootPool.class)
public class LootPoolMixin implements LootPoolAccess {
    @Shadow @Final public LootPoolEntryContainer[] entries;
    @Shadow @Final public LootItemCondition[] conditions;
    @Shadow @Final public LootItemFunction[] functions;
    @Shadow @Final public NumberProvider rolls;
    @Shadow @Final public NumberProvider bonusRolls;

    @Invoker("<init>")
    static LootPool create(LootPoolEntryContainer[] entries, LootItemCondition[] conditions, LootItemFunction[] functions, NumberProvider rolls, NumberProvider bonusRolls) {
        throw new AssertionError();
    }

    @Override
    public LootPool mergeEntries(List<LootPoolEntryContainer> entries) {
        // Create a new list from the existing entries array
        List<LootPoolEntryContainer> merged = new ArrayList<>(Arrays.asList(this.entries));
        // Add all new entries to the list
        merged.addAll(entries);
        // Convert the merged list back to an array
        LootPoolEntryContainer[] mergedArray = merged.toArray(new LootPoolEntryContainer[0]);

        return create(
            mergedArray,
            this.conditions,
            this.functions,
            this.rolls,
            this.bonusRolls
        );
    }
}