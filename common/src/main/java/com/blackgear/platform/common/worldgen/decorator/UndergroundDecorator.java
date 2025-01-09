package com.blackgear.platform.common.worldgen.decorator;

import com.blackgear.platform.core.mixin.access.DecorationContextAccessor;
import com.blackgear.platform.core.util.WorldGenUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

import java.util.Random;
import java.util.stream.Stream;

public class UndergroundDecorator extends FeatureDecorator<NoneDecoratorConfiguration> {
    public UndergroundDecorator(Codec<NoneDecoratorConfiguration> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecorationContext helper, Random random, NoneDecoratorConfiguration config, BlockPos pos) {
        int height = WorldGenUtils.getUndergroundGenerationHeight(
            ((DecorationContextAccessor) helper).getLevel(),
            Heightmap.Types.WORLD_SURFACE_WG,
            pos.getX(),
            pos.getZ()
        );

        if (pos.getY() <= height) {
            return Stream.of(pos);
        }

        return Stream.empty();
    }
}