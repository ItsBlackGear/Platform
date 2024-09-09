package com.blackgear.platform.common.worldgen.decorator;

import com.blackgear.platform.common.registry.PlatformDecorators;
import com.blackgear.platform.common.worldgen.CaveSurface;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.ConfiguredDecorator;

public class CaveSurfaceConfiguration implements DecoratorConfiguration {
    public static final Codec<CaveSurfaceConfiguration> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
            CaveSurface.CODEC.fieldOf("surface").forGetter(config -> config.surface),
            Codec.INT.fieldOf("floor_to_ceiling_search_range").forGetter(config -> config.floorToCeilingSearchRange)
        ).apply(instance, CaveSurfaceConfiguration::new);
    });
    public final CaveSurface surface;
    public final int floorToCeilingSearchRange;
    
    public CaveSurfaceConfiguration(CaveSurface surface, int floorToCeilingSearchRange) {
        this.surface = surface;
        this.floorToCeilingSearchRange = floorToCeilingSearchRange;
    }
    
    public static ConfiguredDecorator<?> of(CaveSurface surface, int floorToCeilingSearchRange) {
        return PlatformDecorators.CAVE_SURFACE.get().configured(new CaveSurfaceConfiguration(surface, floorToCeilingSearchRange));
    }
    
    public static ConfiguredDecorator<?> of(CaveSurface surface) {
        return of(surface, 12);
    }
}