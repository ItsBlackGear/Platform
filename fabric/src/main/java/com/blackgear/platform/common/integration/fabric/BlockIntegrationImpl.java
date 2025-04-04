package com.blackgear.platform.common.integration.fabric;

import com.blackgear.platform.common.integration.Interaction;
import com.blackgear.platform.common.integration.BlockIntegration;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class BlockIntegrationImpl {
    public static void registerIntegrations(Consumer<BlockIntegration.Event> listener) {
        listener.accept(new BlockIntegration.Event() {
            @Override
            public void registerBlockInteraction(Interaction interaction) {
                UseBlockCallback.EVENT.register((player, level, hand, hit) -> interaction.onUse(new UseOnContext(player, hand, hit)));
            }

            @Override
            public void registerFuelItem(ItemLike item, int burnTime) {
                FuelRegistry.INSTANCE.add(item, burnTime);
            }
        });
    }
}