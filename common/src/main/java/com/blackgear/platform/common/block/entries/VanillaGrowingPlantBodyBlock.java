package com.blackgear.platform.common.block.entries;

import com.blackgear.platform.core.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;
import java.util.Random;

public abstract class VanillaGrowingPlantBodyBlock extends VanillaGrowingPlantBlock implements BonemealableBlock {
    protected VanillaGrowingPlantBodyBlock(Properties properties, Direction growthDirection, VoxelShape shape, boolean scheduleFluidTicks) {
        super(properties, growthDirection, shape, scheduleFluidTicks);
    }
    
    protected BlockState updateHeadAfterConvertedFromBody(BlockState head, BlockState body) {
        return body;
    }
    
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (direction == this.growthDirection.getOpposite() && !state.canSurvive(level, currentPos)) {
            level.getBlockTicks().scheduleTick(currentPos, this, 1);
        }
        
        VanillaGrowingPlantHeadBlock headBlock = this.getHeadBlock();
        if (direction == this.growthDirection && !neighborState.is(this) && !neighborState.is(headBlock)) {
            return this.updateHeadAfterConvertedFromBody(state, headBlock.getStateForPlacement(level));
        }
        
        if (this.scheduleFluidTicks) {
            level.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }
    
    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(this.getHeadBlock());
    }
    
    @Override
    public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
        Optional<BlockPos> headPos = this.getHeadPos(level, pos, state.getBlock());
        return headPos.isPresent() && this.getHeadBlock().canGrowInto(level.getBlockState(headPos.get().relative(this.growthDirection)));
    }
    
    @Override
    public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
        return true;
    }
    
    @Override
    public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
        Optional<BlockPos> headPos = this.getHeadPos(level, pos, state.getBlock());
        
        if (headPos.isPresent()) {
            BlockState headState = level.getBlockState(headPos.get());
            ((VanillaGrowingPlantHeadBlock) headState.getBlock()).performBonemeal(level, random, headPos.get(), headState);
        }
    }
    
    private Optional<BlockPos> getHeadPos(BlockGetter level, BlockPos pos, Block block) {
        return BlockUtils.getTopConnectedBlock(level, pos, block, this.growthDirection, this.getHeadBlock());
    }
    
    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        boolean result = super.canBeReplaced(state, useContext);
        return (result && useContext.getItemInHand().getItem() == this.getHeadBlock().asItem()) || result;
    }
    
    @Override
    protected Block getBodyBlock() {
        return this;
    }
}