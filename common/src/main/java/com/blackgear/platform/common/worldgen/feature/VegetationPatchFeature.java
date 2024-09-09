package com.blackgear.platform.common.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

public class VegetationPatchFeature extends Feature<VegetationPatchConfiguration> {
    public VegetationPatchFeature(Codec<VegetationPatchConfiguration> codec) {
        super(codec);
    }
    
    @Override
    public boolean place(
        WorldGenLevel level,
        ChunkGenerator generator,
        Random random,
        BlockPos origin,
        VegetationPatchConfiguration config
    ) {
        Predicate<BlockState> replaceableBlocks = this.getReplaceableBlocks(config);
        int xRadius = config.xzRadius.sample(random) + 1;
        int zRadius = config.xzRadius.sample(random) + 1;
        Set<BlockPos> possiblePositions = this.placeGroundPatch(level, config, random, origin, replaceableBlocks, xRadius, zRadius);
        this.distributeVegetation(level, config, random, possiblePositions, generator);
        return !possiblePositions.isEmpty();
    }
    
    protected Set<BlockPos> placeGroundPatch(
        WorldGenLevel level,
        VegetationPatchConfiguration config,
        Random random,
        BlockPos pos,
        Predicate<BlockState> replaceableBlocks,
        int xRadius,
        int zRadius
    ) {
        BlockPos.MutableBlockPos currentPos = pos.mutable();
        BlockPos.MutableBlockPos nextPos = currentPos.mutable();
        Direction direction = config.surface.getDirection();
        Direction oppositeDirection = direction.getOpposite();
        Set<BlockPos> possiblePositions = new HashSet<>();
        
        for (int x = -xRadius; x <= xRadius; x++) {
            boolean isXEdge = x == -xRadius || x == xRadius;
            
            for (int z = -zRadius; z <= zRadius; z++) {
                boolean isZEdge = z == -zRadius || z == zRadius;
                boolean isEdge = isXEdge || isZEdge;
                boolean isCorner = isXEdge && isZEdge;
                boolean isEdgeNotCorner = isEdge && !isCorner;
                
                if (!isCorner && (!isEdgeNotCorner || config.extraEdgeColumnChance != 0.0F && random.nextFloat() <= config.extraEdgeColumnChance)) {
                    currentPos.setWithOffset(pos, x, 0, z);
                    
                    for (int y = 0; level.isStateAtPosition(currentPos, BlockBehaviour.BlockStateBase::isAir) && y < config.verticalRange; y++) {
                        currentPos.move(direction);
                    }
                    
                    for (int y = 0; level.isStateAtPosition(currentPos, state -> !state.isAir()) && y < config.verticalRange; y++) {
                        currentPos.move(oppositeDirection);
                    }
                    
                    nextPos.setWithOffset(currentPos, config.surface.getDirection());
                    BlockState nextState = level.getBlockState(nextPos);
        
                    if (level.isEmptyBlock(currentPos) && nextState.isFaceSturdy(level, nextPos, config.surface.getDirection().getOpposite())) {
                        int depth = config.depth.sample(random) + (config.extraBottomBlockChance > 0.0F && random.nextFloat() < config.extraBottomBlockChance ? 1 :0);
                        BlockPos position = nextPos.immutable();
                        boolean isPlaced = this.placeGround(level, config, replaceableBlocks, random, nextPos, depth);
        
                        if (isPlaced) {
                            possiblePositions.add(position);
                        }
                    }
                }
            }
        }
        
        return possiblePositions;
    }
    
    protected void distributeVegetation(
        WorldGenLevel level,
        VegetationPatchConfiguration config,
        Random random,
        Set<BlockPos> possiblePositions,
        ChunkGenerator generator
    ) {
        for (BlockPos pos : possiblePositions) {
            if (config.vegetationChance > 0.0F && random.nextFloat() < config.vegetationChance) {
                this.placeVegetation(level, config, generator, random, pos);
            }
        }
    }
    
    protected boolean placeVegetation(
        WorldGenLevel level,
        VegetationPatchConfiguration config,
        ChunkGenerator generator,
        Random random,
        BlockPos pos
    ) {
        return config.vegetationFeature.get().place(level, generator, random, pos.relative(config.surface.getDirection().getOpposite()));
    }
    
    protected boolean placeGround(
        WorldGenLevel level,
        VegetationPatchConfiguration config,
        Predicate<BlockState> replaceableBlocks,
        Random random,
        BlockPos.MutableBlockPos mutable,
        int maxDistance
    ) {
        for (int i = 0; i < maxDistance; i++) {
            BlockState groundState = config.groundState.getState(random, mutable);
            BlockState currentState = level.getBlockState(mutable);
            if (!groundState.is(currentState.getBlock())) {
                if (!replaceableBlocks.test(currentState)) {
                    return i != 0;
                }
                
                level.setBlock(mutable, groundState, 2);
                mutable.move(config.surface.getDirection());
            }
        }
        
        return true;
    }
    
    private Predicate<BlockState> getReplaceableBlocks(VegetationPatchConfiguration config) {
        Tag<Block> tag = BlockTags.getAllTags().getTag(config.replaceable);
        return tag == null ? state -> true : state -> state.is(tag);
    }
}