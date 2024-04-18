package com.blackgear.platform.common.forge;

import com.blackgear.platform.common.IntegrationHandler;
import com.blackgear.platform.Platform;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(
    modid = Platform.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class IntegrationHandlerImpl {
    private static final Set<Consumer<PlayerInteractEvent.RightClickBlock>> BLOCK_INTERACTIONS = ConcurrentHashMap.newKeySet();
    private static final Set<Consumer<FurnaceFuelBurnTimeEvent>> FUEL_ENTRIES = ConcurrentHashMap.newKeySet();

    // ========== Block Interactions ==========

    public static void addInteraction(IntegrationHandler.Interaction interaction) {
        BLOCK_INTERACTIONS.add(event -> {
            InteractionResult result = interaction.of(new UseOnContext((Player) event.getEntity(), event.getHand(), event.getHitVec()));
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
                event.setCancellationResult(result);
            }
        });
    }

    @SubscribeEvent
    public static void registerBlockInteraction(PlayerInteractEvent.RightClickBlock event) {
        BLOCK_INTERACTIONS.forEach(consumer -> consumer.accept(event));
    }

    // ========== Fuel Registry ==========

    public static void addFuel(ItemLike item, int burnTime) {
        FUEL_ENTRIES.add(event -> {
            if (event.getItemStack().getItem() == item.asItem()) {
                event.setBurnTime(burnTime);
            }
        });
    }

    @SubscribeEvent
    public static void registerFuel(FurnaceFuelBurnTimeEvent event) {
        FUEL_ENTRIES.forEach(consumer -> consumer.accept(event));
    }
}