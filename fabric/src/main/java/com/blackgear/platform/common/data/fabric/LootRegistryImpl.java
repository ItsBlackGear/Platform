package com.blackgear.platform.common.data.fabric;

import com.blackgear.platform.common.data.LootRegistry;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.Function;

public class LootRegistryImpl {
    public static void modify(LootRegistry.LootTableModifier modifier) {
        LootTableLoadingCallback.EVENT.register((manager, tables, path, builder, setter) -> {
            modifier.modify(
                tables,
                tables.get(path),
                path,
                new LootRegistry.Adder() {
                    @Override
                    public void addPool(LootPool pool) {
                        builder.withPool(pool);
                    }

                    @Override
                    public void addPool(LootPool.Builder pool) {
                        builder.withPool(pool);
                    }
                },
                new LootRegistry.Setter() {
                    @Override
                    public void setTable(LootTable lootTable) {
                        setter.set(lootTable);
                    }
                    
                    @Override
                    public void setBlock(Block block, Function<Block, LootTable.Builder> factory) {
                        if (path.equals(block.getLootTable())) {
                            this.setTable(factory.apply(block).build());
                        }
                    }
                }
            );
        });
    }
}