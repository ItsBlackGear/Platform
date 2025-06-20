package com.blackgear.platform.common.integration.neoforge;

import com.blackgear.platform.common.integration.BlockIntegration;
import com.blackgear.platform.common.integration.BlockInteraction;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;

import java.util.function.Consumer;

public class BlockIntegrationImpl {
    public static final Object2FloatMap<ItemLike> COMPOSTABLES = new Object2FloatOpenHashMap<>();

    public static void registerIntegrations(Consumer<BlockIntegration.Event> listener) {
        BlockIntegration.Event integration = new BlockIntegration.Event() {
            @Override
            public void registerBlockInteraction(BlockInteraction interaction) {
                ModLoadingContext.get().getActiveContainer().getEventBus().addListener((PlayerInteractEvent.RightClickBlock event) -> {
                    InteractionResult result = interaction.onUse(new UseOnContext(event.getEntity(), event.getHand(), event.getHitVec()));
                    if (result != InteractionResult.PASS) {
                        event.setCanceled(true);
                        event.setCancellationResult(result);
                    }
                });
            }

            @Override
            public void registerFuelItem(ItemLike item, int burnTime) {
                NeoForge.EVENT_BUS.addListener((FurnaceFuelBurnTimeEvent event) -> {
                    if (event.getItemStack().is(item.asItem())) {
                        event.setBurnTime(burnTime);
                    }
                });
            }

            @Override
            public void registerCompostableItem(ItemLike item, float chance) {
                COMPOSTABLES.putIfAbsent(item.asItem(), chance);
            }
        };

        listener.accept(integration);
    }
}