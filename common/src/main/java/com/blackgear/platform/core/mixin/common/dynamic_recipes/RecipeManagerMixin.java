package com.blackgear.platform.core.mixin.common.dynamic_recipes;

import com.blackgear.platform.common.v2.recipe.DynamicRecipeManager;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Inject(
        method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
        at = @At("HEAD")
    )
    private void vb$handleDynamicRecipes(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        DynamicRecipeManager.generate();
        
        for (Map.Entry<ResourceLocation, JsonElement> entry : DynamicRecipeManager.getRecipes().entrySet()) {
            ResourceLocation recipeId = entry.getKey();
            
            if (recipeId.getNamespace().equals("minecraft")) {
                object.put(recipeId, entry.getValue());
            } else {
                if (object.containsKey(recipeId)) {
                    ResourceLocation fileLocation = new ResourceLocation(recipeId.getNamespace(), "recipes/" + recipeId.getPath() + ".json");
                    Optional<Resource> resource = resourceManager.getResource(fileLocation);
                    
                    if (resource.isPresent()) {
                        String packId = resource.get().sourcePackId();
                        if (!DynamicRecipeManager.isAllowedPack(packId)) {
                            continue;
                        }
                    }
                }
                object.put(recipeId, entry.getValue());
            }
        }
    }
}