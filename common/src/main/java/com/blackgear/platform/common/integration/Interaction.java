package com.blackgear.platform.common.integration;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

public interface Interaction {
    InteractionResult onUse(UseOnContext context);
}