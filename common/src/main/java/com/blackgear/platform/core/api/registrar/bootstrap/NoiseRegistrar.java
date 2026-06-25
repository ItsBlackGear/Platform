package com.blackgear.platform.core.api.registrar.bootstrap;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseRegistrar extends BootstrapRegistrar<NormalNoise.NoiseParameters> {
    private NoiseRegistrar(ResourceKey<? extends Registry<NormalNoise.NoiseParameters>> registry, String modId) {
        super(registry, modId);
    }

    public static NoiseRegistrar create(String modId) {
        return new NoiseRegistrar(Registries.NOISE, modId);
    }

    public ResourceKey<NormalNoise.NoiseParameters> register(
        String name,
        int firstOctave,
        double firstAmplitude,
        double... amplitudes
    ) {
        return this.register(name, context -> new NormalNoise.NoiseParameters(firstOctave, firstAmplitude, amplitudes));
    }
}