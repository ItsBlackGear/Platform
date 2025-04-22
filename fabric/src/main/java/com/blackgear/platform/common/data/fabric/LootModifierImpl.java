package com.blackgear.platform.common.data.fabric;

import com.blackgear.platform.common.data.LootModifier;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.level.storage.loot.LootPool;

public class LootModifierImpl {
    public static void modify(LootModifier.LootTableModifier modifier) {
        LootTableEvents.MODIFY.register((resourceKey, builder, source, provider) -> {
            modifier.modify(
                resourceKey.location(),
                new LootModifier.LootTableContext() {
                    @Override
                    public void addPool(LootPool pool) {
                        builder.pool(pool);
                    }

                    @Override
                    public void addPool(LootPool.Builder pool) {
                        builder.withPool(pool);
                    }
                },
                source.isBuiltin()
            );
        });
    }
}