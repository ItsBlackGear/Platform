package com.blackgear.platform.common.data.forge;

import com.blackgear.platform.common.data.LootModifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Consumer;
import java.util.function.Function;

public class LootModifierImpl {
    public static void modify(LootModifier.LootTableModifier modifier) {
        Consumer<LootTableLoadEvent> listener = event -> {
            modifier.modify(
                event.getLootTableManager(),
                event.getName(),
                pool -> event.getTable().addPool(pool),
                new LootModifier.LootSetter() {
                    @Override
                    public void set(LootTable table) {
                        event.setTable(table);
                    }

                    @Override
                    public void setBlock(Block block, Function<Block, LootTable.Builder> factory) {
                        if (event.getName().equals(block.getLootTable())) {
                            this.set(factory.apply(block).build());
                        }
                    }
                }
            );
        };
        FMLJavaModLoadingContext.get().getModEventBus().addListener(listener);
    }
}