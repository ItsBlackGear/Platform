package com.blackgear.platform.common.entity;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EntityFactory {
    public static void registerSpawnPlacements(Consumer<EntityPlacementEvent> listener) {
        listener.accept(new EntityPlacementEvent() {
            @Override
            public <T extends Mob> void register(Supplier<EntityType<T>> entity, SpawnPlacements.Type spawnPlacement, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
                SpawnPlacements.register(entity.get(), spawnPlacement, heightmap, spawnPredicate);
            }
        });
    }

    @ExpectPlatform
    public static void registerMobAttributes(Consumer<EntityAttributesEvent> listener) {
        throw new AssertionError();
    }

    public interface EntityPlacementEvent {
        <T extends Mob> void register(Supplier<EntityType<T>> entity, SpawnPlacements.Type spawnPlacement, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> spawnPredicate);
    }

    public interface EntityAttributesEvent {
        void addAttributes(Supplier<? extends EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> builder);
    }
}