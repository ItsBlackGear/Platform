package com.blackgear.platform.common.worldgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import java.util.List;

public class OverlayOreConfiguration implements FeatureConfiguration {
    public static final Codec<OverlayOreConfiguration> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
            Codec.list(TargetBlockState.CODEC).fieldOf("targets").forGetter(config -> config.targets),
            Codec.intRange(0, 64).fieldOf("size").forGetter(config -> config.size),
            Codec.floatRange(0.0F, 1.0F).fieldOf("discard_chance_on_air_exposure").forGetter(config -> config.discardChanceOnAirExposure)
        ).apply(instance, OverlayOreConfiguration::new);
    });
    public final List<TargetBlockState> targets;
    public final int size;
    public final float discardChanceOnAirExposure;
    
    public OverlayOreConfiguration(List<TargetBlockState> targets, int size, float discardChanceOnAirExposure) {
        this.targets = targets;
        this.size = size;
        this.discardChanceOnAirExposure = discardChanceOnAirExposure;
    }
    
    public OverlayOreConfiguration(List<TargetBlockState> targets, int size) {
        this(targets, size, 0.0F);
    }
    
    public OverlayOreConfiguration(RuleTest target, BlockState state, int size, float discardChanceOnAirExposure) {
        this(ImmutableList.of(target(target, state)), size, discardChanceOnAirExposure);
    }
    
    public OverlayOreConfiguration(RuleTest target, BlockState state, int size) {
        this(target, state, size, 0.0F);
    }
    
    public static TargetBlockState target(RuleTest target, BlockState state) {
        return new TargetBlockState(target, state);
    }
    
    public static TargetBlockState target(Block target, Block block) {
        return new TargetBlockState(new BlockMatchTest(target), block.defaultBlockState());
    }
    
    public static class TargetBlockState {
        public final RuleTest target;
        public final BlockState state;
        
        public static final Codec<TargetBlockState> CODEC = RecordCodecBuilder.create(instance -> {
            return instance.group(
                RuleTest.CODEC.fieldOf("target").forGetter(config -> config.target),
                BlockState.CODEC.fieldOf("state").forGetter(config -> config.state)
            ).apply(instance, TargetBlockState::new);
        });
        
        public TargetBlockState(RuleTest target, BlockState state) {
            this.target = target;
            this.state = state;
        }
    }
}