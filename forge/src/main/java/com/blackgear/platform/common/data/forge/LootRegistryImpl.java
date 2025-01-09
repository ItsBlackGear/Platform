package com.blackgear.platform.common.data.forge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.data.LootRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

@Mod.EventBusSubscriber(
    modid = Platform.MOD_ID
)
public class LootRegistryImpl {
    private static final Set<Consumer<LootTableLoadEvent>> MODIFICATIONS = ConcurrentHashMap.newKeySet();

    public static void modify(LootRegistry.LootTableModifier modifier) {
        MODIFICATIONS.add(event -> {
            modifier.modify(
                event.getLootTableManager(),
                event.getTable(),
                event.getName(),
                pool -> event.getTable().addPool(pool),
                new LootRegistry.Setter() {
                    @Override
                    public void setTable(LootTable lootTable) {
                        event.setTable(lootTable);
                    }
                    
                    @Override
                    public void setBlock(Block block, Function<Block, LootTable.Builder> factory) {
                        if (event.getName().equals(block.getLootTable())) {
                            this.setTable(factory.apply(block).build());
                        }
                    }
                }
            );
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLootTableModify(LootTableLoadEvent event) {
        MODIFICATIONS.forEach(consumer -> consumer.accept(event));
    }
}