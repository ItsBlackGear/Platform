package com.blackgear.platform.client.v2.emissive.forge;

import com.blackgear.platform.client.v2.emissive.EmissiveModelWrapperHolder;
import com.blackgear.platform.client.v2.emissive.EmissiveModelWrapper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class EmissiveModelWrapperImpl {
    public static void bootstrap() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        if (bus == null) return;

        Consumer<ModelEvent.ModifyBakingResult> consumer = event -> {
            @Nullable EmissiveModelWrapper handler = ((EmissiveModelWrapperHolder) event.getModelBakery()).getModelWrapper();
            if (handler == null) return;

            for (ResourceLocation id : event.getModels().keySet()) {
                BakedModel original = event.getModels().get(id);

                if (ForgeEmissiveLayerBakedModel.shouldWrapModel(original)) {
                    event.getModels().put(id, handler.wrap(original, id));
                }
            }
        };
        bus.addListener(consumer);
    }

    public static BakedModel getBakedModel(BakedModel model) {
        return new ForgeEmissiveLayerBakedModel(model);
    }
}