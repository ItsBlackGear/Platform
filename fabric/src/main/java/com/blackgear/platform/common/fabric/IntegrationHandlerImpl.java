package com.blackgear.platform.common.fabric;

import com.blackgear.platform.common.IntegrationHandler;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.item.context.UseOnContext;

public class IntegrationHandlerImpl {
    public static void addInteraction(IntegrationHandler.Interaction interaction) {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> interaction.of(new UseOnContext(player, hand, hitResult)));
    }
}