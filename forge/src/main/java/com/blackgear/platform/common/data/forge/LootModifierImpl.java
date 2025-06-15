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
                        List<LootPool> pools = ((LootTableAccess) event.getTable()).getPools();
                        pools.add(pool.build());
                    }

                    @Override
                    public boolean addToPool(int index, ArrayList<LootPoolEntryContainer> content) {
                        LootTable table = event.getTable();

                        try {
                            List<LootPool> pools = ((LootTableAccess) table).getPools();

                            if (pools.size() > index) {
                                LootPool pool = pools.get(index);
                                LootPoolEntryContainer[] entries = ((LootPoolAccess) pool).getEntries();

                                List<LootPoolEntryContainer> modifiable = new ArrayList<>(Arrays.asList(entries));
                                modifiable.addAll(content);
                                LootPoolEntryContainer[] modified = modifiable.toArray(new LootPoolEntryContainer[0]);

                                ((LootPoolAccess) pool).setEntries(modified);
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