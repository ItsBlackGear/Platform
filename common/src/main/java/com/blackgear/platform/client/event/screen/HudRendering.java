package com.blackgear.platform.client.event.screen;

import com.blackgear.platform.client.event.screen.api.ScreenAccess;
import com.blackgear.platform.core.util.event.Event;
import com.blackgear.platform.core.util.event.CancellableResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

@Environment(EnvType.CLIENT)
public interface HudRendering {
    Event<Rendering> RENDERING = Event.create(Rendering.class);
    Event<ScreenPreInitialization> PRE_INITIALIZE = Event.cancellable(ScreenPreInitialization.class);
    Event<ScreenPostInitialization> POST_INITIALIZE = Event.create(ScreenPostInitialization.class);
    Event<ScreenPreRendering> PRE_RENDERING = Event.cancellable(ScreenPreRendering.class);
    Event<ScreenPostRendering> POST_RENDERING = Event.create(ScreenPostRendering.class);
    Event<ContainerRenderBackground> RENDER_BACKGROUND = Event.create(ContainerRenderBackground.class);
    Event<ContainerRenderForeground> RENDER_FOREGROUND = Event.create(ContainerRenderForeground.class);
    Event<OpenContainer> OPEN_CONTAINER = Event.create(OpenContainer.class);
    Event<CloseContainer> CLOSE_CONTAINER = Event.create(CloseContainer.class);

    interface Rendering {
        void onRender(Minecraft client, GuiGraphics graphics, DeltaTracker tracker);
    }

    interface ScreenPreInitialization {
        CancellableResult onInitialize(Minecraft client, Screen screen, ScreenAccess access);
    }

    interface ScreenPostInitialization {
        void onInitialize(Minecraft client, Screen screen, ScreenAccess access);
    }

    interface ScreenPreRendering {
        CancellableResult onRender(Minecraft client, Screen screen, GuiGraphics graphics, int mouseX, int mouseY, DeltaTracker tracker);
    }

    interface ScreenPostRendering {
        void onRender(Minecraft client, Screen screen, GuiGraphics graphics, int mouseX, int mouseY, DeltaTracker tracker);
    }

    interface ContainerRenderBackground {
        void onRender(Minecraft client, AbstractContainerScreen<?> screen, GuiGraphics graphics, int mouseX, int mouseY, DeltaTracker tickDelta);
    }

    interface ContainerRenderForeground {
        void onRender(Minecraft client, AbstractContainerScreen<?> screen, GuiGraphics graphics, int mouseX, int mouseY, DeltaTracker tickDelta);
    }

    interface OpenContainer {
        void onOpen(Minecraft client, Screen screen);
    }

    interface CloseContainer {
        void onClose(Minecraft client, Screen screen);
    }
}