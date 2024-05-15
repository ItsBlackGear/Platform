package com.blackgear.platform.client.forge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.client.FogRenderingHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
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
    private static final Set<Consumer<ViewportEvent.RenderFog>> RENDERERS = ConcurrentHashMap.newKeySet();
    private static final Set<Consumer<ViewportEvent.ComputeFogColor>> COLORS = ConcurrentHashMap.newKeySet();
    
    @SubscribeEvent
    public static void onFogRendering(ViewportEvent.RenderFog event) {
        RENDERERS.forEach(consumer -> consumer.accept(event));
    }
    
    public static void addFogRendering(FogRenderingHandler.FogRendering rendering) {
        RENDERERS.add(event -> {
            FogRenderingHandler.FogRenderingContext context = new FogRenderingHandler.FogRenderingContext(event.getCamera(), event.getMode(), event.getFarPlaneDistance());
            rendering.setupRendering(context);
        });
    }
    
    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        COLORS.forEach(consumer -> consumer.accept(event));
    }
    
    public static void addFogColor(FogRenderingHandler.FogColor color) {
        COLORS.add(event -> {
            FogRenderingHandler.FogColorContext context = new FogRenderingHandler.FogColorContext(event.getCamera(), event.getRed(), event.getGreen(), event.getBlue());
            FogRenderingHandler.FogColorContext mutable = color.setupColor(context);
            if (mutable != null) {
                event.setRed(mutable.getRed());
                event.setGreen(mutable.getGreen());
                event.setBlue(mutable.getBlue());
            }
        });
    }
}
