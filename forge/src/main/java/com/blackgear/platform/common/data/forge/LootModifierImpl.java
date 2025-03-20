package com.blackgear.platform.common.data.forge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.data.LootModifier;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(
    modid = Platform.MOD_ID
)
public class LootModifierImpl {
    private static final Set<Consumer<LootTableLoadEvent>> MODIFICATIONS = ConcurrentHashMap.newKeySet();
    
    public static void modify(LootModifier.LootTableModifier modifier) {
        MODIFICATIONS.add(event -> {
            modifier.modify(
                null,
                event.getName(),
                pool -> event.getTable().addPool(pool),
                true
            );
        });
    }
    
    @SubscribeEvent
    public static void onLootTableModify(LootTableLoadEvent event) {
        MODIFICATIONS.forEach(consumer -> consumer.accept(event));
    }
}