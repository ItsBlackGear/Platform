package com.blackgear.platform.common.worldgen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CountDecorator extends SimpleFeatureDecorator<CountConfiguration> {
    public CountDecorator(Codec<CountConfiguration> codec) {
        super(codec);
    }
    
    @Override
    protected Stream<BlockPos> place(Random random, CountConfiguration config, BlockPos pos) {
        return IntStream.range(0, config.count.sample(random)).mapToObj(value -> pos);
    }
}