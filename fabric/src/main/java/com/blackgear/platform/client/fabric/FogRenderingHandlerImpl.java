package com.blackgear.platform.client.fabric;

import com.blackgear.platform.client.FogRenderingHandler.*;
import com.blackgear.platform.core.util.event.Event;

public class FogRenderingHandlerImpl {
    public static final Event<FogDensity> DENSITY_HANDLER = Event.create(
        FogDensity.class,
        listener -> context -> {
            for (FogDensity density : listener) {
                FogDensityContext mutable = density.setupDensity(context);

                if (mutable != null) {
                    return mutable;
                }
            }

            return null;
        }
    );
    public static final Event<FogRendering> RENDERING_HANDLER = Event.create(
        FogRendering.class,
        listener -> context -> {
            for (FogRendering renderer : listener) {
                FogRenderingContext mutable = renderer.setupRendering(context);

                if (mutable != null) {
                    return mutable;
                }
            }

            return null;
        }
    );
    public static final Event<FogColor> COLOR_HANDLER = Event.create(
        FogColor.class,
        listener -> context -> {
            for (FogColor color : listener) {
                FogColorContext mutable = color.setupColor(context);

                if (mutable != null) {
                    return mutable;
                }
            }

            return null;
        }
    );
    
    public static void addFogDensity(FogDensity density) {
        DENSITY_HANDLER.register(density);
    }
    
    public static void addFogRendering(FogRendering rendering) {
        RENDERING_HANDLER.register(rendering);
    }
    
    public static void addFogColor(FogColor color) {
        COLOR_HANDLER.register(color);
    }
}