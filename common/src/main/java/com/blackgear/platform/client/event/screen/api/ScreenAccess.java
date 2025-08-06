package com.blackgear.platform.client.event.screen.api;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;

import java.util.List;

public interface ScreenAccess {
    Screen screen();

    List<NarratableEntry> narratables();

    List<Renderable> renderables();

    <T extends AbstractWidget & Renderable & NarratableEntry> T addRenderableWidget(T widget);

    <T extends Renderable> T addRenderableOnly(T listener);

    <T extends GuiEventListener & NarratableEntry> T addWidget(T listener);
}