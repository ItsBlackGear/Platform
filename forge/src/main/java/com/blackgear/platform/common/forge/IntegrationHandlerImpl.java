package com.blackgear.platform.common.forge;

import com.blackgear.platform.common.IntegrationHandler;
import com.blackgear.platform.Platform;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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

    public static void addInteraction(IntegrationHandler.Interaction interaction) {
        BLOCK_INTERACTIONS.add(event -> {
            InteractionResult result = interaction.of(new UseOnContext(event.getEntity(), event.getHand(), event.getHitVec()));
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
}