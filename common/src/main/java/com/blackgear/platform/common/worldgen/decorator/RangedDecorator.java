package com.blackgear.platform.common.worldgen.decorator;

import com.blackgear.platform.common.worldgen.WorldGenerationContext;
import com.blackgear.platform.common.worldgen.height.HeightHolder;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

import java.util.Random;
import java.util.stream.Stream;

public class RangedDecorator extends FeatureDecorator<RangedConfiguration> {
    public RangedDecorator(Codec<RangedConfiguration> codec) {
        super(codec);
    }
    
    @Override
    public Stream<BlockPos> getPositions(DecorationContext helper, Random random, RangedConfiguration config, BlockPos pos) {
        int y = config.height.sample(random, new WorldGenerationContext((HeightHolder) helper));
        return Stream.of(new BlockPos(pos.getX(), y, pos.getZ()));
    }
}