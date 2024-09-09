package com.blackgear.platform.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;

public class SingleBlockConfiguration implements FeatureConfiguration {
    public static final Codec<SingleBlockConfiguration> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(BlockStateProvider.CODEC.fieldOf("to_place").forGetter(config -> config.toPlace))
            .apply(instance, SingleBlockConfiguration::new);
    });
    public final BlockStateProvider toPlace;
    
    public SingleBlockConfiguration(BlockStateProvider toPlace) {
        this.toPlace = toPlace;
    }
    
    public static SingleBlockConfiguration of(Block toPlace) {
        return SingleBlockConfiguration.of(toPlace.defaultBlockState());
    }
    
    public static SingleBlockConfiguration of(BlockState toPlace) {
        return new SingleBlockConfiguration(new SimpleStateProvider(toPlace));
    }
}