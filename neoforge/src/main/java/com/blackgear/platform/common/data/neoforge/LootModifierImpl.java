package com.blackgear.platform.common.data.neoforge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.data.LootModifier;
import com.blackgear.platform.core.mixin.neoforge.access.LootTableAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@EventBusSubscriber(modid = Platform.MOD_ID)
public class LootModifierImpl {
    private static final Set<Consumer<LootTableLoadEvent>> MODIFICATIONS = ConcurrentHashMap.newKeySet();
    
    public static void modify(LootModifier.LootTableModifier modifier) {
        MODIFICATIONS.add(event -> {
            modifier.modify(
                ResourceKey.create(Registries.LOOT_TABLE, event.getName()),
                new LootModifier.LootTableContext() {
                    @Override
                    public void addPool(LootPool.Builder pool) {
                        ((LootTableAccessor) event.getTable()).getPools().add(pool.build());
                    }

                    @Override
                    public boolean addToPool(int index, ArrayList<LootPoolEntryContainer> content) {
                        LootTable table = event.getTable();

                        try {
                            List<LootPool> pools = ((LootTableAccessor) table).getPools();

                            if (pools.size() > index) {
                                LootPool pool = pools.get(index);
                                Field field = LootPool.class.getDeclaredField("entries");
                                field.setAccessible(true);

                                List<LootPoolEntryContainer> entries = (List<LootPoolEntryContainer>) field.get(pool);
                                List<LootPoolEntryContainer> additions = new ArrayList<>(entries);
                                additions.addAll(content);

                                field.set(pool, additions);
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