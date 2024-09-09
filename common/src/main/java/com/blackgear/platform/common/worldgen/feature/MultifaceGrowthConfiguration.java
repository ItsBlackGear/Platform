package com.blackgear.platform.common.worldgen.feature;

import com.blackgear.platform.common.block.entries.MultifaceBlock;
import com.blackgear.platform.core.util.DirectionUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class MultifaceGrowthConfiguration implements FeatureConfiguration {
    public static final Codec<MultifaceGrowthConfiguration> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
            Registry.BLOCK.fieldOf("block").flatXmap(MultifaceGrowthConfiguration::apply, DataResult::success).forGetter(config -> config.placeBlock),
            Codec.intRange(1, 64).fieldOf("search_range").orElse(10).forGetter(config -> config.searchRange),
            Codec.BOOL.fieldOf("can_place_on_floor").orElse(false).forGetter(config -> config.canPlaceOnFloor),
            Codec.BOOL.fieldOf("can_place_on_ceiling").orElse(false).forGetter(config -> config.canPlaceOnCeiling),
            Codec.BOOL.fieldOf("can_place_on_wall").orElse(false).forGetter(config -> config.canPlaceOnWall),
            Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spreading").orElse(0.5F).forGetter(config -> config.chanceOfSpreading),
            Registry.BLOCK.listOf().fieldOf("can_be_placed_on").xmap(ImmutableSet::copyOf, ImmutableList::copyOf).forGetter(config -> (ImmutableSet<Block>) config.canBePlacedOn)
        ).apply(instance, MultifaceGrowthConfiguration::new);
    });
    
    public final MultifaceBlock placeBlock;
    public final int searchRange;
    public final boolean canPlaceOnFloor, canPlaceOnCeiling, canPlaceOnWall;
    public final float chanceOfSpreading;
    public final Set<Block> canBePlacedOn;
    private final ObjectArrayList<Direction> validDirections;
    
    private static DataResult<MultifaceBlock> apply(Block block) {
        if (block instanceof MultifaceBlock) {
            return DataResult.success((MultifaceBlock) block);
        } else {
            return DataResult.error("Growth block should be a multiface block");
        }
    }
    public MultifaceGrowthConfiguration(
        MultifaceBlock placeBlock,
        int searchRange,
        boolean canPlaceOnFloor,
        boolean canPlaceOnCeiling,
        boolean canPlaceOnWall,
        float chanceOfSpreading,
        Set<Block> canBePlacedOn
    ) {
        this.placeBlock = placeBlock;
        this.searchRange = searchRange;
        this.canPlaceOnFloor = canPlaceOnFloor;
        this.canPlaceOnCeiling = canPlaceOnCeiling;
        this.canPlaceOnWall = canPlaceOnWall;
        this.chanceOfSpreading = chanceOfSpreading;
        this.canBePlacedOn = canBePlacedOn;
        this.validDirections = new ObjectArrayList<>(6);
        
        if (canPlaceOnCeiling) {
            this.validDirections.add(Direction.UP);
        }
        
        if (canPlaceOnFloor) {
            this.validDirections.add(Direction.DOWN);
        }
        
        if (canPlaceOnWall) {
            Direction.Plane horizontal = Direction.Plane.HORIZONTAL;
            horizontal.forEach(this.validDirections::add);
        }
    }
    
    /**
     * Get a shuffled list of valid directions except for a specific direction.
     *
     * @param random     The random object used for shuffling.
     * @param direction  The direction to exclude from the shuffled list.
     * @return A shuffled list of valid directions except for the specified direction.
     */
    public List<Direction> getShuffledDirectionsExcept(Random random, Direction direction) {
        return DirectionUtils.toShuffledList(this.validDirections.stream().filter(dir -> {
            return dir != direction;
        }), random);
    }
    
    /**
     * Get a shuffled list of all valid directions.
     *
     * @param random  The random object used for shuffling.
     * @return A shuffled list of all valid directions.
     */
    public List<Direction> getShuffledDirections(Random random) {
        return DirectionUtils.shuffledCopy(this.validDirections, random);
    }
}