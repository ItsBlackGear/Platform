package com.blackgear.platform.fabric;

import com.blackgear.platform.common.data.LootModifierEvent;
import com.blackgear.platform.common.data.LootRegistry;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.Function;

public class FabricCommonEvents {
    public static void bootstrap() {
        setupLootTableModifications();
    }

    public static void setupLootTableModifications() {
        LootTableLoadingCallback.EVENT.register((resourceManager, lootTables, path, builder, setter) -> {
            LootModifierEvent.EVENT.invoker().modify(
                lootTables,
                path,
                new LootModifierEvent.Adder() {
                    @Override
                    public void addPool(LootPool pool) {
                        builder.withPool(pool);
                    }

                    @Override
                    public void addPool(LootPool.Builder pool) {
                        builder.withPool(pool);
                    }
                },
                new LootModifierEvent.Setter() {
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