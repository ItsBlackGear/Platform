package com.blackgear.platform.common.worldgen;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for accessing and managing sections of a level in bulk.
 */
public final class BulkSectionAccess implements AutoCloseable {
    private final LevelAccessor level;
    private final Long2ObjectMap<LevelChunkSection> acquiredSections = new Long2ObjectOpenHashMap<>();
    @Nullable private LevelChunkSection lastSection;
    private long lastSectionKey;
    
    public BulkSectionAccess(LevelAccessor level) {
        this.level = level;
    }
    
    /**
     * Retrieves the section at the specified block position.
     *
     * @return The level chunk section at the specified position, or null if not found.
     */
    @Nullable
    public LevelChunkSection getSection(BlockPos pos) {
        int sectionY = SectionPos.blockToSectionCoord(pos.getY());
        if (isSectionIndexValid(sectionY)) {
            long sectionKey = SectionPos.asLong(SectionPos.blockToSectionCoord(pos.getX()), sectionY, SectionPos.blockToSectionCoord(pos.getZ()));
            if (this.lastSection == null || this.lastSectionKey != sectionKey) {
                this.lastSection = this.acquiredSections.computeIfAbsent(sectionKey, key -> {
                    ChunkAccess chunk = this.level.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
                    LevelChunkSection section = ((ProtoChunk) chunk).getOrCreateSection(sectionY);
                    section.acquire();
                    return section;
                });
                this.lastSectionKey = sectionKey;
            }
            
            return this.lastSection;
        } else {
            return LevelChunk.EMPTY_SECTION;
        }
    }
    
    private boolean isSectionIndexValid(int sectionY) {
        return sectionY >= 0 && sectionY < (SectionPos.blockToSectionCoord(this.level.getMaxBuildHeight() - 1) + 1);
    }
    
    /**
     * Retrieves the block state at the specified block position.
     *
     * @return The block state at the specified position, or AIR if not found.
     */
    public BlockState getBlockState(BlockPos pos) {
        LevelChunkSection section = this.getSection(pos);
        if (section == null) {
            return Blocks.AIR.defaultBlockState();
        } else {
            int x = SectionPos.sectionRelative(pos.getX());
            int y = SectionPos.sectionRelative(pos.getY());
            int z = SectionPos.sectionRelative(pos.getZ());
            return section.getBlockState(x, y, z);
        }
    }
    
    /**
     * Releases acquired sections and cleans up resources.
     */
    @Override
    public void close() {
        for (LevelChunkSection section : this.acquiredSections.values()) {
            section.release();
        }
    }
}