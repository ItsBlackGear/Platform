package com.blackgear.platform.common.data.loot;

import com.blackgear.platform.core.mixin.access.LootTableBuilderAccessor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.Collection;
import java.util.Objects;

/**
 * Builder class for creating and modifying LootTable instances.
 * Based on Fabric's FabricLootSupplierBuilder
 */
public class LootTableBuilder extends LootTable.Builder {
    private final LootTableBuilderAccessor extension = (LootTableBuilderAccessor) this;

    private LootTableBuilder() {}

    private LootTableBuilder(LootTable table) {
        copyFrom(table, true);
    }

    @Override
    public LootTableBuilder withPool(LootPool.Builder builder) {
        super.withPool(builder);
        return this;
    }

    @Override
    public LootTableBuilder setParamSet(LootContextParamSet parameterSet) {
        super.setParamSet(parameterSet);
        return this;
    }

    @Override
    public LootTableBuilder apply(LootItemFunction.Builder builder) {
        super.apply(builder);
        return this;
    }

    /**
     * Adds a LootPool to the builder.
     *
     * @param pool the LootPool to add
     * @return the current builder instance
     */
    public LootTableBuilder withPool(LootPool pool) {
        Objects.requireNonNull(pool, "LootPool cannot be null");
        this.extension.getPools().add(pool);
        return this;
    }

    /**
     * Adds a LootItemFunction to the builder.
     *
     * @param function the LootItemFunction to add
     * @return the current builder instance
     */
    public LootTableBuilder withFunction(LootItemFunction function) {
        Objects.requireNonNull(function, "LootItemFunction cannot be null");
        this.extension.getFunctions().add(function);
        return this;
    }

    /**
     * Adds multiple LootPools to the builder.
     *
     * @param pools the collection of LootPools to add
     * @return the current builder instance
     */
    public LootTableBuilder withPools(Collection<LootPool> pools) {
        Objects.requireNonNull(pools, "LootPools collection cannot be null");
        this.extension.getPools().addAll(pools);
        return this;
    }

    /**
     * Adds multiple LootItemFunctions to the builder.
     *
     * @param functions the collection of LootItemFunctions to add
     * @return the current builder instance
     */
    public LootTableBuilder withFunctions(Collection<LootItemFunction> functions) {
        Objects.requireNonNull(functions, "LootItemFunctions collection cannot be null");
        this.extension.getFunctions().addAll(functions);
        return this;
    }

    /**
     * Copies the pools and functions from another LootTable.
     *
     * @param table the LootTable to copy from
     * @return the current builder instance
     */
    public LootTableBuilder copyFrom(LootTable table) {
        return copyFrom(table, false);
    }

    /**
     * Copies the pools and functions from another LootTable, optionally copying the parameter set.
     *
     * @param table the LootTable to copy from
     * @param copyType whether to copy the parameter set
     * @return the current builder instance
     */
    public LootTableBuilder copyFrom(LootTable table, boolean copyType) {
        LootBuilder builder = (LootBuilder) table;
        this.extension.getPools().addAll(builder.getPools());
        this.extension.getFunctions().addAll(builder.getFunctions());

        if (copyType) {
            this.setParamSet(builder.getType());
        }

        return this;
    }

    /**
     * Creates a new LootTableBuilder instance from an existing LootTable.
     *
     * @param table the LootTable to base the builder on
     * @return a new LootTableBuilder instance
     */
    public static LootTableBuilder of(LootTable table) {
        return new LootTableBuilder(table);
    }

    /**
     * Creates a new empty LootTableBuilder instance.
     *
     * @return a new LootTableBuilder instance
     */
    public static LootTableBuilder builder() {
        return new LootTableBuilder();
    }
}