package com.blackgear.platform.core.util;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public final class NoiseUtils {
    /**
     * Creates a NormalNoise instance with the given parameters.
     *
     * @param random    The random number generator.
     * @param offset    The offset for the noise.
     * @param octaves   The octaves for the noise.
     * @return A NormalNoise instance.
     */
    public static NormalNoise normal(WorldgenRandom random, int offset, double... octaves) {
        return NormalNoise.create(random, offset, new DoubleArrayList(octaves));
    }
    
    /**
     * Creates a NormalNoise instance with the given parameters.
     *
     * @param seed      The seed to use.
     * @param offset    The offset for the noise.
     * @param octaves   The octaves for the noise.
     * @return A NormalNoise instance.
     */
    public static NormalNoise normal(long seed, int offset, double... octaves) {
        return NormalNoise.create(new WorldgenRandom(seed), offset, new DoubleArrayList(octaves));
    }
    
    /**
     * Samples noise from the given NormalNoise instance and maps the result to a specified range.
     *
     * @param noise The NormalNoise instance to sample from.
     * @param x     The x-coordinate for the noise sample.
     * @param y     The y-coordinate for the noise sample.
     * @param z     The z-coordinate for the noise sample.
     * @param start The start of the range to map the noise value to.
     * @param end   The end of the range to map the noise value to.
     * @return The noise value mapped to the specified range.
     */
    public static double sampleNoiseAndMapToRange(NormalNoise noise, double x, double y, double z, double start, double end) {
        double value = noise.getValue(x, y, z);
        return MathUtils.map(value, -1.0, 1.0, start, end);
    }
    
    /**
     * Biases a value towards its extreme based on a given factor.
     *
     * @param value     The value to bias.
     * @param factor    The factor to bias the value by.
     * @return The biased value.
     */
    public static double biasTowardsExtreme(double value, double factor) {
        return value + Math.sin(Math.PI * value) * factor / Math.PI;
    }
}