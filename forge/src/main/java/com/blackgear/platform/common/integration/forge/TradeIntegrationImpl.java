package com.blackgear.platform.common.integration.forge;

import com.blackgear.platform.common.integration.VillagerLevel;
import com.blackgear.platform.common.integration.TradeIntegration;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.List;
import java.util.function.Consumer;

public class TradeIntegrationImpl {
    public static void registerVillagerTrades(Consumer<TradeIntegration.Event> listener) {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        TradeIntegration.Event integration = new TradeIntegration.Event() {
            @Override
            public void registerTrade(VillagerProfession profession, VillagerLevel level, VillagerTrades.ItemListing... trades) {
                bus.addListener((VillagerTradesEvent event) -> {
                    if (event.getType() == profession) {
                        event.getTrades().computeIfAbsent(level.getValue(), trade -> NonNullList.create()).addAll(List.of(trades));
                    }
                });
            }

            @Override
            public void registerWandererTrade(boolean rare, VillagerTrades.ItemListing... trades) {
                bus.addListener((WandererTradesEvent event) -> {
                    if (rare) {
                        event.getRareTrades().addAll(List.of(trades));
                    } else {
                        event.getGenericTrades().addAll(List.of(trades));
                    }
                });
            }
        };

        listener.accept(integration);
    }
}