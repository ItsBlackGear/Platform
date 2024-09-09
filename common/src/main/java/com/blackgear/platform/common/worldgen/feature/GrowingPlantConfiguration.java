package com.blackgear.platform.common.worldgen.feature;

import com.blackgear.platform.common.providers.math.IntProvider;
import com.blackgear.platform.core.util.DirectionUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.behavior.WeightedList;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class GrowingPlantConfiguration implements FeatureConfiguration {
    public static final Codec<GrowingPlantConfiguration> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            WeightedList.codec(IntProvider.CODEC).fieldOf("height_distribution").forGetter(config -> config.heightDistribution),
            DirectionUtils.CODEC.fieldOf("direction").forGetter(config -> config.direction),
            BlockStateProvider.CODEC.fieldOf("body_provider").forGetter(config -> config.bodyProvider),
            BlockStateProvider.CODEC.fieldOf("head_provider").forGetter(config -> config.headProvider),
            Codec.BOOL.fieldOf("allow_water").forGetter(config -> config.allowWater)
        ).apply(instance, GrowingPlantConfiguration::new)
    );
    
    public final WeightedList<IntProvider> heightDistribution;
    public final Direction direction;
    public final BlockStateProvider bodyProvider;
    public final BlockStateProvider headProvider;
    public final boolean allowWater;
    
    public GrowingPlantConfiguration(
        WeightedList<IntProvider> heightDistribution,
        Direction direction,
        BlockStateProvider bodyProvider,
        BlockStateProvider headProvider,
        boolean allowWater
    ) {
        this.heightDistribution = heightDistribution;
        this.direction = direction;
        this.bodyProvider = bodyProvider;
        this.headProvider = headProvider;
        this.allowWater = allowWater;
    }
}