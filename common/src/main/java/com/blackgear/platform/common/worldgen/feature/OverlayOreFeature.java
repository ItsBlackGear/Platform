package com.blackgear.platform.common.worldgen.feature;

import com.blackgear.platform.common.worldgen.BulkSectionAccess;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

public class OverlayOreFeature extends Feature<OverlayOreConfiguration> {
    public OverlayOreFeature(Codec<OverlayOreConfiguration> codec) {
        super(codec);
    }
    
    @Override
    public boolean place(
        WorldGenLevel level,
        ChunkGenerator generator,
        Random random,
        BlockPos origin,
        OverlayOreConfiguration config
    ) {
        float angle = random.nextFloat() * (float) Math.PI;
        float sizeFactor = (float) config.size / 8.0F;
        // Calculate the radius of the ore placement area
        int radius = Mth.ceil(((float) config.size / 16.0F * 2.0F + 1.0F) / 2.0F);
        
        // Calculate the coordinate for the first and second placement point
        double x1 = (double) origin.getX() + Math.sin(angle) * (double) sizeFactor;
        double x2 = (double) origin.getX() - Math.sin(angle) * (double) sizeFactor;
        double z1 = (double) origin.getZ() + Math.cos(angle) * (double) sizeFactor;
        double z2 = (double) origin.getZ() - Math.cos(angle) * (double) sizeFactor;
        double y1 = origin.getY() + random.nextInt(3) - 2;
        double y2 = origin.getY() + random.nextInt(3) - 2;
        
        // Calculate the minimum coordinate for ore placement
        int minX = origin.getX() - Mth.ceil(sizeFactor) - radius;
        int minY = origin.getY() - 2 - radius;
        int minZ = origin.getZ() - Mth.ceil(sizeFactor) - radius;
        
        // Calculate the range of the placement area
        int rangeXZ = 2 * (Mth.ceil(sizeFactor) + radius);
        int rangeY = 2 * (2 + radius);
        
        // Iterate over the placement area
        for(int x = minX; x <= minX + rangeXZ; ++x) {
            for(int z = minZ; z <= minZ + rangeXZ; ++z) {
                if (minY <= level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z)) {
                    return this.doPlace(level, random, config, x1, x2, z1, z2, y1, y2, minX, minY, minZ, rangeXZ, rangeY);
                }
            }
        }
        
        return false;
    }
    
    private boolean doPlace(
        WorldGenLevel level,
        Random random,
        OverlayOreConfiguration config,
        double x1,
        double x2,
        double z1,
        double z2,
        double y1,
        double y2,
        int minX,
        int minY,
        int minZ,
        int rangeXZ,
        int rangeY
    ) {
        int placedBlocks = 0;
        BitSet visitedPositions = new BitSet(rangeXZ * rangeY * rangeXZ);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int size = config.size;
        double[] distances = new double[size * 4];
        
        // Calculate and store distance values for each point of the ore
        for(int index = 0; index < size; ++index) {
            float normal = (float) index / (float)size;
            double posX = Mth.lerp(normal, x1, x2);
            double posY = Mth.lerp(normal, y1, y2);
            double posZ = Mth.lerp(normal, z1, z2);
            double radius = random.nextDouble() * (double)size / 16.0;
            double weight = ((double) (Mth.sin((float)Math.PI * normal) + 1.0F) * radius + 1.0) / 2.0;
            distances[index * 4] = posX;
            distances[index * 4 + 1] = posY;
            distances[index * 4 + 2] = posZ;
            distances[index * 4 + 3] = weight;
        }
        
        // Check for overlapping points and mark one of them as invalid
        for(int p1 = 0; p1 < size - 1; ++p1) {
            if (distances[p1 * 4 + 3] > 0.0D) {
                for(int p2 = p1 + 1; p2 < size; ++p2) {
                    if (distances[p2 * 4 + 3] > 0.0D) {
                        double dx = distances[p1 * 4] - distances[p2 * 4];
                        double dy = distances[p1 * 4 + 1] - distances[p2 * 4 + 1];
                        double dz = distances[p1 * 4 + 2] - distances[p2 * 4 + 2];
                        double distSqr = distances[p1 * 4 + 3] - distances[p2 * 4 + 3];
                        if (distSqr * distSqr > dx * dx + dy * dy + dz * dz) {
                            if (distSqr > 0.0) {
                                distances[p2 * 4 + 3] = -1.0;
                            } else {
                                distances[p1 * 4 + 3] = -1.0;
                            }
                        }
                    }
                }
            }
        }
        
        BulkSectionAccess bulkSection = new BulkSectionAccess(level);
        
        try {
            // Iterate over each point of the ore
            for(int i = 0; i < size; ++i) {
                double weight = distances[i * 4 + 3];
                if (weight >= 0.0D) {
                    double x = distances[i * 4];
                    double y = distances[i * 4 + 1];
                    double z = distances[i * 4 + 2];
                    
                    // Calculate the bounding box of the current point
                    int startX = Math.max(Mth.floor(x - weight), minX);
                    int startY = Math.max(Mth.floor(y - weight), minY);
                    int startZ = Math.max(Mth.floor(z - weight), minZ);
                    int endX = Math.max(Mth.floor(x + weight), startX);
                    int endY = Math.max(Mth.floor(y + weight), startY);
                    int endZ = Math.max(Mth.floor(z + weight), startZ);
                    
                    // Iterate over the volume defined by the bounding box of the point
                    for(int posX = startX; posX <= endX; ++posX) {
                        double dx = ((double) posX + 0.5D - x) / weight;
                        if (dx * dx < 1.0D) {
                            for(int posY = startY; posY <= endY; ++posY) {
                                double dy = ((double) posY + 0.5D - y) / weight;
                                if (dx * dx + dy * dy < 1.0D) {
                                    for(int posZ = startZ; posZ <= endZ; ++posZ) {
                                        double dz = ((double) posZ + 0.5D - z) / weight;
                                        if (dx * dx + dy * dy + dz * dz < 1.0D && !Level.isOutsideBuildHeight(posY)) {
                                            int index = posX - minX + (posY - minY) * rangeXZ + (posZ - minZ) * rangeXZ * rangeY;
                                            
                                            // Check if this position has been visited before
                                            if (!visitedPositions.get(index)) {
                                                visitedPositions.set(index);
                                                mutable.set(posX, posY, posZ);
                                                LevelChunkSection chunkSection = bulkSection.getSection(mutable);
                                                
                                                // Check if the chunk section exists
                                                if (chunkSection != null) {
                                                    int sectionX = SectionPos.sectionRelative(posX);
                                                    int sectionY = SectionPos.sectionRelative(posY);
                                                    int sectionZ = SectionPos.sectionRelative(posZ);
                                                    BlockState state = chunkSection.getBlockState(sectionX, sectionY, sectionZ);
                                                    
                                                    // Iterate over the target block states
                                                    for (OverlayOreConfiguration.TargetBlockState target : config.targets) {
                                                        // Check if the ore can be placed at this position
                                                        if (this.canPlaceOre(state, bulkSection::getBlockState, random, config, target, mutable)) {
                                                            chunkSection.setBlockState(sectionX, sectionY, sectionZ, target.state, false);
                                                            placedBlocks++;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable throwable) {
            try {
                // Attempt to close the bulkSection to release any allocated resources
                bulkSection.close();
            } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
            }
            
            // Rethrow the original exception to propagate it up the call stack
            throw throwable;
        }
        
        // Close the bulkSection to release any allocated resources
        bulkSection.close();
        return placedBlocks > 0;
    }
    
    private boolean canPlaceOre(
        BlockState state,
        Function<BlockPos, BlockState> stateByPos,
        Random random,
        OverlayOreConfiguration config,
        OverlayOreConfiguration.TargetBlockState target,
        BlockPos.MutableBlockPos mutable
    ) {
        if (!target.target.test(state, random)) {
            return false;
        } else if (this.shouldSkipAirCheck(random, config.discardChanceOnAirExposure)) {
            return true;
        } else {
            return !this.isAdjacentToAir(stateByPos, mutable);
        }
    }
    
    private boolean shouldSkipAirCheck(Random random, float discardChanceOnAirExposure) {
        if (discardChanceOnAirExposure <= 0.0F) {
            return true;
        } else if (discardChanceOnAirExposure >= 1.0F) {
            return false;
        } else {
            return random.nextFloat() >= discardChanceOnAirExposure;
        }
    }
    
    private boolean isAdjacentToAir(Function<BlockPos, BlockState> stateByPos, BlockPos pos) {
        return this.checkNeighbors(stateByPos, pos, BlockBehaviour.BlockStateBase::isAir);
    }
    
    private boolean checkNeighbors(Function<BlockPos, BlockState> stateByPos, BlockPos pos, Predicate<BlockState> state) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.values()) {
            mutable.setWithOffset(pos, direction);
            if (state.test(stateByPos.apply(mutable))) {
                return true;
            }
        }
        
        return false;
    }
}