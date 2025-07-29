package com.blackgear.platform.client.event.screen;

import com.blackgear.platform.core.util.event.Event;
import com.blackgear.platform.core.util.event.CancellableResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

@Environment(EnvType.CLIENT)
public interface HudInteractions {
    Event<MouseScroll> SCROLLING_PRE = Event.cancellable(MouseScroll.class);
    Event<MouseScroll> SCROLLING_POST = Event.cancellable(MouseScroll.class);

    Event<MouseClick> CLICKING_PRE = Event.cancellable(MouseClick.class);
    Event<MouseClick> CLICKING_POST = Event.cancellable(MouseClick.class);

    Event<MouseRelease> RELEASING_PRE = Event.cancellable(MouseRelease.class);
    Event<MouseRelease> RELEASING_POST = Event.cancellable(MouseRelease.class);

    Event<SlotClicked> SLOT_CLICK = Event.create(SlotClicked.class);
    Event<StopHovering> STOP_HOVERING = Event.create(StopHovering.class);

    interface SlotClicked {
        void onMouseClick(Minecraft client, AbstractContainerScreen<?> screen, Slot slot, ClickType clickType);
    }

    interface StopHovering {
        void onStopHovering(Minecraft client, AbstractContainerScreen<?> screen, Slot slot);
    }

    interface MouseScroll {
        CancellableResult onScrolling(Minecraft client, Screen screen, double mouseX, double mouseY, double delta);
    }

    interface MouseClick {
        CancellableResult onClicking(Minecraft client, Screen screen, double mouseX, double mouseY, int button);
    }

    interface MouseRelease {
        CancellableResult onReleasing(Minecraft client, Screen screen, double mouseX, double mouseY, int button);
    }
}