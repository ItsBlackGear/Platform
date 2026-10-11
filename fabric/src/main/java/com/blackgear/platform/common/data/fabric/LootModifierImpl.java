package com.blackgear.platform.common.data.fabric;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.data.LootModifier;
import com.blackgear.platform.core.mixin.fabric.loot.LootTableBuilderAccessor;
import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

import java.util.ArrayList;

public class LootModifierImpl {
    public static void modify(LootModifier.LootTableModifier modifier) {
        LootTableEvents.MODIFY.register((key, table, source, provider) -> {
            modifier.modify(
                key,
                new FabricLootTableContext(table, provider),
                source.isBuiltin()
            );
        });
    }
    
    private record FabricLootTableContext(LootTable.Builder table, HolderLookup.Provider provider) implements LootModifier.LootTableContext {
        @Override
        public void addPool(LootPool.Builder pool) {
            this.table.withPool(pool);
        }
        
        @Override
        public boolean addToPool(int index, ArrayList<LootPoolEntryContainer> content) {
            try {
                ImmutableList.Builder<LootPool> pools = ((LootTableBuilderAccessor) table).getPools();
                ImmutableList<LootPool> local = pools.build();
                
                if (local.size() <= index) {
                    Platform.LOGGER.error("Failed to add content to loot pool at index {}: No pools found", index);
                    return false;
                }
                
                LootPool pool = local.get(index);
                LootPool modifiedPool = ((LootPoolAccess) pool).mergeEntries(content);
                
                ImmutableList.Builder<LootPool> builder = ImmutableList.builder();
                for (int i = 0; i < local.size(); i++) {
                    builder.add(i == index ? modifiedPool : local.get(i));
                }
                
                ((LootTableBuilderAccessor) table).setPools(builder);
                return true;
            } catch (Throwable t) {
                Platform.LOGGER.error("Failed to add content to loot pool at index {}: {}", index, t.getMessage(), t);
                return false;
            }
        }
        
        @Override
        public HolderLookup.Provider registries() {
            return this.provider;
        }
    }
}