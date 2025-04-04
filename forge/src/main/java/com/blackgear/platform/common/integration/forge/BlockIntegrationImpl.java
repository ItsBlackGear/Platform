package com.blackgear.platform.common.integration.forge;

import com.blackgear.platform.common.integration.Interaction;
import com.blackgear.platform.common.integration.BlockIntegration;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Consumer;

public class BlockIntegrationImpl {
    public static void registerIntegrations(Consumer<BlockIntegration.Event> listener) {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BlockIntegration.Event integration = new BlockIntegration.Event() {
            @Override
            public void registerBlockInteraction(Interaction interaction) {
                bus.addListener((PlayerInteractEvent.RightClickBlock event) -> {
                    InteractionResult result = interaction.onUse(new UseOnContext(event.getEntity(), event.getHand(), event.getHitVec()));
                    if (result != InteractionResult.PASS) {
                        event.setCanceled(true);
                        event.setCancellationResult(result);
                    }
                });
            }

            @Override
            public void registerFuelItem(ItemLike item, int burnTime) {
                bus.addListener((FurnaceFuelBurnTimeEvent event) -> {
                    if (event.getItemStack().is(item.asItem())) {
                        event.setBurnTime(burnTime);
                    }
                });
            }
        };

        listener.accept(integration);
    }
}