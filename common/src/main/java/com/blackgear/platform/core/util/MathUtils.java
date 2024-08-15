package com.blackgear.platform.core.util;

import net.minecraft.util.Mth;

import java.util.Random;

public class MathUtils {
    /**
     * Maps a value from one range to another and clamps it within the new range.
     *
     * @param value The value to be mapped.
     * @param prevStart The start of the original range.
     * @param prevEnd The end of the original range.
     * @param start The start of the new range.
     * @param end The end of the new range.
     * @return The mapped and clamped value.
     */
    public static double clampedMap(double value, double prevStart, double prevEnd, double start, double end) {
        return Mth.clampedLerp(start, end, Mth.inverseLerp(value, prevStart, prevEnd));
    }
    
    /**
     * Maps a value from one range to another and clamps it within the new range.
     *
     * @param value The value to be mapped.
     * @param prevStart The start of the original range.
     * @param prevEnd The end of the original range.
     * @param start The start of the new range.
     * @param end The end of the new range.
     * @return The mapped and clamped value.
     */
    public static float clampedMap(float value, float prevStart, float prevEnd, float start, float end) {
        return clampedLerp(start, end, inverseLerp(value, prevStart, prevEnd));
    }
    
    /**
     * Performs a linear interpolation between two values and clamps the result.
     *
     * @param start The start value.
     * @param end The end value.
     * @param delta The interpolation factor.
     * @return The interpolated and clamped value.
     */
    public static float clampedLerp(float start, float end, float delta) {
        if (delta < 0.0F) {
            return start;
        } else {
            return delta > 1.0F ? end : Mth.lerp(delta, start, end);
        }
    }
    
    /**
     * Calculates the inverse linear interpolation between two values.
     *
     * @param value The value to interpolate.
     * @param start The start value.
     * @param end The end value.
     * @return The result of the inverse linear interpolation.
     */
    public static float inverseLerp(float value, float start, float end) {
        return (value - start) / (end - start);
    }
    
    /**
     * Generates a random integer between two values, inclusive.
     *
     * @param random The random number generator.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return A random integer between min and max, inclusive.
     */
    public static int randomBetweenInclusive(Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
    
    /**
     * Generates a random float between two values.
     *
     * @param random The random number generator.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return A random float between min and max.
     */
    public static float randomBetween(Random random, float min, float max) {
        return random.nextFloat() * (max - min) + min;
    }
    
    /**
     * Generates a normally distributed random float.
     *
     * @param random The random number generator.
     * @param mean The mean of the normal distribution.
     * @param deviation The standard deviation of the normal distribution.
     * @return A normally distributed random float.
     */
    public static float normal(Random random, float mean, float deviation) {
        return mean + (float)random.nextGaussian() * deviation;
    }
    
    /**
     * Maps a value from one range to another.
     *
     * @param value The value to be mapped.
     * @param prevStart The start of the original range.
     * @param prevEnd The end of the original range.
     * @param start The start of the new range.
     * @param end The end of the new range.
     * @return The mapped value.
     */
    public static double map(double value, double prevStart, double prevEnd, double start, double end) {
        return Mth.lerp(Mth.inverseLerp(value, prevStart, prevEnd), start, end);
    }
}