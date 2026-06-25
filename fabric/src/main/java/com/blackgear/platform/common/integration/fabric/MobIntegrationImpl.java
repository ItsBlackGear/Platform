package com.blackgear.platform.common.integration.fabric;

import com.blackgear.platform.common.integration.MobIntegration;
import com.blackgear.platform.common.integration.MobInteraction;
import com.blackgear.platform.core.mixin.access.SpawnPlacementsAccessor;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MobIntegrationImpl {
    public static void registerIntegrations(Consumer<MobIntegration.Event> listener) {
        listener.accept(new MobIntegration.Event() {
            @Override
            public void registerMobInteraction(MobInteraction interaction) {
                UseEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
                    InteractionResult result = interaction.onInteract(player, entity, hand);
                    if (result.consumesAction() && result.shouldSwing()) player.swing(hand);
                    return result;
                });
            }

            @Override
            public void registerAttributes(Supplier<? extends EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> builder) {
                FabricDefaultAttributeRegistry.register(type.get(), builder.get());
            }

            @Override
            public <T extends Mob> void registerPlacement(Supplier<EntityType<T>> entity, SpawnPlacements.Type spawnPlacement, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
                if (SpawnPlacementsAccessor.getDATA_BY_TYPE().get(entity.get()) != null) {
                    SpawnPlacementsAccessor.getDATA_BY_TYPE().put(entity.get(), new SpawnPlacements.Data(heightmap, spawnPlacement, spawnPredicate));
                } else {
                    SpawnPlacements.register(entity.get(), spawnPlacement, heightmap, spawnPredicate);
                }
            }
        });
    }
}
