package com.blackgear.platform.client.fabric;

import com.blackgear.platform.client.FogRenderingHandler.*;
import com.blackgear.platform.core.util.event.Event;
import com.blackgear.platform.core.util.event.EventFactory;

public class FogRenderingHandlerImpl {
    public static final Event<FogRendering> RENDERING = EventFactory.create(FogRendering.class);
    public static final Event<FogColor> COLOR = EventFactory.create(FogColor.class);

    public static void addFogRendering(FogRendering rendering) {
        RENDERING.register(rendering);
    }
    
    public static void addFogColor(FogColor color) {
        COLOR.register(color);
    }
}