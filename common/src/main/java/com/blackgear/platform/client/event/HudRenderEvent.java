package com.blackgear.platform.client.event;

import com.blackgear.platform.core.util.event.Event;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;

public class HudRenderEvent {
    public static final Event<RenderHud> RENDER_HUD = Event.create(RenderHud.class);
    
    @FunctionalInterface
    public interface RenderHud {
        void render(GuiGraphics matrices, float tickDelta, ElementType type, RenderContext context);
    }
    
    public interface RenderContext {
        Window getWindow();
        
        int getScreenWidth();
        
        int getScreenHeight();
    }
    
    public enum ElementType {
        DEFAULT, HEALTH, EXPERIENCE, FIRST_PERSON, VIGNETTE
    }
}