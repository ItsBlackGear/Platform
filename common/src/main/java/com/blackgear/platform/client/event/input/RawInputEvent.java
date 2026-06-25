package com.blackgear.platform.client.event.input;

import com.blackgear.platform.core.util.event.CancellableResult;
import com.blackgear.platform.core.util.event.Event;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public interface RawInputEvent {
    Event<KeyPress> ON_KEY_PRESS = Event.cancellable(KeyPress.class);
    Event<MouseScroll> ON_MOUSE_SCROLL = Event.cancellable(MouseScroll.class);

    interface KeyPress {
        CancellableResult handle(Minecraft minecraft, int keyCode, int scanCode, int action, int modifiers);
    }

    interface MouseScroll {
        CancellableResult handle(Minecraft minecraft, double amountY);
    }
}