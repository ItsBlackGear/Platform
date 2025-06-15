package com.blackgear.platform.common.integration.forge;

import com.blackgear.platform.common.integration.MobIntegration;
import com.blackgear.platform.common.integration.MobInteraction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MobIntegrationImpl {
    public static void registerIntegrations(Consumer<MobIntegration.Event> listener) {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MobIntegration.Event integration = new MobIntegration.Event() {
            @Override
            public void registerMobInteraction(MobInteraction interaction) {
                MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.EntityInteract event) -> {
                    InteractionResult result = interaction.onInteract(event.getEntity(), event.getTarget(), event.getHand());
                    if (result != InteractionResult.PASS) {
                        event.setCanceled(true);
                        event.setCancellationResult(result);
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