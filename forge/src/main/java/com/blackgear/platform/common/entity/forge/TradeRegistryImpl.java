package com.blackgear.platform.common.entity.forge;

import com.blackgear.platform.common.entity.TradeRegistry;
import com.blackgear.platform.Platform;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(
    modid = Platform.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class TradeRegistryImpl {
    private static final Map<VillagerProfession, Int2ObjectMap<List<VillagerTrades.ItemListing>>> VILLAGER_TRADES = new HashMap<>();
    private static final List<VillagerTrades.ItemListing> WANDERING_TRADER_GENERICS = new ArrayList<>();
    private static final List<VillagerTrades.ItemListing> WANDERING_TRADER_RARES = new ArrayList<>();

    // ========== Villager Trades ==========

    public static void addVillagerTrades(VillagerProfession profession, TradeRegistry.VillagerLevel level, VillagerTrades.ItemListing... trades) {
        Int2ObjectMap<List<VillagerTrades.ItemListing>> perProfession = VILLAGER_TRADES.computeIfAbsent(profession, trade -> new Int2ObjectOpenHashMap<>());
        List<VillagerTrades.ItemListing> perLevel = perProfession.computeIfAbsent(level.getValue(), trade -> new ArrayList<>());
        Collections.addAll(perLevel, trades);
    }

    @SubscribeEvent
    public static void registerVillagerTrades(VillagerTradesEvent event) {
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = VILLAGER_TRADES.get(event.getType());

        if (trades != null) {
            for (Int2ObjectMap.Entry<List<VillagerTrades.ItemListing>> entry : trades.int2ObjectEntrySet()) {
                event.getTrades().computeIfAbsent(entry.getIntKey(), level -> NonNullList.create()).addAll(entry.getValue());
            }
        }
    }

    // ========== Wandering Trader Trades ==========

    public static void addWanderingTraderTrades(boolean rare, VillagerTrades.ItemListing... trades) {
        if (rare) {
            Collections.addAll(WANDERING_TRADER_RARES, trades);
        } else {
            Collections.addAll(WANDERING_TRADER_GENERICS, trades);
        }
    }

    @SubscribeEvent
    public static void registerWanderingTraderTrades(WandererTradesEvent event) {
        if (!WANDERING_TRADER_GENERICS.isEmpty()) {
            event.getGenericTrades().addAll(WANDERING_TRADER_GENERICS);
        }

        if (!WANDERING_TRADER_RARES.isEmpty()) {
            event.getRareTrades().addAll(WANDERING_TRADER_RARES);
        }
    }
}