package com.blackgear.platform.common.worldgen.biome;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.context.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class WeightedBiomePicker {
    private double currentTotal;
    private final List<WeightedBiomeEntry> entries;
    
    WeightedBiomePicker() {
        this.currentTotal = 0;
        this.entries = Collections.synchronizedList(new ArrayList<>());
    }
    
    void addBiome(final ResourceKey<Biome> biome, final double weight) {
        this.currentTotal += weight;
        this.entries.add(new WeightedBiomeEntry(biome, weight, this.currentTotal));
    }
    
    double getCurrentWeightTotal() {
        return this.currentTotal;
    }
    
    public ResourceKey<Biome> pickRandom(Context random) {
        double target = random.nextRandom(Integer.MAX_VALUE) * getCurrentWeightTotal() / Integer.MAX_VALUE;
        return search(target).getBiome();
    }
    
    public ResourceKey<Biome> pickFromNoise(Context source, double x, double y, double z) {
        double target = Math.abs(source.getBiomeNoise().noise(x, y, z, 0.0, 0.0)) * getCurrentWeightTotal();
        return search(target).getBiome();
    }
    
    /**
     * Searches with the specified target value.
     *
     * @param target The target value, must satisfy the constraint 0 <= target <= currentTotal
     * @return The result of the search
     */
    public WeightedBiomeEntry search(final double target) {
        // Sanity checks, fail fast if stuff is going wrong.
        Preconditions.checkArgument(target <= currentTotal, "The provided target value for biome selection must be less than or equal to the weight total");
        Preconditions.checkArgument(target >= 0, "The provided target value for biome selection cannot be negative");
        
        int low = 0;
        int high = entries.size() - 1;

        while (low < high) {
            int mid = (high + low) >>> 1;

            if (target < entries.get(mid).getUpperWeightBound()) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }

        return entries.get(low);
    }
}
