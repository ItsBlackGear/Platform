package com.blackgear.platform.core.mixin.fabric.client.renderer;

import com.blackgear.platform.client.api.model.CustomBoatModel;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(BoatRenderer.class)
public class BoatRendererMixin {
    @Inject(
        method = "getTextureLocation(Lnet/minecraft/world/entity/vehicle/Boat;)Lnet/minecraft/resources/ResourceLocation;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void platform$getCustomModelTexture(Boat boat, CallbackInfoReturnable<ResourceLocation> cir) {
        if (this instanceof CustomBoatModel model) {
            cir.setReturnValue(model.getModelWithLocation(boat).getFirst());
        }
    }

    @WrapOperation(
        method = "render(Lnet/minecraft/world/entity/vehicle/Boat;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"
        )
    )
    private Object platform$getCustomModel(
        Map<Boat.Type, Pair<ResourceLocation, ListModel<Boat>>> instance,
        Object o,
        Operation<Pair<ResourceLocation, ListModel<Boat>>> original,
        Boat boat
    ) {
        if (this instanceof CustomBoatModel model) return model.getModelWithLocation(boat);
        return original.call(instance, o);
    }
}