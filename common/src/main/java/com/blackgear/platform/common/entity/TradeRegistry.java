package com.blackgear.platform.common.entity;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

public class TradeRegistry {

    // ========== Villager Trades ==========

    /**
     * Adds trades to a villager profession at a specific level.
     *
     * <p>Example of adding new trades to a villager</p>
     *
     * <pre>{@code
     *
     * TradeRegistry.addVillagerTrades(
     *     VillagerProfession.FARMER,
     *     VillagerLevel.APPRENTICE,
     *     new VillagerTrades.ItemsForEmeraldsTrade(Items.WHEAT, 1, 16, 2),
     *     new VillagerTrades.ItemsForEmeraldsTrade(Items.POTATO, 1, 16, 2),
     *     new VillagerTrades.ItemsForEmeraldsTrade(Items.CARROT, 1, 16, 2)
     * )
     *
     * }</pre>
     *
     * @param profession The profession of the villager that will take the trade.
     * @param level The level of expertise of the villager that takes the trade.
     * @param trades The list of trades that the villager will take.
     */
    @ExpectPlatform
    public static void addVillagerTrades(VillagerProfession profession, VillagerLevel level, VillagerTrades.ItemListing... trades) {
        throw new AssertionError();
    }

    // ========== Wandering Trader Trades ==========

    /**
     * Adds trades to the wandering trader.
     *
     * <p>Example of adding new trades to the wandering trader</p>
     *
     * <pre>{@code
     *
     * TradeRegistry.addWanderingTraderTrades(
     *    false,
     *    new VillagerTrades.ItemsForEmeraldsTrade(Items.WHEAT, 1, 16, 2),
     *    new VillagerTrades.ItemsForEmeraldsTrade(Items.POTATO, 1, 16, 2),
     *    new VillagerTrades.ItemsForEmeraldsTrade(Items.CARROT, 1, 16, 2)
     * )
     *
     * }</pre>
     * @param rare Sets the rarity of the trades.
     * @param trades The list of trades that the wandering trader will take.
     */
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