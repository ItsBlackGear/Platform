package com.blackgear.platform.common.data.fabric;

import com.blackgear.platform.common.data.LootModifier;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.world.level.storage.loot.LootPool;

public class LootModifierImpl {
    public static void modify(LootModifier.LootTableModifier modifier) {
        LootTableEvents.MODIFY.register((resourceManager, lootTables, path, tableBuilder, source) -> {
            modifier.modify(
                lootTables,
                path,
                new LootModifier.LootTableContext() {
                    @Override
                    public void addPool(LootPool pool) {
                        tableBuilder.pool(pool);
                    }
                    
                    @Override
                    public void addPool(LootPool.Builder pool) {
                        tableBuilder.withPool(pool);
                    }
                },
                source.isBuiltin()
            );
        });
    }
}