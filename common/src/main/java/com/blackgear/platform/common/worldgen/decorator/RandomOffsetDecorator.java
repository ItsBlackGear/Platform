package com.blackgear.platform.common.worldgen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

import java.util.Random;
import java.util.stream.Stream;

public class RandomOffsetDecorator extends FeatureDecorator<RandomOffsetConfiguration> {
    public RandomOffsetDecorator(Codec<RandomOffsetConfiguration> codec) {
        super(codec);
    }
    
    @Override
    public Stream<BlockPos> getPositions(DecorationContext context, Random random, RandomOffsetConfiguration config, BlockPos pos) {
        int x = pos.getX() + config.xzSpread.sample(random);
        int y = pos.getY() + config.ySpread.sample(random);
        int z = pos.getZ() + config.xzSpread.sample(random);
        return Stream.of(new BlockPos(x, y, z));
    }
}