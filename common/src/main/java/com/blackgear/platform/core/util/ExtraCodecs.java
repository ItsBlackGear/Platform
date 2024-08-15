package com.blackgear.platform.core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.List;
import java.util.function.Function;

public class ExtraCodecs {
    public static final Codec<Integer> POSITIVE_INT = intRangeWithMessage(1, Integer.MAX_VALUE, integer -> "Value must be positive: " + integer);
    public static final Codec<Float> POSITIVE_FLOAT = floatRangeMinExclusiveWithMessage(0.0F, Float.MAX_VALUE, float_ -> "Value must be positive: " + float_);
    
    /**
     * Validates a codec by applying a function to it.
     *
     * @param codec    The codec to be validated.
     * @param function The function to be applied to the codec.
     * @return The validated codec.
     */
    public static <T> Codec<T> validate(Codec<T> codec, Function<T, DataResult<T>> function) {
        return codec.flatXmap(function, function);
    }
    
    /**
     * Checks if a list is non-empty.
     *
     * @return A function that takes a list and returns a DataResult of the list if it's non-empty, or an error otherwise.
     */
    public static <T> Function<List<T>, DataResult<List<T>>> nonEmptyListCheck() {
        return list -> list.isEmpty()
            ? DataResult.error("List must have contents")
            : DataResult.success(list);
    }
    
    /**
     * Validates a codec of a list by checking if the list is non-empty.
     *
     * @param codec The codec of the list to be validated.
     * @return The validated codec of the list.
     */
    public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> codec) {
        return codec.flatXmap(nonEmptyListCheck(), nonEmptyListCheck());
    }
    
    /**
     * Creates a function to check if a number is within a specified range, inclusive, with a custom error message.
     *
     * @param min          The minimum value of the range.
     * @param max          The maximum value of the range.
     * @param errorMessage The function to generate an error message.
     * @param <N>          The type of the number.
     * @return The function to check the range.
     */
    private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> checkRangeWithMessage(N min, N max, Function<N, String> errorMessage) {
        return value -> value.compareTo(min) >= 0 && value.compareTo(max) <= 0
            ? DataResult.success(value)
            : DataResult.error(errorMessage.apply(value));
    }
    
    /**
     * Creates a codec for an integer within a specified range, inclusive, with a custom error message.
     *
     * @param min          The minimum value of the range.
     * @param max          The maximum value of the range.
     * @param errorMessage The function to generate an error message.
     * @return The created codec.
     */
    private static Codec<Integer> intRangeWithMessage(int min, int max, Function<Integer, String> errorMessage) {
        Function<Integer, DataResult<Integer>> function = checkRangeWithMessage(min, max, errorMessage);
        return Codec.INT.flatXmap(function, function);
    }
    
    /**
     * Creates a function to check if a number is within a specified range, exclusive of the minimum, with a custom error message.
     *
     * @param min          The minimum value of the range, exclusive.
     * @param max          The maximum value of the range.
     * @param errorMessage The function to generate an error message.
     * @param <N>          The type of the number.
     * @return The function to check the range.
     */
    private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> checkRangeMinExclusiveWithMessage(N min, N max, Function<N, String> errorMessage) {
        return value -> value.compareTo(min) > 0 && value.compareTo(max) <= 0
            ? DataResult.success(value)
            : DataResult.error(errorMessage.apply(value));
    }
    
    /**
     * Creates a codec for a float within a specified range, exclusive of the minimum, with a custom error message.
     *
     * @param min          The minimum value of the range, exclusive.
     * @param max          The maximum value of the range.
     * @param errorMessage The function to generate an error message.
     * @return The created codec.
     */
    private static Codec<Float> floatRangeMinExclusiveWithMessage(float min, float max, Function<Float, String> errorMessage) {
        Function<Float, DataResult<Float>> function = checkRangeMinExclusiveWithMessage(min, max, errorMessage);
        return Codec.FLOAT.flatXmap(function, function);
    }
}