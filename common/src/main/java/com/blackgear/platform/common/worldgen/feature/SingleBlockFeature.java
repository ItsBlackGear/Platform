package com.blackgear.platform.common.worldgen.feature;

import com.blackgear.platform.core.util.BlockUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.Random;

public class SingleBlockFeature extends Feature<SingleBlockConfiguration> {
    public SingleBlockFeature(Codec<SingleBlockConfiguration> codec) {
        super(codec);
    }
    
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, Random random, BlockPos origin, SingleBlockConfiguration config) {
        BlockState state = config.toPlace.getState(random, origin);
        
        if (state.canSurvive(level, origin)) {
            if (state.getBlock() instanceof DoublePlantBlock) {
                if (!level.isEmptyBlock(origin.above())) {
                    return false;
                }
                
                this.placeDoublePlantAt(level, state, origin);
            } else {
                level.setBlock(origin, state, 2);
            }
            
            return true;
        }
        
        return false;
    }
    
    private void placeDoublePlantAt(LevelAccessor level, BlockState state, BlockPos pos) {
        level.setBlock(pos, BlockUtils.copyWaterloggedFrom(level, pos, state.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)), 2);
        level.setBlock(pos.above(), BlockUtils.copyWaterloggedFrom(level, pos.above(), state.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER)), 2);
    }
}