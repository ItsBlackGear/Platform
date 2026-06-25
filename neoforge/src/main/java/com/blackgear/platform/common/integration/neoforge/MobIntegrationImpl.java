package com.blackgear.platform.common.integration.neoforge;

import com.blackgear.platform.common.integration.MobIntegration;
import com.blackgear.platform.common.integration.MobInteraction;
import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.util.EventBus;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MobIntegrationImpl {
    public static void registerIntegrations(Consumer<MobIntegration.Event> listener) {
        listener.accept(new MobIntegration.Event() {
            @Override
            public void registerMobInteraction(MobInteraction interaction) {
                EventBus.get(EventBus.LOADER).addListener((PlayerInteractEvent.EntityInteract event) -> {
                    InteractionResult result = interaction.onInteract(event.getEntity(), event.getTarget(), event.getHand());
                    if (result.consumesAction()) {
                        event.setCancellationResult(result);
                        event.setCanceled(true);
                    }
                });
            }

            @Override
            public void registerAttributes(Supplier<? extends EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> builder) {
                EventBus.get(EventBus.MOD).addListener((EntityAttributeCreationEvent event) -> event.put(type.get(), builder.get().build()));
            }

            @Override
            public <T extends Mob> void registerPlacement(Supplier<EntityType<T>> entity, SpawnPlacementType spawnPlacement, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
                EventBus.get(EventBus.MOD).addListener((RegisterSpawnPlacementsEvent event) -> {
                    if (entity.get() == EntityType.CAMEL) {
                        boolean hasNoMansLand = Environment.hasModLoaded("nomansland");
                        if (hasNoMansLand) return; // THIS IS A TEMPORAL FIX TO AVOID CONFLICT WITH NO MAN'S LAND, WHICH ALREADY REGISTERS THE CAMEL SPAWN PLACEMENT.
                    }

                    event.register(entity.get(), spawnPlacement, heightmap, spawnPredicate, RegisterSpawnPlacementsEvent.Operation.OR);
                });
            }
        });
    }
}