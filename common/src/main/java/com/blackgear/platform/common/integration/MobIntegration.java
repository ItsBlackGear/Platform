package com.blackgear.platform.common.integration;

import com.blackgear.platform.common.events.EntityEvents;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MobIntegration {
    @ExpectPlatform
    public static void registerIntegrations(Consumer<Event> listener) {
        throw new AssertionError();
    }

    public interface Event {
        void registerAttributes(Supplier<? extends EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> builder);

        default <T extends Mob> void registerPlacement(Supplier<EntityType<T>> entity, SpawnPlacements.Type spawnPlacement, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
            SpawnPlacements.register(entity.get(), spawnPlacement, heightmap, spawnPredicate);
        }

        default void addGoal(Selector selector, Predicate<Mob> predicate, int priority, Function<Mob, Goal> factory) {
            EntityEvents.ON_SPAWN.register((entity, level) -> {
                if (entity instanceof Mob mob && predicate.test(mob)) {
                    GoalSelector goalSelector = selector == Selector.GOAL ? mob.goalSelector : mob.targetSelector;
                    goalSelector.addGoal(priority, factory.apply(mob));
                }

                return true;
            });
        }

        default void addGoal(Selector selector, EntityType<?> entity, int priority, Function<Mob, Goal> factory) {
            addGoal(selector, mob -> mob.getType() == entity, priority, factory);
        }

        default void removeGoal(Selector selector, Predicate<Mob> predicate, Function<Mob, Goal> factory) {
            EntityEvents.ON_SPAWN.register((entity, level) -> {
                if (entity instanceof Mob mob && predicate.test(mob)) {
                    GoalSelector goalSelector = selector == Selector.GOAL ? mob.goalSelector : mob.targetSelector;
                    goalSelector.removeGoal(factory.apply(mob));
                }

                return true;
            });
        }

        default void removeGoal(Selector selector, EntityType<?> entity, Function<Mob, Goal> factory) {
            removeGoal(selector, mob -> mob.getType() == entity, factory);
        }

        enum Selector {
            GOAL, TARGET
        }
    }
}