package com.blackgear.platform.common.data;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Utility class to apply modifications to Loot Tables
 *
 * @author ItsBlackGear
 */
public class LootModifier {
    /**
     * Modify an existing loot table.
     *
     * <p>Example of modifying a vanilla loot table:</p>
     *
     * <pre>{@code
     *
     * LootRegistry.modify((lootTables, path, context, builtin) -> {
     *     if (path.equals(BuiltInLootTables.SIMPLE_DUNGEON)) {
     *         LootPool.Builder pool = LootPool.lootPool()
     *             .setRolls(UniformGenerator.between(1.0F, 3.0F))
     *             .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(15));
     *         context.addPool(pool);
     *     }
     * });
     *
     * }</pre>
     *
     * In the example we're currently adding a diamond to the loot table of a simple dungeon.
     */
    @ExpectPlatform
    public static void modify(LootTableModifier modifier) {
        throw new AssertionError();
    }
    
    public interface LootTableModifier {
        void modify(ResourceKey<LootTable> key, LootTableContext context, boolean builtin);
    }
    
    public interface LootTableContext {
        void addPool(LootPool.Builder pool);

        boolean addToPool(int index, ArrayList<LootPoolEntryContainer> content);

        default boolean addToPool(ArrayList<LootPoolEntryContainer> content) {
            return this.addToPool(0, content);
        }

        default boolean addToPool(int index, LootPoolEntryContainer... content) {
            ArrayList<LootPoolEntryContainer> entries = new ArrayList<>();
            Collections.addAll(entries, content);
            return this.addToPool(index, entries);
        }

        default boolean addToPool(LootPoolEntryContainer... content) {
            return this.addToPool(0, content);
        }
    }
}