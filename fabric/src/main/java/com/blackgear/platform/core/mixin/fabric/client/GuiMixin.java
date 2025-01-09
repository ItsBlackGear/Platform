package com.blackgear.platform.core.mixin.fabric.client;

import com.blackgear.platform.client.event.HudRenderEvent;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow private int screenHeight;
    @Shadow private int screenWidth;
    
    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;getArmor(I)Lnet/minecraft/world/item/ItemStack;"
        )
    )
    private void render(PoseStack matrixStack, float partialTicks, CallbackInfo ci) {
        HudRenderEvent.RenderContext context = new HudRenderEvent.RenderContext() {
            @Override
            public Window window() {
                return minecraft.getWindow();
            }
            
            @Override
            public int screenWidth() {
                return screenWidth;
            }
            
            @Override
            public int screenHeight() {
                return screenHeight;
            }
        };
        HudRenderEvent.RENDER_HUD.invoker().render(matrixStack, partialTicks, HudRenderEvent.ElementType.FIRST_PERSON, context);
    }
}