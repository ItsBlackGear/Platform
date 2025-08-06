package com.blackgear.platform.client.v2.emissive.fabric;

import com.blackgear.platform.client.v2.emissive.EmissiveModelWrapper;
import com.blackgear.platform.client.v2.emissive.EmissiveModelWrapperHolder;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.minecraft.client.resources.model.BakedModel;
import org.jetbrains.annotations.Nullable;

public class EmissiveModelWrapperImpl {
    public static void bootstrap() {
        ModelLoadingPlugin.register(context -> {
            context.modifyModelAfterBake().register(ModelModifier.WRAP_LAST_PHASE, (model, ctx) -> {
                @Nullable EmissiveModelWrapper wrapper = ((EmissiveModelWrapperHolder) ctx.loader()).getModelWrapper();
                return wrapper != null ? wrapper.wrap(model, ctx.resourceId()) : model;
            });
        });
    }

    public static BakedModel getBakedModel(BakedModel model) {
        return new FabricEmissiveLayerBakedModel(model);
    }
}