package com.blackgear.platform.common.data.fabric;

import com.blackgear.platform.common.data.LootModifier;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.Function;

public class LootModifierImpl {
    public static void modify(LootModifier.LootTableModifier modifier) {
        LootTableLoadingCallback.EVENT.register((resourceManager, lootTables, path, builder, setter) -> {
            modifier.modify(
                lootTables,
                path,
                new LootModifier.LootTableContext() {
                    @Override
                    public void addPool(LootPool pool) {
                        builder.withPool(pool);
                    }

                    @Override
                    public void addPool(LootPool.Builder pool) {
                        builder.withPool(pool);
                    }
                },
                new LootModifier.LootSetter() {
                    @Override
                    public void set(LootTable context) {
                        setter.set(context);
                    }

                    @Override
                    public void setBlock(Block block, Function<Block, LootTable.Builder> factory) {
                        if (path.equals(block.getLootTable())) {
                            this.set(factory.apply(block).build());
                        }
                    }
                }
            );
        });
    }
}
