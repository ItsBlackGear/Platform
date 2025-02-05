package com.blackgear.platform.core.helper;

import com.blackgear.platform.core.CoreRegistry;
import com.blackgear.platform.core.mixin.access.SimpleParticleTypeAccessor;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.function.Function;
import java.util.function.Supplier;

public class ParticleRegistry {
    public final CoreRegistry<ParticleType<?>> particles;

    private ParticleRegistry(String modId) {
        this.particles = CoreRegistry.create(Registry.PARTICLE_TYPE, modId);
    }

    public static ParticleRegistry create(String modId) {
        return new ParticleRegistry(modId);
    }

    public Supplier<SimpleParticleType> register(String name) {
        return register(name, false);
    }

    public Supplier<SimpleParticleType> register(String name, boolean overrideLimiter) {
        return this.particles.register(name, () -> SimpleParticleTypeAccessor.createSimpleParticleType(overrideLimiter));
    }

    public <T extends ParticleOptions> Supplier<ParticleType<T>> register(
        String name,
        boolean overrideLimiter,
        ParticleOptions.Deserializer<T> deserializer,
        Function<ParticleType<T>, Codec<T>> factory
    ) {
        return this.particles.register(name, () -> new ParticleType<T>(overrideLimiter, deserializer) {
            @Override public Codec<T> codec() {
                return factory.apply(this);
            }
        });
    }

    public void register() {
        this.particles.register();
    }
}