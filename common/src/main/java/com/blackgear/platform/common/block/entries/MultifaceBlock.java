package com.blackgear.platform.common.block.entries;

import com.blackgear.platform.core.util.BlockUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public abstract class MultifaceBlock extends Block {
    private static final VoxelShape UP_AABB = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape DOWN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    private static final VoxelShape WEST_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    private static final VoxelShape EAST_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
    private static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION = Util.make(Maps.newEnumMap(Direction.class), shape -> {
        shape.put(Direction.NORTH, NORTH_AABB);
        shape.put(Direction.EAST, EAST_AABB);
        shape.put(Direction.SOUTH, SOUTH_AABB);
        shape.put(Direction.WEST, WEST_AABB);
        shape.put(Direction.UP, UP_AABB);
        shape.put(Direction.DOWN, DOWN_AABB);
    });
    private final ImmutableMap<BlockState, VoxelShape> shapesCache;
    private final boolean canRotate;
    private final boolean canMirrorX;
    private final boolean canMirrorZ;
    
    public MultifaceBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(getDefaultMultiFaceState(this.stateDefinition));
        this.shapesCache = BlockUtils.getShapeForEachState(this, MultifaceBlock::calculateMultifaceShape);
        this.canRotate = Direction.Plane.HORIZONTAL.stream().allMatch(this::isFaceSupported);
        this.canMirrorX = Direction.Plane.HORIZONTAL.stream().filter(Direction.Axis.X).filter(this::isFaceSupported).count() % 2L == 0L;
        this.canMirrorZ = Direction.Plane.HORIZONTAL.stream().filter(Direction.Axis.Z).filter(this::isFaceSupported).count() % 2L == 0L;
    }
    
    /**
     * Retrieves the available faces on a given block state
     *
     * @return A set of available faces as directions.
     */
    public static Set<Direction> availableFaces(BlockState state) {
        if (state.getBlock() instanceof MultifaceBlock) {
            Set<Direction> faces = EnumSet.noneOf(Direction.class);
            
            for (Direction direction : Direction.values()) {
                if (hasFace(state, direction)) {
                    faces.add(direction);
                }
            }
            
            return faces;
        } else {
            return ImmutableSet.of();
        }
    }
    
    /**
     * Unpacks the byte representation of directions into a set of directions.
     *
     * @return A set of directions.
     */
    public static Set<Direction> unpack(byte face) {
        Set<Direction> faces = EnumSet.noneOf(Direction.class);
        
        for (Direction direction : Direction.values()) {
            if ((face & (byte)(1 << direction.ordinal())) > 0) {
                faces.add(direction);
            }
        }
        
        return faces;
    }
    
    /**
     * Packs the collection of directions into a byte representation.
     *
     * @return The packed byte representation of directions
     */
    public static byte pack(Collection<Direction> directions) {
        byte face = 0;
        
        for (Direction direction : directions) {
            face = (byte)(face | 1 << direction.ordinal());
        }
        
        return face;
    }
    
    /**
     * Determines if a given direction is supported by the block.
     *
     * @return True if the direction is supported.
     */
    protected boolean isFaceSupported(Direction direction) {
        return true;
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        for (Direction direction : Direction.values()) {
            if (this.isFaceSupported(direction)) {
                builder.add(getFaceProperty(direction));
            }
        }
    }
    
    @Override
    public BlockState updateShape(BlockState currentState, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (!hasAnyFace(currentState)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            if (hasFace(currentState, direction) && !canAttachTo(level, direction, neighborPos, neighborState)) {
                return removeFace(currentState, getFaceProperty(direction));
            }
            
            return currentState;
        }
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shapesCache.get(state);
    }
    
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        boolean canSurvive = false;
        
        for (Direction direction : Direction.values()) {
            if (hasFace(state, direction)) {
                BlockPos blockPos = pos.relative(direction);
                if (!canAttachTo(level, direction, blockPos, level.getBlockState(blockPos))) {
                    return false;
                }
                
                canSurvive = true;
            }
        }
        
        return canSurvive;
    }
    
    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return hasAnyVacantFace(state);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        return Arrays.stream(context.getNearestLookingDirections()).map(direction -> {
            return this.getStateForPlacement(state, level, pos, direction);
        }).filter(Objects::nonNull).findFirst().orElse(null);
    }
    
    /**
     * Checks if the given state is valid for placement in the world at the specified position and direction.
     *
     * @return True if the state is valid for placement.
     */
    public boolean isValidStateForPlacement(BlockGetter level, BlockState state, BlockPos pos, Direction direction) {
        if (this.isFaceSupported(direction) && (!state.is(this) || !hasFace(state, direction))) {
            BlockPos blockPos = pos.relative(direction);
            return canAttachTo(level, direction, blockPos, level.getBlockState(blockPos));
        } else {
            return false;
        }
    }
    
    /**
     * Gets the block state for placement in the world at the specified position and direction.
     *
     * @return The block state for placement, null if the placement is not possible.
     */
    @Nullable
    public BlockState getStateForPlacement(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (!this.isValidStateForPlacement(level, state, pos, direction)) {
            return null;
        } else {
            BlockState blockState;
            if (state.is(this)) {
                blockState = state;
            } else if (this.isWaterloggable() && BlockUtils.isSourceOfWater(state.getFluidState())) {
                blockState = this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true);
            } else {
                blockState = this.defaultBlockState();
            }
            
            return blockState.setValue(getFaceProperty(direction), true);
        }
    }
    
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        if (!this.canRotate) {
            return state;
        } else {
            return this.mapDirections(state, rotation::rotate);
        }
    }
    
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.FRONT_BACK && !this.canMirrorX) {
            return state;
        } else if (mirror == Mirror.LEFT_RIGHT && !this.canMirrorZ) {
            return state;
        } else {
            return this.mapDirections(state, mirror::mirror);
        }
    }
    
    /**
     * Maps the directions of a block state according to a provided mapping function.
     *
     * @return The block state with mapped directions.
     */
    private BlockState mapDirections(BlockState state, Function<Direction, Direction> faces) {
        BlockState blockState = state;
        
        for (Direction direction : Direction.values()) {
            if (this.isFaceSupported(direction)) {
                blockState = blockState.setValue(getFaceProperty(faces.apply(direction)), state.getValue(getFaceProperty(direction)));
            }
        }
        
        return blockState;
    }
    
    public static boolean hasFace(BlockState state, Direction direction) {
        BooleanProperty faceProperty = getFaceProperty(direction);
        return state.hasProperty(faceProperty) && state.getValue(faceProperty);
    }
    
    public static boolean canAttachTo(BlockGetter level, Direction direction, BlockPos pos, BlockState state) {
        return Block.isFaceFull(state.getBlockSupportShape(level, pos), direction.getOpposite()) || Block.isFaceFull(state.getCollisionShape(level, pos), direction.getOpposite());
    }
    
    private boolean isWaterloggable() {
        return this.getStateDefinition().getProperties().contains(BlockStateProperties.WATERLOGGED);
    }
    
    private static BlockState removeFace(BlockState state, BooleanProperty property) {
        BlockState blockState = state.setValue(property, false);
        return hasAnyFace(blockState) ? blockState : Blocks.AIR.defaultBlockState();
    }
    
    public static BooleanProperty getFaceProperty(Direction direction) {
        return PROPERTY_BY_DIRECTION.get(direction);
    }
    
    private static BlockState getDefaultMultiFaceState(StateDefinition<Block, BlockState> definition) {
        BlockState state = definition.any();
        
        for (BooleanProperty property : PROPERTY_BY_DIRECTION.values()) {
            if (state.hasProperty(property)) {
                state = state.setValue(property, false);
            }
        }
        
        return state;
    }
    
    private static VoxelShape calculateMultifaceShape(BlockState state) {
        VoxelShape shape = Shapes.empty();
        
        for (Direction direction : Direction.values()) {
            if (hasFace(state, direction)) {
                shape = Shapes.or(shape, SHAPE_BY_DIRECTION.get(direction));
            }
        }
        
        return shape.isEmpty() ? Shapes.block() : shape;
    }
    
    protected static boolean hasAnyFace(BlockState state) {
        return Arrays.stream(Direction.values()).anyMatch(direction -> hasFace(state, direction));
    }
    
    private static boolean hasAnyVacantFace(BlockState state) {
        return Arrays.stream(Direction.values()).anyMatch(direction -> !hasFace(state, direction));
    }
    
    public abstract MultifaceSpreader getSpreader();
}