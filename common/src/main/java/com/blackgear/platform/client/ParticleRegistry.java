package com.blackgear.platform.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * The ParticleRegistry class is a utility class designed to simplify the process of registering particles to particle providers.
 * It provides a unified interface for both Forge and Fabric modding platforms.
 *
 *  @author ItsBlackGear
 **/
public class ParticleRegistry {
    /**
     * Registers a particle provider with the provided particle type.
     *
     * @param type     A supplier that provides the particle type to be registered.
     * @param provider The particle provider associated with the particle type.
     * @param <T>      The type of particle options.
     * @param <P>      The type of particle type.
     *
     * @see net.minecraft.client.particle.ParticleEngine#register(ParticleType, ParticleProvider)
     **/
    @ExpectPlatform
    public static <T extends ParticleOptions, P extends ParticleType<T>> void create(Supplier<P> type, ParticleProvider<T> provider) {
        throw new AssertionError();
    }

    /**
     * Registers a particle provider with the provided particle type.
     *
     * @param type    A supplier that provides the particle type to be registered.
     * @param factory The factory that creates a particle provider from a sprite set.
     * @param <T>     The type of particle options.
     * @param <P>     The type of particle type.
     *
     * @see net.minecraft.client.particle.ParticleEngine#register(ParticleType, ParticleEngine.SpriteParticleRegistration)
     **/
    @ExpectPlatform
    public static <T extends ParticleOptions, P extends ParticleType<T>> void create(Supplier<P> type, Factory<T> factory) {
        throw new AssertionError();
    }

    public interface Factory<T extends ParticleOptions> {
        @NotNull ParticleProvider<T> create(SpriteSet sprites);
    }
}