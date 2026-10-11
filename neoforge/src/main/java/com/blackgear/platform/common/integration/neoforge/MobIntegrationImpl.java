package com.blackgear.platform.common.integration.neoforge;

import com.blackgear.platform.common.integration.MobIntegration;
import com.blackgear.platform.common.integration.MobInteraction;
import com.blackgear.platform.common.integration.v2.spawn_placement.SpawnPlacementStrategy;
import com.blackgear.platform.neoforge.CommonLoaderPipelines;
import com.blackgear.platform.neoforge.CommonModPipelines;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class MobIntegrationImpl {
    public static void registerIntegrations(Consumer<MobIntegration.Event> listener) {
        listener.accept(ForgeMobIntegrations.INSTANCE);
    }
    
    static class ForgeMobIntegrations implements MobIntegration.Event {
        public static final ForgeMobIntegrations INSTANCE = new ForgeMobIntegrations();
        
        @Override
        public void registerMobInteraction(MobInteraction interaction) {
            CommonLoaderPipelines.MOB_INTERACTION.add(event -> {
                InteractionResult result = interaction.onInteract(event.getEntity(), event.getTarget(), event.getHand());
                if (result.consumesAction()) {
                    event.setCancellationResult(result);
                    event.setCanceled(true);
                }
            });
        }
        
        @Override
        public void registerAttributes(Supplier<? extends EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> builder) {
            CommonModPipelines.MOB_ATTRIBUTE.add(event -> {
                if (!DefaultAttributes.hasSupplier(type.get())) event.put(type.get(), builder.get().build());
            });
        }
        
        @Override
        public <T extends Mob> void registerPlacement(
            Supplier<EntityType<T>> entity,
            SpawnPlacementType spawnPlacement,
            Heightmap.Types heightmap,
            SpawnPlacements.SpawnPredicate<T> spawnPredicate,
            SpawnPlacementStrategy strategy
        ) {
            CommonModPipelines.SPAWN_PLACEMENT.add(event -> event.register(entity.get(), spawnPlacement, heightmap, spawnPredicate, Operation.valueOf(strategy.name())));
        }
    }
}