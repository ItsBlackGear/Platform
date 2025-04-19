package com.blackgear.platform.common.integration;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.function.Consumer;

public class TradeIntegration {
    @ExpectPlatform
    public static void registerVillagerTrades(Consumer<Event> listener) {
        throw new AssertionError();
    }

    public interface Event {
        void registerTrade(VillagerProfession profession, VillagerLevel level, VillagerTrades.ItemListing... trades);

        void registerWandererTrade(boolean rare, VillagerTrades.ItemListing... trades);
    }
}