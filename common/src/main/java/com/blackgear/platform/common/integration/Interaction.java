package com.blackgear.platform.common.integration;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

@FunctionalInterface
public interface Interaction {
    InteractionResult of(UseOnContext context);
}