package com.blackgear.platform.client.v2.emissive;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class EmissiveModelWrapper {
    private final boolean shouldWrap;

    public EmissiveModelWrapper(boolean shouldWrap) {
        this.shouldWrap = shouldWrap;
    }

    public static EmissiveModelWrapper create(boolean shouldWrap) {
        return shouldWrap ? new EmissiveModelWrapper(true) : null;
    }

    public BakedModel wrap(@Nullable BakedModel model, ResourceLocation resource) {
        if (model == null || !this.shouldWrap) return null;

        if (!model.isCustomRenderer() && (resource == null || !resource.equals(ModelBakery.MISSING_MODEL_LOCATION))) {
            return getBakedModel(model);
        }

        return model;
    }

    @ExpectPlatform
    public static BakedModel getBakedModel(BakedModel model) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void bootstrap() {
        throw new AssertionError();
    }
}