package com.blackgear.platform.common.entity;

import com.blackgear.platform.common.integration.MobIntegration;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Deprecated(forRemoval = true)
public class EntityFactory {
    public static void registerSpawnPlacements(Consumer<EntityPlacementEvent> listener) {
        listener.accept(new EntityPlacementEvent() {});
    }

    public static void registerMobAttributes(Consumer<EntityAttributesEvent> listener) {
        listener.accept((type, builder) -> MobIntegration.registerIntegrations(event -> event.registerAttributes(type, builder)));
    }

    public interface EntityPlacementEvent {
        default <T extends Mob> void register(Supplier<EntityType<T>> entity, SpawnPlacements.Type spawnPlacement, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
            SpawnPlacements.register(entity.get(), spawnPlacement, heightmap, spawnPredicate);
        }
    }

    public interface EntityAttributesEvent {
        void register(Supplier<? extends EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> builder);
    }
}