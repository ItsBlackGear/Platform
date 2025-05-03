package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.client.GameRendering;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Shadow protected abstract void loadSpecialItemModelAndDependencies(ModelResourceLocation modelLocation);

    @Inject(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/model/ModelBakery;loadSpecialItemModelAndDependencies(Lnet/minecraft/client/resources/model/ModelResourceLocation;)V",
            ordinal = 1,
            shift = At.Shift.AFTER
        )
    )
    public void addModel(
        BlockColors blockColors,
        ProfilerFiller profilerFiller,
        Map<ResourceLocation, BlockModel> modelResources,
        Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> blockStateResources,
        CallbackInfo ci
    ) {
        GameRendering.HAND_HELD_MODELS.forEach((item, model) -> {
            this.loadSpecialItemModelAndDependencies(ModelResourceLocation.inventory(model));
        });
    }
}