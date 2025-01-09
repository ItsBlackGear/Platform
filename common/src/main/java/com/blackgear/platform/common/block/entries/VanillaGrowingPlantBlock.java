package com.blackgear.platform.common.block.entries;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public abstract class VanillaGrowingPlantBlock extends Block {
    protected final Direction growthDirection;
    protected final VoxelShape shape;
    protected final boolean scheduleFluidTicks;
    
    public VanillaGrowingPlantBlock(Properties properties, Direction growthDirection, VoxelShape shape, boolean scheduleFluidTicks) {
        super(properties);
        this.growthDirection = growthDirection;
        this.shape = shape;
        this.scheduleFluidTicks = scheduleFluidTicks;
    }
    
    @Nullable @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateAtGrowthDirection = context.getLevel().getBlockState(context.getClickedPos().relative(this.growthDirection));
        
        return !stateAtGrowthDirection.is(this.getHeadBlock()) && !stateAtGrowthDirection.is(this.getBodyBlock())
            ? this.getStateForPlacement(context.getLevel())
            : this.getBodyBlock().defaultBlockState();
    }
    
    public BlockState getStateForPlacement(LevelAccessor level) {
        return this.defaultBlockState();
    }
    
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos positionBehind = pos.relative(this.growthDirection.getOpposite());
        BlockState stateBehind = level.getBlockState(positionBehind);
        
        return this.canAttachTo(stateBehind) && (stateBehind.is(this.getHeadBlock()) || stateBehind.is(this.getBodyBlock()) || stateBehind.isFaceSturdy(level, positionBehind, this.growthDirection));
    }
    
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }
    
    protected boolean canAttachTo(BlockState state) {
        return true;
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shape;
    }
    
    protected abstract VanillaGrowingPlantHeadBlock getHeadBlock();
    
    protected abstract Block getBodyBlock();
}