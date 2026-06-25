package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.client.v2.render.DynamicItemRenderer;
import com.blackgear.platform.client.v2.render.ItemRendererRegistry;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Shadow protected abstract void loadSpecialItemModelAndDependencies(ModelResourceLocation modelLocation);
    @Shadow @Final private Map<ModelResourceLocation, UnbakedModel> topLevelModels;
    @Shadow abstract UnbakedModel getModel(ResourceLocation modelLocation);

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    public void addModel(CallbackInfo ci) {
        for (var renderer : ItemRendererRegistry.INSTANCE.get().getRenderers().entrySet()) {
            for (var model : renderer.getValue().registerModels()) {
                this.loadSpecialItemModelAndDependencies(model);
                UnbakedModel unbaked = this.topLevelModels.get(model);
                unbaked.resolveParents(resource -> this.getModel(resource));
            }
        }

        for (var renderer : DynamicItemRenderer.INSTANCE.get().getRenderers().entrySet()) {
            for (var model : renderer.getValue().registerModels()) {
                this.loadSpecialItemModelAndDependencies(model);
                UnbakedModel unbaked = this.topLevelModels.get(model);
                unbaked.resolveParents(resource -> this.getModel(resource));
            }
        }
    }
}