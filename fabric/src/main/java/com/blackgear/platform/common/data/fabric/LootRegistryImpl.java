package com.blackgear.platform.common.data.fabric;

import com.blackgear.platform.common.data.LootRegistry;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.world.level.storage.loot.LootPool;

public class LootRegistryImpl {
    public static void modify(LootRegistry.LootTableModifier modifier) {
        LootTableLoadingCallback.EVENT.register((resourceManager, lootTables, path, tableBuilder, source) -> {
            modifier.modify(
                lootTables,
                path,
                new LootRegistry.LootTableContext() {
                    @Override
                    public void addPool(LootPool pool) {
                        tableBuilder.withPool(pool);
                    }

                    @Override
                    public void addPool(LootPool.Builder pool) {
                        tableBuilder.withPool(pool);
                    }
                },
                true
            );
        });
    }
}