package com.blackgear.platform.forge.client;

import com.blackgear.platform.Platform;
import com.blackgear.platform.client.event.ComputeCameraAnglesCallback;
import com.blackgear.platform.client.event.FogRendering;
import com.blackgear.platform.client.event.HudRenderEvent;
import com.blackgear.platform.client.event.LocalPlayerEvents;
import com.blackgear.platform.client.event.input.RawInputEvent;
import com.blackgear.platform.client.event.screen.HudRendering;
import com.blackgear.platform.client.event.screen.TooltipEvents;
import com.blackgear.platform.client.event.screen.api.ScreenAccessImpl;
import com.blackgear.platform.common.events.TickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
    modid = Platform.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE,
    value = Dist.CLIENT
)
public class ForgeClientEvents {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        ComputeCameraAnglesCallback.EVENT.invoker().handle(new ComputeCameraAnglesCallback.ComputeCameraAngles(
            event.getRenderer(),
            event.getCamera(),
            event.getPartialTick(),
            event.getYaw(),
            event.getPitch(),
            event.getRoll()
        ) {
            @Override
            public void setPitch(float pitch) {
                event.setPitch(pitch);
            }

            @Override
            public void setRoll(float roll) {
                event.setRoll(roll);
            }

            @Override
            public void setYaw(float yaw) {
                event.setYaw(yaw);
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onItemTooltip(ItemTooltipEvent event) {
        TooltipEvents.ITEM_SETUP.invoker().registerTooltip(event.getItemStack(), event.getToolTip(), event.getFlags());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onItemTooltipRender(RenderTooltipEvent.Pre event) {
        if (TooltipEvents.RENDER_TOOLTIP.invoker().onRendering(event.getGraphics(), event.getComponents(), event.getX(), event.getY()).isCancelled()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRenderGui(RenderGuiEvent.Post event) {
        HudRendering.RENDERING.invoker().onRender(Minecraft.getInstance(), event.getGuiGraphics(), event.getPartialTick());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onScreenInitialization(ScreenEvent.Init.Pre event) {
        if (HudRendering.PRE_INITIALIZE.invoker().onInitialize(Minecraft.getInstance(), event.getScreen(), new ScreenAccessImpl(event.getScreen())).isCancelled()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onScreenInitialization(ScreenEvent.Init.Post event) {
        HudRendering.POST_INITIALIZE.invoker().onInitialize(Minecraft.getInstance(), event.getScreen(), new ScreenAccessImpl(event.getScreen()));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onScreenRendering(ScreenEvent.Render.Pre event) {
        if (HudRendering.PRE_RENDERING.invoker().onRender(Minecraft.getInstance(), event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick()).isCancelled()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onScreenRendering(ScreenEvent.Render.Post event) {
        HudRendering.POST_RENDERING.invoker().onRender(Minecraft.getInstance(), event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBackgroundRender(ContainerScreenEvent.Render.Background event) {
        HudRendering.RENDER_BACKGROUND.invoker().onRender(Minecraft.getInstance(), event.getContainerScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), Minecraft.getInstance().getDeltaFrameTime());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onForegroundRender(ContainerScreenEvent.Render.Foreground event) {
        HudRendering.RENDER_FOREGROUND.invoker().onRender(Minecraft.getInstance(), event.getContainerScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), Minecraft.getInstance().getDeltaFrameTime());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onContainerOpen(ScreenEvent.Opening event) {
        HudRendering.OPEN_CONTAINER.invoker().onOpen(Minecraft.getInstance(), event.getNewScreen());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onContainerClose(ScreenEvent.Closing event) {
        HudRendering.CLOSE_CONTAINER.invoker().onClose(Minecraft.getInstance(), event.getScreen());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        LocalPlayerEvents.ON_LOGIN.invoker().onLogin(event.getPlayer());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerLeave(ClientPlayerNetworkEvent.LoggingOut event) {
        LocalPlayerEvents.ON_LOGOUT.invoker().onLogout(event.getPlayer());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerRespawn(ClientPlayerNetworkEvent.Clone event) {
        LocalPlayerEvents.ON_RESPAWN.invoker().onRespawn(event.getOldPlayer(), event.getNewPlayer());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onKeyPress(InputEvent.Key event) {
        RawInputEvent.ON_KEY_PRESS.invoker().handle(Minecraft.getInstance(), event.getKey(), event.getScanCode(), event.getAction(), event.getModifiers());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (RawInputEvent.ON_MOUSE_SCROLL.invoker().handle(Minecraft.getInstance(), event.getScrollDelta()).isCancelled()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            TickEvents.CLIENT_TICK_PRE.invoker().handle();
        } else {
            TickEvents.CLIENT_TICK_POST.invoker().handle();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void renderHudEvent(RenderGuiOverlayEvent.Post event) {
        GuiGraphics matrices = event.getGuiGraphics();
        float tickDelta = event.getPartialTick();

        Minecraft minecraft = Minecraft.getInstance();
        HudRenderEvent.RenderContext context = new HudRenderEvent.RenderContext() {};

        if (Minecraft.useFancyGraphics()) {
            HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.VIGNETTE, context);
        }

        if (minecraft.gameMode.canHurtPlayer()) {
            HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.HEALTH, context);
        }

        if (minecraft.gameMode.hasExperience()) {
            HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.EXPERIENCE, context);
        }

        if (minecraft.options.getCameraType().isFirstPerson()) {
            HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.FIRST_PERSON, context);
        }

        HudRenderEvent.RENDER_HUD.invoker().render(matrices, tickDelta, HudRenderEvent.ElementType.DEFAULT, context);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void renderFogColor(ViewportEvent.ComputeFogColor event) {
        FogRendering.ColorData data = new FogRendering.ColorData(event.getCamera(), event.getRed(), event.getGreen(), event.getBlue());
        FogRendering.FOG_COLOR.invoker().setColor(data, (float) event.getPartialTick());
        event.setRed(data.getRed());
        event.setGreen(data.getGreen());
        event.setBlue(data.getBlue());
    }


    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void setupFogDensity(ViewportEvent.RenderFog event) {
        float density = FogRendering.FOG_DENSITY.invoker().setDensity(event.getCamera(), 0.1F);
        if (density != 0.1F) {
            event.setNearPlaneDistance(-8.0F);
            event.setFarPlaneDistance(density * 0.5F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void renderFog(ViewportEvent.RenderFog event) {
        FogRendering.FogData data = new FogRendering.FogData(event.getNearPlaneDistance(), event.getFarPlaneDistance(), event.getFogShape());
        if (FogRendering.FOG_RENDER.invoker().onFogRender(event.getMode(), event.getType(), event.getCamera(), (float) event.getPartialTick(), event.getRenderer().getRenderDistance(), event.getFarPlaneDistance(), event.getNearPlaneDistance(), event.getFogShape(), data).isCancelled()) {
            event.setNearPlaneDistance(data.getNearPlaneDistance());
            event.setFarPlaneDistance(data.getFarPlaneDistance());
            event.setFogShape(data.getShape());
        }
    }
}