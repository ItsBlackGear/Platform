package com.blackgear.platform.core.mixin.common;

import com.blackgear.platform.common.worldgen.height.HeightHolder;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = DecorationContext.class, priority = 998) // Lowered priority so it can be overridden
public abstract class DecorationContextMixin implements HeightHolder {}