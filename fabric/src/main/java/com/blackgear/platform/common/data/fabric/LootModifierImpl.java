package com.blackgear.platform.common.data.fabric;

import com.blackgear.platform.common.data.LootModifier;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

public class LootModifierImpl {
    public static void modify(LootModifier.LootTableModifier modifier) {
        LootTableEvents.MODIFY.register((key, builder, source, provider) -> {
            modifier.modify(
                key,
                builder::withPool,
                source.isBuiltin()
            );
        });
    }
}