package com.blackgear.platform.common.data;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;

import java.util.function.Function;

public class LootModifier {
    @ExpectPlatform
    public static void modify(LootTableModifier modifier) {
        throw new AssertionError();
    }

    public interface LootTableModifier {
        void modify(LootTables manager, ResourceLocation path, LootTableContext context, LootSetter setter);
    }

    public interface LootSetter {
        void set(LootTable context);

        void setBlock(Block block, Function<Block, LootTable.Builder> factory);
    }

    public interface LootTableContext {
        void addPool(LootPool pool);

        default void addPool(LootPool.Builder pool) {
            this.addPool(pool.build());
        }
    }
}