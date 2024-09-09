package com.blackgear.platform.common.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.Random;

public class GrowingPlantFeature extends Feature<GrowingPlantConfiguration> {
    public GrowingPlantFeature(Codec<GrowingPlantConfiguration> codec) {
        super(codec);
    }
    
    @Override
    public boolean place(
        WorldGenLevel level,
        ChunkGenerator generator,
        Random random,
        BlockPos origin,
        GrowingPlantConfiguration config
    ) {
        int height = config.heightDistribution.getOne(random).sample(random);
        BlockPos.MutableBlockPos mutable = origin.mutable();
        BlockPos.MutableBlockPos offset = mutable.mutable().move(config.direction);
        BlockState state = level.getBlockState(mutable);
        
        for (int i = 1; i <= height; i++) {
            BlockState local = state;
            state = level.getBlockState(offset);
            
            if (local.isAir() || config.allowWater && local.getFluidState().is(FluidTags.WATER)) {
                if (i == height || !state.isAir()) {
                    level.setBlock(mutable, config.headProvider.getState(random, mutable), 2);
                    break;
                } else {
                    level.setBlock(mutable, config.bodyProvider.getState(random, mutable), 2);
                }
            }
            
            offset.move(config.direction);
            mutable.move(config.direction);
        }
        
        return true;
    }
}