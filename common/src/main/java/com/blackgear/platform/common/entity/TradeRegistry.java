package com.blackgear.platform.common.entity;

import com.blackgear.platform.common.integration.TradeIntegration;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

@Deprecated(forRemoval = true)
public class TradeRegistry {
    public static void addVillagerTrades(VillagerProfession profession, VillagerLevel level, VillagerTrades.ItemListing... trades) {
        TradeIntegration.registerVillagerTrades(event -> event.registerTrade(profession, com.blackgear.platform.common.integration.VillagerLevel.valueOf(level.name()), trades));
    }

    public static void addWanderingTraderTrades(boolean rare, VillagerTrades.ItemListing... trades) {
        TradeIntegration.registerVillagerTrades(event -> event.registerWandererTrade(rare, trades));
    }

    public enum VillagerLevel {
        NOVICE(1),
        APPRENTICE(2),
        JOURNEYMAN(3),
        EXPERT(4),
        MASTER(5);

        private final int value;

        VillagerLevel(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
}