package com.blackgear.platform.common.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

public class WaterloggedVegetationPatchFeature extends VegetationPatchFeature {
    public WaterloggedVegetationPatchFeature(Codec<VegetationPatchConfiguration> codec) {
        super(codec);
    }
    
    @Override
    protected Set<BlockPos> placeGroundPatch(WorldGenLevel level, VegetationPatchConfiguration config, Random random, BlockPos pos, Predicate<BlockState> replaceableBlocks, int xRadius, int zRadius) {
        Set<BlockPos> positions = super.placeGroundPatch(level, config, random, pos, replaceableBlocks, xRadius, zRadius);
        Set<BlockPos> waterloggablePositions = new HashSet<>();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        
        for (BlockPos position : positions) {
            if (!isExposed(level, positions, position, mutable)) {
                waterloggablePositions.add(position);
            }
        }
        
        for (BlockPos position : waterloggablePositions) {
            level.setBlock(position, Blocks.WATER.defaultBlockState(), 2);
        }
        
        return waterloggablePositions;
    }
    
    private static boolean isExposed(WorldGenLevel level, Set<BlockPos> positions, BlockPos pos, BlockPos.MutableBlockPos mutable) {
        return isExposedDirection(level, pos, mutable, Direction.NORTH)
            || isExposedDirection(level, pos, mutable, Direction.SOUTH)
            || isExposedDirection(level, pos, mutable, Direction.EAST)
            || isExposedDirection(level, pos, mutable, Direction.WEST)
            || isExposedDirection(level, pos, mutable, Direction.DOWN);
    }
    
    private static boolean isExposedDirection(WorldGenLevel level, BlockPos pos, BlockPos.MutableBlockPos mutable, Direction direction) {
        mutable.setWithOffset(pos, direction);
        return !level.getBlockState(mutable).isFaceSturdy(level, mutable, direction.getOpposite());
    }
    
    @Override
    protected boolean placeVegetation(WorldGenLevel level, VegetationPatchConfiguration config, ChunkGenerator generator, Random random, BlockPos pos) {
        if (super.placeVegetation(level, config, generator, random, pos.below())) {
            BlockState state = level.getBlockState(pos);
            if (state.hasProperty(BlockStateProperties.WATERLOGGED) && !state.getValue(BlockStateProperties.WATERLOGGED)) {
                level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, true), 2);
            }
            
            return true;
        }
        
        return false;
    }
}