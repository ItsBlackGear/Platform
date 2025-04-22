package com.blackgear.platform.core.helper;

import com.blackgear.platform.core.CoreRegistry;
import com.blackgear.platform.core.mixin.access.SimpleParticleTypeAccessor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;
import java.util.function.Supplier;

public class ParticleRegistry {
    private final CoreRegistry<ParticleType<?>> particles;

    private ParticleRegistry(String modId) {
        this.particles = CoreRegistry.create(BuiltInRegistries.PARTICLE_TYPE, modId);
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
        Function<ParticleType<T>, MapCodec<T>> deserializer,
        Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> factory
    ) {
        return this.particles.register(name, () -> new ParticleType<T>(overrideLimiter) {
            @Override
            public MapCodec<T> codec() {
                return deserializer.apply(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return factory.apply(this);
            }
        });
    }

    public void register() {
        this.particles.register();
    }
}