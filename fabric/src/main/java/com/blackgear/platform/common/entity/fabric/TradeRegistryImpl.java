package com.blackgear.platform.common.entity.fabric;

import com.blackgear.platform.common.entity.TradeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.Collections;

public class TradeRegistryImpl {
    
    // ========== Villager Trades ==========
    
    public static void addVillagerTrades(VillagerProfession profession, TradeRegistry.VillagerLevel level, VillagerTrades.ItemListing... trades) {
        TradeOfferHelper.registerVillagerOffers(profession, level.getValue(), tradeList -> Collections.addAll(tradeList, trades));
    }
    
    // ========== Wandering Trader Trades ==========
    
    public static void addWanderingTraderTrades(boolean rare, VillagerTrades.ItemListing... trades) {
        TradeOfferHelper.registerWanderingTraderOffers(rare ? 2 : 1, tradeList -> Collections.addAll(tradeList, trades));
    }
}