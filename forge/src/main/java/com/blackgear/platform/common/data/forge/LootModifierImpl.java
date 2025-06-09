package com.blackgear.platform.common.data.forge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.data.LootModifier;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = Platform.MOD_ID)
public class LootModifierImpl {
    private static final Set<Consumer<LootTableLoadEvent>> MODIFICATIONS = ConcurrentHashMap.newKeySet();
    
    public static void modify(LootModifier.LootTableModifier modifier) {
        MODIFICATIONS.add(event -> {
            modifier.modify(
                event.getName(),
                new LootModifier.LootTableContext() {
                    @Override
                    public void addPool(LootPool.Builder pool) {
                        event.getTable().addPool(pool.build());
                    }

                    @Override
                    public boolean addToPool(int index, ArrayList<LootPoolEntryContainer> content) {
                        LootTable table = event.getTable();

                        try {
                            Field pools = table.getClass().getDeclaredField("pools");
                            pools.setAccessible(true);
                            List<LootPool> localPools = (List<LootPool>) pools.get(table);

                            if (localPools.size() > index) {
                                LootPool pool = localPools.get(index);

                                Field entries = pool.getClass().getDeclaredField("entries");
                                entries.setAccessible(true);
                                LootPoolEntryContainer[] localEntries = (LootPoolEntryContainer[]) entries.get(pool);

                                List<LootPoolEntryContainer> modifiable = new ArrayList<>(Arrays.asList(localEntries));
                                modifiable.addAll(content);
                                LootPoolEntryContainer[] modified = modifiable.toArray(new LootPoolEntryContainer[0]);

                                entries.set(pool, modified);
                                return true;
                            }
                        } catch (Throwable t) {
                            Platform.LOGGER.error("Failed to add content to loot pool at index {}: {}", index, t.getMessage(), t);
                        }

                        return false;
                    }
                },
                true
            );
        });
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLootTableModify(LootTableLoadEvent event) {
        MODIFICATIONS.forEach(consumer -> consumer.accept(event));
    }
}