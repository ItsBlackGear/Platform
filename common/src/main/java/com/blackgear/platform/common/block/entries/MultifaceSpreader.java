package com.blackgear.platform.common.block.entries;

import com.blackgear.platform.core.util.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

/**
 * This class is responsible for managing the spread of block to multiple faces.
 * It provides methods to spread blocks in various directions based on a defined configuration.
 */
public class MultifaceSpreader {
    public static final SpreadType[] DEFAULT_SPREAD_ORDER = new SpreadType[] {
        SpreadType.SAME_POSITION,
        SpreadType.SAME_PLANE,
        SpreadType.WRAP_AROUND
    };
    private final SpreadConfig config;
    
    /**
     * Constructs a {@link MultifaceSpreader} using the default spreader configuration.
     *
     * @param block The {@link MultifaceBlock} associated with this spreader.
     */
    public MultifaceSpreader(MultifaceBlock block) {
        this(new DefaultSpreaderConfig(block));
    }
    
    /**
     * Constructs a {@link MultifaceSpreader} with a custom spread configuration.
     *
     * @param config The custom {@link SpreadConfig} implementation for the spreader.
     */
    public MultifaceSpreader(SpreadConfig config) {
        this.config = config;
    }
    
    /**
     * Checks if spreading is possible in any direction from the given position.
     *
     * @param state The block state to be spread.
     * @param level The world in which to perform the check.
     * @param pos The position of the block being checked.
     * @param spreadDirection The initial spread direction.
     * @return True if spreading is possible in any direction, false otherwise.
     */
    public boolean canSpreadInAnyDirection(BlockState state, BlockGetter level, BlockPos pos, Direction spreadDirection) {
        return DirectionUtils.stream().anyMatch(dir -> {
            return this.getSpreadFromFaceTowardDirection(state, level, pos, spreadDirection, dir, this.config::canSpreadInto).isPresent();
        });
    }
    
    /**
     * Attempts to spread the block from a random face to a random direction.
     *
     * @param state The block state to be spread.
     * @param level The world in which to perform the spread.
     * @param pos The position of the block being spread.
     * @param random The source of randomness for choosing directions.
     * @return An {@link Optional} containing the {@link SpreadPos} if spreading is successful, otherwise empty.
     */
    public Optional<SpreadPos> spreadFromRandomFaceTowardRandomDirection(BlockState state, LevelAccessor level, BlockPos pos, Random random) {
        return DirectionUtils.allShuffled(random).stream().filter(direction -> {
            return this.config.canSpreadFrom(state, direction);
        }).map(direction -> {
            return this.spreadFromFaceTowardRandomDirection(state, level, pos, direction, random, false);
        }).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
    }
    
    /**
     * Spreads the block to all valid faces.
     *
     * @param state The block state to be spread.
     * @param level The world in which to perform the spread.
     * @param pos The position of the block being spread.
     * @param markForPostprocessing Whether to mark the position for postprocessing.
     * @return The number of successful spreads.
     */
    public long spreadAll(BlockState state, LevelAccessor level, BlockPos pos, boolean markForPostprocessing) {
        return DirectionUtils.stream().filter(direction -> {
            return this.config.canSpreadFrom(state, direction);
        }).map(direction -> {
            return this.spreadFromFaceTowardAllDirections(state, level, pos, direction, markForPostprocessing);
        }).reduce(0L, Long::sum);
    }
    
    /**
     * Attempts to spread the block from a specific face to a random direction.
     *
     * @param state The block state to be spread.
     * @param level The world in which to perform the spread.
     * @param pos The position of the block being spread.
     * @param spreadDirection The direction from which spreading originates.
     * @param random The source of randomness for choosing directions.
     * @param markForPostprocessing Whether to mark the position for postprocessing.
     * @return An {@link Optional} containing the {@link SpreadPos} if spreading is successful, otherwise empty.
     */
    public Optional<SpreadPos> spreadFromFaceTowardRandomDirection(BlockState state, LevelAccessor level, BlockPos pos, Direction spreadDirection, Random random, boolean markForPostprocessing) {
        return DirectionUtils.allShuffled(random).stream().map(dir -> {
            return this.spreadFromFaceTowardDirection(state, level, pos, spreadDirection, dir, markForPostprocessing);
        }).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
    }
    
    /**
     * Spreads the block from a specific face to all valid directions.
     *
     * @param state The block state to be spread.
     * @param level The world in which to perform the spread.
     * @param pos The position of the block being spread.
     * @param spreadDirection The direction from which spreading originates.
     * @param markForPostprocessing Whether to mark the position for postprocessing.
     * @return The number of successful spreads.
     */
    private long spreadFromFaceTowardAllDirections(BlockState state, LevelAccessor level, BlockPos pos, Direction spreadDirection, boolean markForPostprocessing) {
        return DirectionUtils.stream().map(dir -> {
            return this.spreadFromFaceTowardDirection(state, level, pos, spreadDirection, dir, markForPostprocessing);
        }).filter(Optional::isPresent).count();
    }
    
    /**
     * Attempts to spread the block from a specific face towards a specific direction.
     *
     * @param state The block state to be spread.
     * @param level The world in which to perform the spread.
     * @param pos The position of the block being spread.
     * @param spreadDirection The direction from which spreading originates.
     * @param face The target face direction from spreading.
     * @param markForPostprocessing Whether to mark the position for postprocessing.
     * @return An {@link Optional} containing the {@link SpreadPos} if spreading is successful, otherwise empty.
     */
    public Optional<SpreadPos> spreadFromFaceTowardDirection(BlockState state, LevelAccessor level, BlockPos pos, Direction spreadDirection, Direction face, boolean markForPostprocessing) {
        return this.getSpreadFromFaceTowardDirection(state, level, pos, spreadDirection, face, this.config::canSpreadInto).flatMap((spreadPos) -> {
            return this.spreadToFace(level, spreadPos, markForPostprocessing);
        });
    }
    
    /**
     * Attempts to get a valid {@link SpreadPos} for spreading from a specific face towards a specific direction.
     *
     * @param state The block state to be spread.
     * @param level The world in which to perform the check.
     * @param pos The position of the block being spread.
     * @param spreadDirection The direction from which spreading originates.
     * @param face The target face direction for spreading.
     * @param spreadPredicate The predicate to check if the spread is valid.
     * @return An {@link Optional} containing the valid {@link SpreadPos} if found, otherwise empty.
     */
    public Optional<SpreadPos> getSpreadFromFaceTowardDirection(BlockState state, BlockGetter level, BlockPos pos, Direction spreadDirection, Direction face, SpreadPredicate spreadPredicate) {
        if (face.getAxis() == spreadDirection.getAxis()) {
            // If the target and source direction have the same axis, spreading is not possible.
            return Optional.empty();
        } else if (!this.config.isOtherBlockValidAsSource(state) && (!this.config.hasFace(state, spreadDirection) || this.config.hasFace(state, face))) {
            // If the other block is not a valid source and either the source or target direction is missing a face, spreading is not possible.
            return Optional.empty();
        } else {
            // Iterate over the spread types and check if spreading is possible for each type.
            for (SpreadType spreadType : this.config.getSpreadTypes()) {
                SpreadPos spreadPos = spreadType.getSpreadPos(pos, face, spreadDirection);
                if (spreadPredicate.test(level, pos, spreadPos)) {
                    return Optional.of(spreadPos);
                }
            }
            
            return Optional.empty();
        }
    }
    
    /**
     * Attempts to spread the block to a specific face.
     *
     * @param level The world in which to perform the spread.
     * @param pos The {@link SpreadPos} containing the block position and face direction.
     * @param markForPostprocessing Whether to mark the position for postprocessing.
     * @return An {@link Optional} containing the {@link SpreadPos} if spreading is successful, otherwise empty.
     */
    public Optional<SpreadPos> spreadToFace(LevelAccessor level, SpreadPos pos, boolean markForPostprocessing) {
        BlockState state = level.getBlockState(pos.pos);
        return this.config.placeBlock(level, pos, state, markForPostprocessing) ? Optional.of(pos) : Optional.empty();
    }
    
    /**
     * Default implementation of SpreadConfig.
     * Provides methods to determine valid spreading conditions.
     */
    public static class DefaultSpreaderConfig implements SpreadConfig {
        protected final MultifaceBlock block;
        
        /**
         * Constructs a {@link DefaultSpreaderConfig} with the associated {@link MultifaceBlock}.
         *
         * @param block The {@link MultifaceBlock} to configure spreading for.
         */
        public DefaultSpreaderConfig(MultifaceBlock block) {
            this.block = block;
        }
        
        /**
         * Gets the block state for placement based on the current state and facing direction.
         *
         * @param currentState The current block state.
         * @param level The world in which the block is placed.
         * @param pos The position of the block.
         * @param lookingDirection The facing direction for placement.
         * @return The {@link BlockState} for placement, or null if invalid.
         */
        @Override
        public @Nullable BlockState getStateForPlacement(BlockState currentState, BlockGetter level, BlockPos pos, Direction lookingDirection) {
            return this.block.getStateForPlacement(currentState, level, pos, lookingDirection);
        }
        
        protected boolean stateCanBeReplaced(BlockGetter level, BlockPos sourcePos, BlockPos targetPos, Direction direction, BlockState state) {
            return state.isAir() || state.is(this.block) || state.is(Blocks.WATER) && state.getFluidState().isSource();
        }
        
        /**
         * Checks if spreading into a {@link SpreadPos} is valid.
         *
         * @param level The world in which to perform the check.
         * @param pos The current block position.
         * @param spreadPos The target {@link SpreadPos} for spreading.
         * @return True if spreading is valid, false otherwise.
         */
        @Override
        public boolean canSpreadInto(BlockGetter level, BlockPos pos, SpreadPos spreadPos) {
            BlockState state = level.getBlockState(spreadPos.pos);
            return this.stateCanBeReplaced(level, pos, spreadPos.pos, spreadPos.face, state) && this.block.isValidStateForPlacement(level, state, spreadPos.pos, spreadPos.face);
        }
    }
    
    /**
     * Interface for defining spread configurations.
     * It provides methods for obtaining the block state, checking if spreading is possible, and customizing the spread behavior.
     */
    public interface SpreadConfig {
        @Nullable BlockState getStateForPlacement(BlockState state, BlockGetter level, BlockPos pos, Direction direction);
        
        boolean canSpreadInto(BlockGetter level, BlockPos blockPos, SpreadPos spreadPos);
        
        default SpreadType[] getSpreadTypes() {
            return MultifaceSpreader.DEFAULT_SPREAD_ORDER;
        }
        
        default boolean hasFace(BlockState state, Direction direction) {
            return MultifaceBlock.hasFace(state, direction);
        }
        
        default boolean isOtherBlockValidAsSource(BlockState state) {
            return false;
        }
        
        default boolean canSpreadFrom(BlockState state, Direction direction) {
            return this.isOtherBlockValidAsSource(state) || this.hasFace(state, direction);
        }
        
        default boolean placeBlock(LevelAccessor level, SpreadPos spreadPos, BlockState state, boolean postProcess) {
            BlockState blockState = this.getStateForPlacement(state, level, spreadPos.pos, spreadPos.face);
            if (blockState != null) {
                if (postProcess) {
                    level.getChunk(spreadPos.pos).markPosForPostprocessing(spreadPos.pos);
                }
                
                return level.setBlock(spreadPos.pos, blockState, 2);
            } else {
                return false;
            }
        }
    }
    
    /**
     * Interface for defining a spread predicate.
     * It takes a level, position, and spread position, and returns a boolean indicating if spreading is possible.
     */
    public interface SpreadPredicate {
        boolean test(BlockGetter level, BlockPos pos, SpreadPos spreadPos);
    }
    
    /**
     * It defines different ways of spreading to calculate the spread position.
     */
    public enum SpreadType {
        SAME_POSITION {
            @Override public SpreadPos getSpreadPos(BlockPos pos, Direction face, Direction spreadDirection) {
                return new SpreadPos(pos, face);
            }
        },
        SAME_PLANE {
            @Override public SpreadPos getSpreadPos(BlockPos pos, Direction face, Direction spreadDirection) {
                return new SpreadPos(pos.relative(face), spreadDirection);
            }
        },
        WRAP_AROUND {
            @Override public SpreadPos getSpreadPos(BlockPos pos, Direction face, Direction spreadDirection) {
                return new SpreadPos(pos.relative(face).relative(spreadDirection), face.getOpposite());
            }
        };
        
        /**
         * Get a SpreadPos instance based on the spread type and positions.
         *
         * @param pos The initial position.
         * @param face The face to spread towards.
         * @param spreadDirection The direction of spreading.
         * @return A {@link SpreadPos} instance with calculated position and face.
         */
        public abstract SpreadPos getSpreadPos(BlockPos pos, Direction face, Direction spreadDirection);
    }
    
    /**
     * Represents a position for spreading, including a block position and a face direction.
     */
    public static class SpreadPos {
        private final BlockPos pos;
        private final Direction face;
        
        public SpreadPos(BlockPos pos, Direction face) {
            this.pos = pos;
            this.face = face;
        }
    }
}