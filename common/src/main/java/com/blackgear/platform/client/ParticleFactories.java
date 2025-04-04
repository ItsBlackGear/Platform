package com.blackgear.platform.client;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@Deprecated(forRemoval = true)
public class ParticleFactories {
    public static <T extends ParticleOptions, P extends ParticleType<T>> void create(Supplier<P> type, ParticleProvider<T> provider) {
        GameRendering.registerParticleFactories(event -> event.register(type, provider));
    }

    public static <T extends ParticleOptions, P extends ParticleType<T>> void create(Supplier<P> type, Factory<T> factory) {
        GameRendering.registerParticleFactories(event -> event.register(type, factory::create));
    }
    
    public interface Factory<T extends ParticleOptions> {
        @NotNull ParticleProvider<T> create(SpriteSet sprites);
    }
}