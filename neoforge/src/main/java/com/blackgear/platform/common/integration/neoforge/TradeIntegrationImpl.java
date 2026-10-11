package com.blackgear.platform.common.integration.neoforge;

import com.blackgear.platform.common.integration.TradeIntegration;
import com.blackgear.platform.common.integration.VillagerLevel;
import com.blackgear.platform.neoforge.CommonLoaderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class TradeIntegrationImpl {
    public static void registerVillagerTrades(Consumer<TradeIntegration.Event> listener) {
        listener.accept(ForgeTradeIntegrations.INSTANCE);
    }
    
    static class ForgeTradeIntegrations implements TradeIntegration.Event {
        public static final ForgeTradeIntegrations INSTANCE = new ForgeTradeIntegrations();
        
        @Override
        public void registerTrade(VillagerProfession profession, VillagerLevel level, VillagerTrades.ItemListing... trades) {
            CommonLoaderPipelines.VILLAGER_TRADES.add(event -> {
                if (event.getType() == profession) {
                    event.getTrades().computeIfAbsent(level.getValue(), trade -> NonNullList.create()).addAll(List.of(trades));
                }
            });
        }
        
        @Override
        public void registerWandererTrade(boolean rare, VillagerTrades.ItemListing... trades) {
            CommonLoaderPipelines.WANDERER_TRADES.add(event -> {
                if (rare) {
                    event.getRareTrades().addAll(List.of(trades));
                } else {
                    event.getGenericTrades().addAll(List.of(trades));
                }
            });
        }
    }
}