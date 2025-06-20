package com.blackgear.platform.common.integration.neoforge;

import com.blackgear.platform.common.integration.MobIntegration;
import com.blackgear.platform.common.integration.MobInteraction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MobIntegrationImpl {
    public static void registerIntegrations(Consumer<MobIntegration.Event> listener) {
        IEventBus bus = ModLoadingContext.get().getActiveContainer().getEventBus();
        MobIntegration.Event integration = new MobIntegration.Event() {
            @Override
            public void registerMobInteraction(MobInteraction interaction) {
                NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.EntityInteract event) -> {
                    if (event.getTarget() instanceof LivingEntity entity) {
                        InteractionResult result = interaction.onInteract(event.getEntity(), entity, event.getHand());
                        if (result.consumesAction()) {
                            event.setCancellationResult(result);
                            event.setCanceled(true);
                        }
                    }
                });
            }

            @Override
            public void registerAttributes(Supplier<? extends EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> builder) {
                bus.addListener((EntityAttributeCreationEvent event) -> event.put(type.get(), builder.get().build()));
            }
        };
        listener.accept(integration);
    }
}