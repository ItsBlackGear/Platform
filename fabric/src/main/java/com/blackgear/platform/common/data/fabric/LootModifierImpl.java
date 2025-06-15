package com.blackgear.platform.common.data.fabric;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.data.LootModifier;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

import java.util.ArrayList;
import java.util.List;

public class LootModifierImpl {
    public static void modify(LootModifier.LootTableModifier modifier) {
        LootTableEvents.MODIFY.register((resourceManager, lootTables, path, table, source) -> {
            modifier.modify(
                path,
                new LootModifier.LootTableContext() {
                    @Override
                    public void addPool(LootPool.Builder pool) {
                        table.withPool(pool);
                    }

                    @Override
                    public boolean addToPool(int index, ArrayList<LootPoolEntryContainer> content) {
                        try {
                            List<LootPool> pools = ((LootTableAccess) table).getPools();

                            if (pools.size() <= index) {
                                Platform.LOGGER.error("Failed to add content to loot pool at index {}: No pools found", index);
                                return false;
                            }

                            LootPool pool = pools.get(index);
                            LootPool modified = ((LootPoolAccess) pool).mergeEntries(content);
                            pools.set(index, modified);

                            ((LootTableAccess) table).setPools(pools);
                            return true;
                        } catch (Throwable t) {
                            Platform.LOGGER.error("Failed to add content to loot pool at index {}: {}", index, t.getMessage(), t);
                            return false;
                        }
                    }
                },
                source.isBuiltin()
            );
        });
    }
}