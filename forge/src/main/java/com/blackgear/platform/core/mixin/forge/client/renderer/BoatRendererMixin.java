package com.blackgear.platform.core.mixin.forge.client.renderer;

import com.blackgear.platform.client.api.model.CustomBoatModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatRenderer.class)
public class BoatRendererMixin {
    @Inject(
        method = "getModelWithLocation",
        at = @At("HEAD"),
        remap = false,
        cancellable = true
    )
    private void platform$injectCustomModelTexture(Boat boat, CallbackInfoReturnable<ResourceLocation> cir) {
        if (this instanceof CustomBoatModel model) {
            cir.setReturnValue(model.getModelWithLocation(boat).getFirst());
        }
    }
}