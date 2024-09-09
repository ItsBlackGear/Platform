package com.blackgear.platform.core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class DirectionUtils {
    public static final Codec<Direction> CODEC = StringRepresentable.fromEnum(Direction::values, Direction::byName);
    public static final Codec<Direction> VERTICAL_CODEC = CODEC.flatXmap(DirectionUtils::verifyVertical, DirectionUtils::verifyVertical);
    
    private static DataResult<Direction> verifyVertical(Direction direction) {
        return direction.getAxis().isVertical()
            ? DataResult.success(direction)
            : DataResult.error("Expected a vertical direction");
    }
    
    /**
     * Returns a shuffled collection of all directions.
     * It shuffles the array of directions using the provided random value and returns a collection of the shuffled directions.
     *
     * @param random The random value used for shuffling.
     * @return A shuffled collection of directions.
     */
    public static Collection<Direction> allShuffled(Random random) {
        return shuffledCopy(Direction.values(), random);
    }
    
    /**
     * Returns a stream of all directions.
     */
    public static Stream<Direction> stream() {
        return Stream.of(Direction.values());
    }
    
    /**
     * Creates a shuffled copy of a stream of objects.
     * It collects the elements from the stream into an ObjectArrayList, shuffles the list using the provided random object,
     * and returns a new shuffled list.
     *
     * @param stream    The stream of objects to shuffle.
     * @param random    The random value used for shuffling.
     * @param <T>       The type of objects in the stream.
     * @return A shuffled copy of the stream.
     */
    public static <T> List<T> toShuffledList(Stream<T> stream, Random random) {
        ObjectArrayList<T> list = stream.collect(Collector.of(ObjectArrayList::new, ObjectArrayList::add, (source, target) -> {
            source.addAll(target);
            return source;
        }));
        shuffle(list, random);
        return list;
    }
    
    /**
     * Creates a shuffled copy of an array of objects.
     * It shuffles the elements in the array using the provided random object and returns a new shuffled list.
     *
     * @param objects   The array of objects to shuffle.
     * @param random    The random value used for shuffling.
     * @param <T>       The type of objects in the array.
     * @return A shuffled copy of the array.
     */
    private static <T> List<T> shuffledCopy(T[] objects, Random random) {
        ObjectArrayList<T> list = new ObjectArrayList<>(objects);
        shuffle(list, random);
        return list;
    }
    
    /**
     * Creates a shuffled copy of an ObjectArrayList.
     * It shuffles the elements in the list using the provided random object and returns a new shuffled list.
     *
     * @param list      The ObjectArrayList to shuffle.
     * @param random    The random value used for shuffling.
     * @param <T>       The type of objects in the list.
     * @return A shuffled copy of the list.
     */
    public static <T> List<T> shuffledCopy(ObjectArrayList<T> list, Random random) {
        ObjectArrayList<T> newList = new ObjectArrayList<>(list);
        shuffle(newList, random);
        return newList;
    }
    
    /**
     * Shuffles the elements in an ObjectArrayStream using the Fisher-Yates algorithm.
     *
     * @param list      The ObjectArrayList to shuffle.
     * @param random    The random value used for shuffling.
     * @param <T>       The type of objects in the list
     */
    private static <T> void shuffle(ObjectArrayList<T> list, Random random) {
        int size = list.size();
        
        for (int i = size; i > 1; i--) {
            int index = random.nextInt(i);
            list.set(i - 1, list.set(index, list.get(i - 1)));
        }
    }
    /**
     * Determines the direction an entity is facing along a given axis.
     * <p>
     * For the X axis, it checks if the entity is facing the EAST direction. If not, it assumes the entity is facing WEST.
     * For the Z axis, it checks if the entity is facing the SOUTH direction. If not, it assumes the entity is facing NORTH.
     * For the Y axis, it checks if the entity's view rotation on the X axis is less than 0. If so, it assumes the entity is facing UP, otherwise it assumes the entity is facing DOWN.
     *
     * @param entity The entity whose facing direction is to be determined.
     * @param axis The axis along which the entity's facing direction is to be determined.
     * @return The direction the entity is facing along the given axis.
     */
    public static Direction getFacingAxis(Entity entity, Direction.Axis axis) {
        switch (axis) {
            case X :
                return Direction.EAST.isFacingAngle(entity.getViewYRot(1.0F)) ? Direction.EAST : Direction.WEST;
            case Z :
                return Direction.SOUTH.isFacingAngle(entity.getViewYRot(1.0F)) ? Direction.SOUTH : Direction.NORTH;
            default:
                return entity.getViewXRot(1.0F) < 0.0F ? Direction.UP : Direction.DOWN;
        }
    }
}