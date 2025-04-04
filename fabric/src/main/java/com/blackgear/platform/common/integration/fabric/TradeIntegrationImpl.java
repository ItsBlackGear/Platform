package com.blackgear.platform.common.integration.fabric;

import com.blackgear.platform.common.integration.VillagerLevel;
import com.blackgear.platform.common.integration.TradeIntegration;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.Collections;
import java.util.function.Consumer;

public class TradeIntegrationImpl {
    public static void registerVillagerTrades(Consumer<TradeIntegration.Event> listener) {
        listener.accept(new TradeIntegration.Event() {
            @Override
            public void registerTrade(VillagerProfession profession, VillagerLevel level, VillagerTrades.ItemListing... trades) {
                TradeOfferHelper.registerVillagerOffers(profession, level.getValue(), listings -> Collections.addAll(listings, trades));
            }

            @Override
            public void registerWandererTrade(boolean rare, VillagerTrades.ItemListing... trades) {
                TradeOfferHelper.registerWanderingTraderOffers(rare ? 2 : 1, listings -> Collections.addAll(listings, trades));
            }
        });
    }
}