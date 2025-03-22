package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.client.GameRendering;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
    @Redirect(
        method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/model/BakedModel;usesBlockLight()Z"
        )
    )
    private boolean onRenderItem(BakedModel instance, @Nullable LivingEntity entity, @Nullable Level level, ItemStack stack) {
        return !GameRendering.HAND_HELD_MODELS.containsKey(stack.getItem()) && instance.usesBlockLight();
    }
}