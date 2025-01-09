package com.blackgear.platform.common.block.entries;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public abstract class VanillaGrowingPlantHeadBlock extends VanillaGrowingPlantBlock implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_25;
    public static final int MAX_AGE = 25;
    private final double growPerTickProbability;
    
    public VanillaGrowingPlantHeadBlock(Properties properties, Direction growthDirection, VoxelShape shape, boolean scheduleFluidTicks, double growPerTickProbability) {
        super(properties, growthDirection, shape, scheduleFluidTicks);
        this.growPerTickProbability = growPerTickProbability;
        this.registerDefaultState(
            this.getStateDefinition().any()
                .setValue(AGE, 0)
        );
    }
    
    @Override
    public BlockState getStateForPlacement(LevelAccessor level) {
        return this.defaultBlockState().setValue(AGE, level.getRandom().nextInt(MAX_AGE));
    }
    
    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }
    
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (state.getValue(AGE) < MAX_AGE && random.nextDouble() < this.growPerTickProbability) {
            BlockPos offset = pos.relative(this.growthDirection);
            
            if (this.canGrowInto(level.getBlockState(offset))) {
                level.setBlockAndUpdate(offset, this.getGrowIntoState(state, level.random));
            }
        }
    }
    
    protected BlockState getGrowIntoState(BlockState state, Random random) {
        return state.cycle(AGE);
    }
    
    public BlockState getMaxAgeState(BlockState state) {
        return state.setValue(AGE, MAX_AGE);
    }
    
    public boolean isMaxAge(BlockState state) {
        return state.getValue(AGE) == MAX_AGE;
    }
    
    protected BlockState updateBodyAfterConvertedFromHead(BlockState head, BlockState body) {
        return body;
    }
    
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (direction == this.growthDirection.getOpposite() && !state.canSurvive(level, currentPos)) {
            level.getBlockTicks().scheduleTick(currentPos, this, 1);
        }
        
        if (direction != this.growthDirection || !neighborState.is(this) && !neighborState.is(this.getBodyBlock())) {
            if (this.scheduleFluidTicks) {
                level.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            }
            
            return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
        }
        
        return this.updateBodyAfterConvertedFromHead(state, this.getBodyBlock().defaultBlockState());
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
    
    @Override
    public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
        return this.canGrowInto(level.getBlockState(pos.relative(this.growthDirection)));
    }
    
    @Override
    public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
        return true;
    }
    
    @Override
    public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
        BlockPos offset = pos.relative(this.growthDirection);
        
        int age = Math.min(state.getValue(AGE) + 1, MAX_AGE);
        int blocksToGrow = this.getBlocksToGrowWhenBonemealed(random);
        
        for (int i = 0; i < blocksToGrow && this.canGrowInto(level.getBlockState(offset)); i++) {
            level.setBlockAndUpdate(offset, state.setValue(AGE, age));
            offset = offset.relative(this.growthDirection);
            age = Math.min(age + 1, MAX_AGE);
        }
    }
    
    protected abstract int getBlocksToGrowWhenBonemealed(Random random);
    
    protected abstract boolean canGrowInto(BlockState state);
    
    @Override
    protected VanillaGrowingPlantHeadBlock getHeadBlock() {
        return this;
    }
}