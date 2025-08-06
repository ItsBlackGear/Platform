package com.blackgear.platform.client.v2.emissive.neoforge;

import com.blackgear.platform.client.v2.emissive.EmissiveModelWrapperHolder;
import com.blackgear.platform.client.v2.emissive.EmissiveModelWrapper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class EmissiveModelWrapperImpl {
    public static void bootstrap() {
        IEventBus bus = ModLoadingContext.get().getActiveContainer().getEventBus();
        if (bus == null) return;

        Consumer<ModelEvent.ModifyBakingResult> consumer = event -> {
            @Nullable EmissiveModelWrapper handler = ((EmissiveModelWrapperHolder) event.getModelBakery()).getModelWrapper();
            if (handler == null) return;

            for (ModelResourceLocation id : event.getModels().keySet()) {
                BakedModel original = event.getModels().get(id);

                if (ForgeEmissiveLayerBakedModel.shouldWrapModel(original)) {
                    event.getModels().put(id, handler.wrap(original, id.id()));
                }
            }
        };
        bus.addListener(consumer);
    }

    public static BakedModel getBakedModel(BakedModel model) {
        return new ForgeEmissiveLayerBakedModel(model);
    }
}