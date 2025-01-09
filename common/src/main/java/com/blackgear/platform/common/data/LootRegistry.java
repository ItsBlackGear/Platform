package com.blackgear.platform.common.data;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;

import java.util.function.Function;

/**
 * Utility class to apply modifications to Loot Tables
 *
 * @author ItsBlackGear
 */
public class LootRegistry {
    /**
     * Modify an existing loot table.
     *
     * <p>Example of modifying a vanilla loot table:</p>
     *
     * <pre>{@code
     *
     * LootRegistry.modify((lootTables, path, adder, setter) -> {
     *     if (path.equals(BuiltInLootTables.SIMPLE_DUNGEON)) {
     *         LootPool.Builder pool = LootPool.lootPool()
     *             .setRolls(UniformGenerator.between(1.0F, 3.0F))
     *             .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(15));
     *         adder.addPool(pool);
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
        void modify(LootTables lootTables, LootTable table, ResourceLocation path, Adder adder, Setter setter);
    }

    public interface Adder {
        void addPool(LootPool pool);

        default void addPool(LootPool.Builder pool) {
            this.addPool(pool.build());
        }
    }
    
    public interface Setter {
        void setTable(LootTable lootTable);
        
        void setBlock(Block block, Function<Block, LootTable.Builder> factory);
    }
}