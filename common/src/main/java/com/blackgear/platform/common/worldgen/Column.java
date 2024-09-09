package com.blackgear.platform.common.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;

/**
 * A representation of an integer valued interval, either bounded or unbounded.
 * While the class itself does not imply any coordinate in particular, this is practically used to represent a column in the Y direction.
 */
public abstract class Column {
    /**
     * @return A column of the closed interval [floor, ceiling].
     */
    public static Range around(int floor, int ceiling) {
        return new Range(floor - 1, ceiling + 1);
    }
    
    /**
     * @return A column of the open interval (floor, ceiling).
     */
    public static Range inside(int floor, int ceiling) {
        return new Range(floor, ceiling);
    }
    
    /**
     * @return A column of the unbounded interval (-infinity, ceiling).
     */
    public static Column below(int ceiling) {
        return new Ray(ceiling, false);
    }
    
    /**
     * @return A column of the unbounded interval (-infinity, ceiling].
     */
    public static Column fromHighest(int ceiling) {
        return new Ray(ceiling + 1, false);
    }
    
    /**
     * @return A column of the unbounded interval (floor, infinity).
     */
    public static Column above(int floor) {
        return new Ray(floor, true);
    }
    
    /**
     * @return A column of the unbounded interval [floor, infinity).
     */
    public static Column fromLowest(int floor) {
        return new Ray(floor - 1, true);
    }
    
    /**
     * @return A column of the unbounded interval (-infinity, infinity).
     */
    public static Column line() {
        return Line.INSTANCE;
    }
    
    public static Column create(OptionalInt floor, OptionalInt ceiling) {
        if (floor.isPresent() && ceiling.isPresent()) {
            return inside(floor.getAsInt(), ceiling.getAsInt());
        } else if (floor.isPresent()) {
            return above(floor.getAsInt());
        } else {
            return ceiling.isPresent() ? below(ceiling.getAsInt()) : line();
        }
    }
    
    public abstract OptionalInt getCeiling();
    
    public abstract OptionalInt getFloor();
    
    public abstract OptionalInt getHeight();
    
    public Column withFloor(OptionalInt floor) {
        return create(floor, this.getCeiling());
    }
    
    public Column withCeiling(OptionalInt ceiling) {
        return create(this.getFloor(), ceiling);
    }
    
    /**
     * Scans for a column of states satisfying {@code columnPredicate}, up to a length of {@code maxDistance} from the origin, and ending with a state which satisfies {@code tipPredicate}.
     * @return A column representing the tips found. The column will be bounded if a tip was reached in the given direction, unbounded otherwise.
     */
    public static Optional<Column> scan(
        LevelSimulatedReader level,
        BlockPos blockPos,
        int maxDistance,
        Predicate<BlockState> columnPredicate,
        Predicate<BlockState> tipPredicate
    ) {
        BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
        if (!level.isStateAtPosition(blockPos, columnPredicate)) {
            return Optional.empty();
        } else {
            int y = blockPos.getY();
            OptionalInt optionalInt = scanDirection(level, maxDistance, columnPredicate, tipPredicate, mutableBlockPos, y, Direction.UP);
            OptionalInt optionalInt2 = scanDirection(level, maxDistance, columnPredicate, tipPredicate, mutableBlockPos, y, Direction.DOWN);
            return Optional.of(create(optionalInt2, optionalInt));
        }
    }
    
    /**
     * Scans for a sequence of states in a given {@code direction}, up to a length of {@code maxDistance} which satisfy {@code columnPredicate}, and ending with a state which satisfies {@code tipPredicate}.
     * @return The y position of the tip, if found.
     */
    private static OptionalInt scanDirection(
        LevelSimulatedReader level,
        int maxDistance,
        Predicate<BlockState> columnPredicate,
        Predicate<BlockState> tipPredicate,
        BlockPos.MutableBlockPos mutable,
        int startY,
        Direction direction
    ) {
        mutable.setY(startY);
        
        for(int i = 1; i < maxDistance && level.isStateAtPosition(mutable, columnPredicate); ++i) {
            mutable.move(direction);
        }
        
        return level.isStateAtPosition(mutable, tipPredicate) ? OptionalInt.of(mutable.getY()) : OptionalInt.empty();
    }
    
    public static final class Range extends Column {
        private final int floor;
        private final int ceiling;
        
        private Range(int floor, int ceiling) {
            this.floor = floor;
            this.ceiling = ceiling;
            
            if (this.height() < 0) {
                throw new IllegalArgumentException("Column of negative height: " + this);
            }
        }
        
        public OptionalInt getCeiling() {
            return OptionalInt.of(this.ceiling);
        }
        
        public OptionalInt getFloor() {
            return OptionalInt.of(this.floor);
        }
        
        public OptionalInt getHeight() {
            return OptionalInt.of(this.height());
        }
        
        public int ceiling() {
            return this.ceiling;
        }
        
        public int floor() {
            return this.floor;
        }
        
        public int height() {
            return this.ceiling - this.floor - 1;
        }
        
        public String toString() {
            return "C(" + this.ceiling + "-" + this.floor + ")";
        }
    }
    
    public static final class Ray extends Column {
        private final int edge;
        private final boolean pointingUp;
        
        public Ray(int edge, boolean pointingUp) {
            this.edge = edge;
            this.pointingUp = pointingUp;
        }
        
        public OptionalInt getCeiling() {
            return this.pointingUp ? OptionalInt.empty() : OptionalInt.of(this.edge);
        }
        
        public OptionalInt getFloor() {
            return this.pointingUp ? OptionalInt.of(this.edge) : OptionalInt.empty();
        }
        
        public OptionalInt getHeight() {
            return OptionalInt.empty();
        }
        
        public String toString() {
            return this.pointingUp ? "C(" + this.edge + "-)" : "C(-" + this.edge + ")";
        }
    }
    
    public static final class Line extends Column {
        static final Line INSTANCE = new Line();
        
        public OptionalInt getCeiling() {
            return OptionalInt.empty();
        }
        
        public OptionalInt getFloor() {
            return OptionalInt.empty();
        }
        
        public OptionalInt getHeight() {
            return OptionalInt.empty();
        }
        
        public String toString() {
            return "C(-)";
        }
    }
}