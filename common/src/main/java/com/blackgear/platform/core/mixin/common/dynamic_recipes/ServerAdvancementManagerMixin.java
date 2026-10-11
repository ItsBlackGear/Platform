package com.blackgear.platform.core.mixin.common.dynamic_recipes;

import com.blackgear.platform.common.v2.recipe.DynamicRecipeManager;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import net.minecraft.advancements.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mixin(ServerAdvancementManager.class)
public abstract class ServerAdvancementManagerMixin {
    @Unique private static final FileToIdConverter ADVANCEMENT_CONVERTER = FileToIdConverter.json(Registries.elementsDirPath(Registries.ADVANCEMENT));
    
    @Shadow private Map<ResourceLocation, AdvancementHolder> advancements;
    @Shadow private AdvancementTree tree;
    
    @Inject(
        method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
        at = @At("TAIL")
    )
    private void vb$injectDynamicAdvancements(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        Map<ResourceLocation, AdvancementHolder> dynamic = DynamicRecipeManager.getAdvancements();
        if (dynamic.isEmpty()) return;
        
        Map<ResourceLocation, AdvancementHolder> merged = new HashMap<>(this.advancements);
        
        for (Map.Entry<ResourceLocation, AdvancementHolder> entry : dynamic.entrySet()) {
            ResourceLocation id = entry.getKey();
            
            if (object.containsKey(id)) {
                Optional<Resource> resource = resourceManager.getResource(ADVANCEMENT_CONVERTER.idToFile(id));
                if (resource.isPresent() && !DynamicRecipeManager.isAllowedPack(resource.get().sourcePackId())) {
                    continue;
                }
            }
            
            merged.put(id, entry.getValue());
        }
        
        this.advancements = ImmutableMap.copyOf(merged);
        
        AdvancementTree newTree = new AdvancementTree();
        newTree.addAll(this.advancements.values());
        for (AdvancementNode root : newTree.roots()) {
            if (root.holder().value().display().isPresent()) {
                TreeNodePosition.run(root);
            }
        }
        this.tree = newTree;
    }
}