package com.blackgear.platform.client.forge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.client.FogRenderingHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(
    modid = Platform.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE,
    value = Dist.CLIENT
)
public class FogRenderingHandlerImpl {
    private static final Set<Consumer<EntityViewRenderEvent.FogDensity>> DENSITIES = ConcurrentHashMap.newKeySet();
    private static final Set<Consumer<EntityViewRenderEvent.RenderFogEvent>> RENDERERS = ConcurrentHashMap.newKeySet();
    private static final Set<Consumer<EntityViewRenderEvent.FogColors>> COLORS = ConcurrentHashMap.newKeySet();
    
    @SubscribeEvent
    public static void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        DENSITIES.forEach(consumer -> consumer.accept(event));
    }
    
    public static void addFogDensity(FogRenderingHandler.FogDensity density) {
        DENSITIES.add(event -> {
            FogRenderingHandler.FogDensityContext context = new FogRenderingHandler.FogDensityContext(event.getInfo(), event.getType());
            FogRenderingHandler.FogDensityContext mutable = density.setupDensity(context);
            
            if (mutable != null) {
                event.setDensity(mutable.getDensity());
                event.setCanceled(mutable.isCancellable());
            }
        });
    }
    
    @SubscribeEvent
    public static void onFogRendering(EntityViewRenderEvent.RenderFogEvent event) {
        RENDERERS.forEach(consumer -> consumer.accept(event));
    }
    
    public static void addFogRendering(FogRenderingHandler.FogRendering rendering) {
        RENDERERS.add(event -> {
            FogRenderingHandler.FogRenderingContext context = new FogRenderingHandler.FogRenderingContext(event.getInfo(), event.getType(), event.getFarPlaneDistance());
            rendering.setupRendering(context);
        });
    }
    
    @SubscribeEvent
    public static void onFogColor(EntityViewRenderEvent.FogColors event) {
        COLORS.forEach(consumer -> consumer.accept(event));
    }
    
    public static void addFogColor(FogRenderingHandler.FogColor color) {
        COLORS.add(event -> {
            FogRenderingHandler.FogColorContext context = new FogRenderingHandler.FogColorContext(event.getInfo(), event.getRed(), event.getGreen(), event.getBlue());
            FogRenderingHandler.FogColorContext mutable = color.setupColor(context);
            if (mutable != null) {
                event.setRed(mutable.getRed());
                event.setGreen(mutable.getGreen());
                event.setBlue(mutable.getBlue());
            }
        });
    }
}
