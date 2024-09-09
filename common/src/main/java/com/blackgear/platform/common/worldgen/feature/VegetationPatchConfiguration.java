package com.blackgear.platform.common.worldgen.feature;

import com.blackgear.platform.common.providers.math.IntProvider;
import com.blackgear.platform.common.worldgen.CaveSurface;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.function.Supplier;

public class VegetationPatchConfiguration implements FeatureConfiguration {
    public final ResourceLocation replaceable;
    public final BlockStateProvider groundState;
    public final Supplier<ConfiguredFeature<?, ?>> vegetationFeature;
    public final CaveSurface surface;
    public final IntProvider depth;
    public final float extraBottomBlockChance;
    public final int verticalRange;
    public final float vegetationChance;
    public final IntProvider xzRadius;
    public final float extraEdgeColumnChance;
    
    public static final Codec<VegetationPatchConfiguration> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
            ResourceLocation.CODEC.fieldOf("replaceable").forGetter(config -> config.replaceable),
            BlockStateProvider.CODEC.fieldOf("ground_state").forGetter(config -> config.groundState),
            ConfiguredFeature.CODEC.fieldOf("vegetation_feature").forGetter(config -> config.vegetationFeature),
            CaveSurface.CODEC.fieldOf("surface").forGetter(config -> config.surface),
            IntProvider.createCodec(1, 128).fieldOf("depth").forGetter(config -> config.depth),
            Codec.floatRange(0.0F, 1.0F).fieldOf("extra_bottom_block_chance").forGetter(config -> config.extraBottomBlockChance),
            Codec.intRange(1, 256).fieldOf("vertical_range").forGetter(config -> config.verticalRange),
            Codec.floatRange(0.0F, 1.0F).fieldOf("vegetation_chance").forGetter(config -> config.vegetationChance),
            IntProvider.CODEC.fieldOf("xz_radius").forGetter(config -> config.xzRadius),
            Codec.floatRange(0.0F, 1.0F).fieldOf("extra_edge_column_chance").forGetter(config -> config.extraEdgeColumnChance)
        ).apply(instance, VegetationPatchConfiguration::new);
    });
    
    public VegetationPatchConfiguration(
        Tag.Named<?> replaceable,
        BlockStateProvider groundState,
        Supplier<ConfiguredFeature<?, ?>> vegetationFeature,
        CaveSurface surface,
        IntProvider depth,
        float extraBottomBlockChance,
        int verticalRange,
        float vegetationChance,
        IntProvider xzRadius,
        float extraEdgeColumnChance
    ) {
        this(
            replaceable.getName(),
            groundState,
            vegetationFeature,
            surface,
            depth,
            extraBottomBlockChance,
            verticalRange,
            vegetationChance,
            xzRadius,
            extraEdgeColumnChance
        );
    }
    
    public VegetationPatchConfiguration(
        ResourceLocation replaceable,
        BlockStateProvider groundState,
        Supplier<ConfiguredFeature<?, ?>> vegetationFeature,
        CaveSurface surface,
        IntProvider depth,
        float extraBottomBlockChance,
        int verticalRange,
        float vegetationChance,
        IntProvider xzRadius,
        float extraEdgeColumnChance
    ) {
        this.replaceable = replaceable;
        this.groundState = groundState;
        this.vegetationFeature = vegetationFeature;
        this.surface = surface;
        this.depth = depth;
        this.extraBottomBlockChance = extraBottomBlockChance;
        this.verticalRange = verticalRange;
        this.vegetationChance = vegetationChance;
        this.xzRadius = xzRadius;
        this.extraEdgeColumnChance = extraEdgeColumnChance;
    }
}