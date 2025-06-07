package com.blackgear.platform.common.data.neoforge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.data.LootModifier;
import com.blackgear.platform.core.mixin.neoforge.access.LootTableAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;

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
                pool -> ((LootTableAccessor) event.getTable()).getPools().add(pool.build()),
                true
            );
        });
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLootTableModify(LootTableLoadEvent event) {
        MODIFICATIONS.forEach(consumer -> consumer.accept(event));
    }
}