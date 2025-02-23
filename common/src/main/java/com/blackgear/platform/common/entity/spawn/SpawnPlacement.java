package com.blackgear.platform.common.entity.spawn;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Utility class for registering or replacing spawn placements for entities.
 */
public final class SpawnPlacement {
    /**
     * Registers or replaces a spawn placement for an entity.
     *
     * <br>
     *
     * <p>Example of registering a SpawnPlacement for an entity</p>
     *
     * <pre> {@code
     * SpawnPlacement.register(
     *     ModEntities.CUSTOM_MOB,
     *     SpawnPlacements.Type.ON_GROUND,
     *     Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
     *     CustomMob::checkMobSpawnRules
     * );
     * } </pre>
     *
     * @param entity         The entity type supplier.
     * @param placement      The spawn placement type.
     * @param heightmap      The heightmap type.
     * @param spawnPredicate The spawn predicate.
     * @param <T>            The type of the entity.
     */
    public static <T extends Mob> void register(
        Supplier<EntityType<T>> entity,
        SpawnPlacements.Type placement,
        Heightmap.Types heightmap,
        SpawnPlacements.SpawnPredicate<T> spawnPredicate
    ) {
        SpawnPlacements.Data data = new SpawnPlacements.Data(
            heightmap,
            placement,
            spawnPredicate
        );
        
        registerOrReplaceEntityData(entity, data);
    }
    
    /**
     * Registers or replaces a spawn placement for an entity using a custom spawn placement type.
     *
     * <br>
     *
     * <p>Example of registering a SpawnPlacement for an entity</p>
     *
     * <pre> {@code
     * SpawnPlacement.register(
     *     ModEntities.CUSTOM_MOB,
     *     (level, pos, entity) -> level.getBlockState(pos).is(Blocks.DIRT),
     *     Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
     *     CustomMob::checkMobSpawnRules
     * );
     * } </pre>
     *
     * @param entity         The entity type supplier.
     * @param placement      The spawn placement type.
     * @param heightmap      The heightmap type.
     * @param spawnPredicate The spawn predicate.
     * @param <T>            The type of the entity.
     */
    public static <T extends Mob> void register(
        Supplier<EntityType<T>> entity,
        SpawnPlacementType placement,
        Heightmap.Types heightmap,
        SpawnPlacements.SpawnPredicate<T> spawnPredicate
    ) {
        SpawnPlacements.Data data = new SpawnPlacements.Data(
            heightmap,
            SpawnPlacements.Type.NO_RESTRICTIONS,
            (entityType, level, spawnType, pos, random) -> {
                return placement.isSpawnPositionValid(level, pos, entityType) && spawnPredicate.test(entity.get(), level, spawnType, pos, random);
            }
        );
        
        registerOrReplaceEntityData(entity, data);
    }
    
    private static <T extends Mob> void registerOrReplaceEntityData(Supplier<EntityType<T>> entity, SpawnPlacements.Data data) {
        SpawnPlacements.Data entry = SpawnPlacements.DATA_BY_TYPE.put(entity.get(), data);

        if (entry != null) {
            SpawnPlacements.DATA_BY_TYPE.replace(entity.get(), data);
        }
    }
    
    /**
     * Interface for defining custom spawn placement types.
     */
    public interface SpawnPlacementType {
        /**
         * Checks if the spawn position is valid for the given entity type.
         *
         * @param level  The level reader.
         * @param pos    The block position.
         * @param entity The entity type.
         * @return True if the spawn position is valid.
         */
        boolean isSpawnPositionValid(LevelReader level, BlockPos pos, @Nullable EntityType<?> entity);
    }
}