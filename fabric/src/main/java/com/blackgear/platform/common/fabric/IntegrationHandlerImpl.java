package com.blackgear.platform.common.fabric;

import com.blackgear.platform.common.IntegrationHandler;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;

public class IntegrationHandlerImpl {
    public static void setAsFuel(ItemLike item, int burnTime) {
        FuelRegistry.INSTANCE.add(item, burnTime);
    }

    public static void addInteraction(IntegrationHandler.Interaction interaction) {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> interaction.of(new UseOnContext(player, hand, hitResult)));
    }
}