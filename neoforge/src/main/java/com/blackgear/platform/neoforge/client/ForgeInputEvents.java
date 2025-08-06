package com.blackgear.platform.neoforge.client;

import com.blackgear.platform.Platform;
import com.blackgear.platform.client.event.screen.HudInteractions;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(
    modid = Platform.MOD_ID,
    bus = EventBusSubscriber.Bus.GAME,
    value = Dist.CLIENT
)
public class ForgeInputEvents {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onMouseScrollPre(ScreenEvent.MouseScrolled.Pre event) {
        if (HudInteractions.SCROLLING_PRE.invoker().onScrolling(Minecraft.getInstance(), event.getScreen(), event.getMouseX(), event.getMouseY(), event.getScrollDeltaX(), event.getScrollDeltaY()).isCancelled()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onMouseScrollPost(ScreenEvent.MouseScrolled.Post event) {
        HudInteractions.SCROLLING_POST.invoker().onScrolling(Minecraft.getInstance(), event.getScreen(), event.getMouseX(), event.getMouseY(), event.getScrollDeltaX(), event.getScrollDeltaY());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onMouseClickPre(ScreenEvent.MouseButtonPressed.Pre event) {
        if (HudInteractions.CLICKING_PRE.invoker().onClicking(Minecraft.getInstance(), event.getScreen(), event.getMouseX(), event.getMouseY(), event.getButton()).isCancelled()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onMouseClickPost(ScreenEvent.MouseButtonPressed.Post event) {
        HudInteractions.CLICKING_POST.invoker().onClicking(Minecraft.getInstance(), event.getScreen(), event.getMouseX(), event.getMouseY(), event.getButton());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onMouseReleasePre(ScreenEvent.MouseButtonReleased.Pre event) {
        if (HudInteractions.RELEASING_PRE.invoker().onReleasing(Minecraft.getInstance(), event.getScreen(), event.getMouseX(), event.getMouseY(), event.getButton()).isCancelled()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onMouseReleasePost(ScreenEvent.MouseButtonReleased.Post event) {
        HudInteractions.RELEASING_POST.invoker().onReleasing(Minecraft.getInstance(), event.getScreen(), event.getMouseX(), event.getMouseY(), event.getButton());
    }
}