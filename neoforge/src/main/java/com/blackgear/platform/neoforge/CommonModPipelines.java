package com.blackgear.platform.neoforge;

import com.blackgear.platform.Platform;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = Platform.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class CommonModPipelines {
    // MOB PIPELINES
    public static final EventPipeline<EntityAttributeCreationEvent> MOB_ATTRIBUTE = new EventPipeline<>();
    public static final EventPipeline<RegisterSpawnPlacementsEvent> SPAWN_PLACEMENT = new EventPipeline<>();
    
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        MOB_ATTRIBUTE.dispatch(event);
    }
    
    @SubscribeEvent
    public static void onSpawnPlacementRegister(RegisterSpawnPlacementsEvent event) {
        SPAWN_PLACEMENT.dispatch(event);
    }
}