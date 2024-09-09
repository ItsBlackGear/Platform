package com.blackgear.platform.common.worldgen.feature;

import com.blackgear.platform.core.util.BlockUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.List;
import java.util.Random;

public class MultifaceGrowthFeature extends Feature<MultifaceGrowthConfiguration> {
    public MultifaceGrowthFeature(Codec<MultifaceGrowthConfiguration> codec) {
        super(codec);
    }
    
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, Random random, BlockPos origin, MultifaceGrowthConfiguration config) {
        if (!BlockUtils.isAirOrWater(level.getBlockState(origin))) {
            return false;
        } else {
            List<Direction> shuffledDirections = config.getShuffledDirections(random);
            
            if (placeGrowthIfPossible(level, origin, level.getBlockState(origin), config, random, shuffledDirections)) {
                return true;
            } else {
                BlockPos.MutableBlockPos mutable = origin.mutable();
                for (Direction direction : shuffledDirections) {
                    mutable.set(origin);
                    List<Direction> shuffledDirectionsExcept = config.getShuffledDirectionsExcept(random, direction.getOpposite());
                    
                    for (int i = 0; i < config.searchRange; i++) {
                        mutable.setWithOffset(origin, direction);
                        BlockState state = level.getBlockState(mutable);
                        
                        if (!BlockUtils.isAirOrWater(state) && !state.is(config.placeBlock)) {
                            break;
                        }
                        
                        if (this.placeGrowthIfPossible(level, mutable, state, config, random, shuffledDirectionsExcept)) {
                            return true;
                        }
                    }
                }
                
                return false;
            }
        }
    }
    
    private boolean placeGrowthIfPossible(WorldGenLevel level, BlockPos pos, BlockState state, MultifaceGrowthConfiguration config, Random random, List<Direction> directions) {
        BlockPos.MutableBlockPos mutable = pos.mutable();
        
        for (Direction direction : directions) {
            BlockState neighborState = level.getBlockState(mutable.setWithOffset(pos, direction));
            
            if (config.canBePlacedOn.contains(neighborState.getBlock())) {
                BlockState placementState = config.placeBlock.getStateForPlacement(state, level, pos, direction);
                
                if (placementState != null) {
                    level.setBlock(pos, placementState, 3);
                    level.getChunk(pos).markPosForPostprocessing(pos);
                    
                    if (random.nextFloat() < config.chanceOfSpreading) {
                        config.placeBlock.getSpreader().spreadFromFaceTowardRandomDirection(placementState, level, pos, direction, random, true);
                    }
                    
                    return true;
                }
            }
        }
        
        return false;
    }
}