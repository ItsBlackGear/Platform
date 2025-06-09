package com.blackgear.platform.common.data.fabric;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.data.LootModifier;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

import java.lang.reflect.Field;
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
                            Field pools = table.getClass().getDeclaredField("pools");
                            pools.setAccessible(true);
                            List<LootPool> local = (List<LootPool>) pools.get(table);

                            if (local.size() <= index) {
                                Platform.LOGGER.error("Failed to add content to loot pool at index {}: No pools found", index);
                                return false;
                            }

                            LootPool pool = local.get(index);
                            LootPool modified = ((LootPoolAccess) pool).mergeEntries(content);
                            local.set(index, modified);

                            pools.set(table, local);
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