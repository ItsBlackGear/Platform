package com.blackgear.platform.common.entity;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

public class TradeRegistry {

    // ========== Villager Trades ==========

    @ExpectPlatform
    public static void addVillagerTrades(VillagerProfession profession, VillagerLevel level, VillagerTrades.ItemListing... trades) {
        throw new AssertionError();
    }

    // ========== Wandering Trader Trades ==========

    @ExpectPlatform
    public static void addWanderingTraderTrades(boolean rare, VillagerTrades.ItemListing... trades) {
        throw new AssertionError();
    }

    // ========== Villager Level ==========

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