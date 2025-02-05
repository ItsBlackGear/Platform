package com.blackgear.platform.core.mixin.access;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.schedule.Activity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SimpleParticleType.class)
public interface SimpleParticleTypeAccessor {
    @Invoker("<init>")
    static SimpleParticleType createSimpleParticleType(boolean overrideLimiter) {
        throw new UnsupportedOperationException();
    }
}