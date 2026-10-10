package com.blackgear.platform.core.mixin.client;

import com.blackgear.platform.client.v2.render.BlockRendererRegistry;
import com.blackgear.platform.client.v2.render.DynamicItemRenderer;
import com.blackgear.platform.client.v2.render.ItemRendererRegistry;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Shadow protected abstract void loadTopLevel(ModelResourceLocation par1);
    @Shadow @Final private Map<ModelResourceLocation, UnbakedModel> topLevelModels;
    @Shadow public abstract UnbakedModel getModel(ResourceLocation modelLocation);

    @Shadow
    @Final
    private static Map<ResourceLocation, StateDefinition<Block, BlockState>> STATIC_DEFINITIONS;

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/resources/model/ModelBakery;STATIC_DEFINITIONS:Ljava/util/Map;", opcode = Opcodes.GETSTATIC))
    private Map<ResourceLocation, StateDefinition<Block, BlockState>> redirectStaticDefinitionsConstructor() {
        return vb$redirectStaticDefinitions();
    }

    @Redirect(method = "loadModel", at = @At(value = "FIELD", target = "Lnet/minecraft/client/resources/model/ModelBakery;STATIC_DEFINITIONS:Ljava/util/Map;", opcode = Opcodes.GETSTATIC))
    private Map<ResourceLocation, StateDefinition<Block, BlockState>> redirectStaticDefinitionsLoadModel() {
        return vb$redirectStaticDefinitions();
    }

    @Unique
    private Map<ResourceLocation, StateDefinition<Block, BlockState>> vb$redirectStaticDefinitions() {
        HashMap<ResourceLocation, StateDefinition<Block, BlockState>> map = new HashMap<>();
        for (var renderer : BlockRendererRegistry.INSTANCE.get().getRenderers().entrySet()) {
            for (var entry : renderer.getValue().registerBlockStates().entrySet()) {
                map.put(entry.getValue(), entry.getKey().getStateDefinition());
            }
        }
        map.putAll(STATIC_DEFINITIONS);
        return map;
    }

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    public void addModel(CallbackInfo ci) {
        for (var renderer : ItemRendererRegistry.INSTANCE.get().getRenderers().entrySet()) {
            for (var model : renderer.getValue().registerModels()) {
                this.loadTopLevel(model);
                UnbakedModel unbaked = this.topLevelModels.get(model);
                unbaked.resolveParents(resource -> this.getModel(resource));
            }
        }

        for (var renderer : DynamicItemRenderer.INSTANCE.get().getRenderers().entrySet()) {
            for (var model : renderer.getValue().registerModels()) {
                this.loadTopLevel(model);
                UnbakedModel unbaked = this.topLevelModels.get(model);
                unbaked.resolveParents(resource -> this.getModel(resource));
            }
        }
    }
}