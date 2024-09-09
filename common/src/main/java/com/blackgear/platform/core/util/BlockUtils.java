package com.blackgear.platform.core.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public final class BlockUtils {
    public static ImmutableMap<BlockState, VoxelShape> getShapeForEachState(Block block, Function<BlockState, VoxelShape> function) {
        return block.getStateDefinition()
            .getPossibleStates()
            .stream()
            .collect(ImmutableMap.toImmutableMap(Function.identity(), function));
    }
    
    public static BlockState withPropertiesOf(Block block, BlockState state) {
        BlockState defaultState = block.defaultBlockState();
        for (Property<?> property : state.getBlock().getStateDefinition().getProperties()) {
            if (defaultState.hasProperty(property)) {
                defaultState = copyProperty(state, defaultState, property);
            }
        }
        
        return defaultState;
    }
    
    public static BlockPos atY(BlockPos pos, int y) {
        return new BlockPos(pos.getX(), y, pos.getZ());
    }
    
    private static <T extends Comparable<T>> BlockState copyProperty(BlockState state, BlockState defaultState, Property<T> property) {
        return defaultState.setValue(property, state.getValue(property));
    }
    
    public static boolean isSourceOfWater(FluidState state) {
        return state.getType() == Fluids.WATER && state.getType().isSource(state);
    }
    
    public static Iterable<BlockPos> randomInCube(Random random, int steps, BlockPos pos, int offset) {
        return BlockPos.randomBetweenClosed(random, steps, pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
    }
    
    public static Optional<BlockPos> getTopConnectedBlock(BlockGetter level, BlockPos pos, Block baseBlock, Direction direction, Block endBlock) {
        BlockPos.MutableBlockPos mutable = pos.mutable();
        
        BlockState state;
        do {
            mutable.move(direction);
            state = level.getBlockState(mutable);
        } while(state.is(baseBlock));
        
        return state.is(endBlock) ? Optional.of(mutable) : Optional.empty();
    }
    
    public static boolean isAirOrWater(BlockState state) {
        return state.isAir() || state.is(Blocks.WATER);
    }
    
    public static BlockState copyWaterloggedFrom(LevelReader level, BlockPos pos, BlockState state) {
        return state.hasProperty(BlockStateProperties.WATERLOGGED)
            ? state.setValue(BlockStateProperties.WATERLOGGED, level.isWaterAt(pos))
            : state;
    }
}