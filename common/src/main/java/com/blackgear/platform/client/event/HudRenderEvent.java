package com.blackgear.platform.client.event;

import com.blackgear.platform.core.util.event.Event;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class HudRenderEvent {
    public static final Event<RenderHud> RENDER_HUD = Event.create(RenderHud.class);
    
    @FunctionalInterface
    public interface RenderHud {
        void render(GuiGraphics matrices, float tickDelta, ElementType type, RenderContext context);
    }

    public interface RenderContext {
        default Window window() {
            return this.minecraft().getWindow();
        }

        default int screenWidth() {
            return this.window().getGuiScaledWidth();
        }

        default int screenHeight() {
            return this.window().getGuiScaledHeight();
        }

        default Minecraft minecraft() {
            return Minecraft.getInstance();
        }

        default LocalPlayer player() {
            return this.minecraft().player;
        }

        default Gui gui() {
            return this.minecraft().gui;
        }

        default void renderTextureOverlay(ResourceLocation texture, float alpha) {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            RenderSystem.setShaderTexture(0, texture);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferBuilder.vertex(0.0, this.screenHeight(), -90.0).uv(0.0F, 1.0F).endVertex();
            bufferBuilder.vertex(this.screenWidth(), this.screenHeight(), -90.0).uv(1.0F, 1.0F).endVertex();
            bufferBuilder.vertex(this.screenWidth(), 0.0, -90.0).uv(1.0F, 0.0F).endVertex();
            bufferBuilder.vertex(0.0, 0.0, -90.0).uv(0.0F, 0.0F).endVertex();
            tesselator.end();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public enum ElementType {
        DEFAULT, HEALTH, EXPERIENCE, FIRST_PERSON, VIGNETTE
    }
}