package com.blackgear.platform.common.data;

import com.blackgear.platform.core.util.event.Event;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;

import java.util.function.Function;

public interface LootModifierEvent {
    Event<LootModifierEvent> EVENT = Event.create(LootModifierEvent.class);

    void modify(LootTables lootTables, ResourceLocation path, Adder adder, Setter setter);

    interface Adder {
        void addPool(LootPool pool);

        default void addPool(LootPool.Builder pool) {
            this.addPool(pool.build());
        }
    }

    interface Setter {
        void setTable(LootTable lootTable);

        void setBlock(Block block, Function<Block, LootTable.Builder> factory);
    }
}