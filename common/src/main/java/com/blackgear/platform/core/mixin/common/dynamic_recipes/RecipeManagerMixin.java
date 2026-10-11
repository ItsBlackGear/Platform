package com.blackgear.platform.core.mixin.common.dynamic_recipes;

import com.blackgear.platform.common.v2.recipe.DynamicRecipeManager;
import com.google.gson.JsonElement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Unique private static final FileToIdConverter RECIPE_CONVERTER = FileToIdConverter.json(Registries.elementsDirPath(Registries.RECIPE));
    @Shadow @Final private HolderLookup.Provider registries;
    
    @Inject(
        method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
        at = @At("HEAD")
    )
    private void vb$handleDynamicRecipes(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        DynamicRecipeManager.generate(this.registries);
        
        for (Map.Entry<ResourceLocation, JsonElement> entry : DynamicRecipeManager.getRecipes().entrySet()) {
            ResourceLocation recipe = entry.getKey();
            
            if (recipe.getNamespace().equals("minecraft")) {
                object.put(recipe, entry.getValue());
            } else {
                if (object.containsKey(recipe)) {
                    ResourceLocation location = RECIPE_CONVERTER.idToFile(recipe);
                    Optional<Resource> resource = resourceManager.getResource(location);
                    
                    if (resource.isPresent() && !DynamicRecipeManager.isAllowedPack(resource.get().sourcePackId())) {
                        continue;
                    }
                }
                
                object.put(recipe, entry.getValue());
            }
        }
    }
}