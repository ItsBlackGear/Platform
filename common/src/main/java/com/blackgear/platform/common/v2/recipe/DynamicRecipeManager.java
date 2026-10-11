package com.blackgear.platform.common.v2.recipe;

import com.blackgear.platform.Platform;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class DynamicRecipeManager {
    private static final Map<ResourceLocation, JsonElement> RECIPES = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, AdvancementHolder> ADVANCEMENTS = new ConcurrentHashMap<>();
    
    private static final List<ProviderEntry> PROVIDERS = new ArrayList<>();
    private static final List<String> ALLOWED_PACKS = new ArrayList<>(List.of("vanilla"));
    
    private record ProviderEntry(DynamicRecipeProvider provider, String modId) {}
    
    public static void register(DynamicRecipeProvider provider, @Nullable String modId) {
        PROVIDERS.add(new ProviderEntry(provider, modId));
        if (modId != null) ALLOWED_PACKS.add(modId.toLowerCase(Locale.ROOT));
    }
    
    public static void register(DynamicRecipeProvider provider) {
        register(provider, null);
    }
    
    public static void generate(HolderLookup.Provider registries) {
        RECIPES.clear();
        ADVANCEMENTS.clear();
        
        RegistryOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);
        
        RecipeOutput consumer = new RecipeOutput() {
            @Override
            public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
                Recipe.CODEC.encodeStart(ops, recipe)
                    .resultOrPartial(error -> Platform.LOGGER.error("Failed to parse dynamic recipe {}: {}", id, error))
                    .ifPresent(json -> RECIPES.put(id, json));
                
                if (advancement != null) {
                    ADVANCEMENTS.put(advancement.id(), advancement);
                }
            }
            
            @Override
            public Advancement.Builder advancement() {
                return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
            }
        };
        
        for (ProviderEntry entry : PROVIDERS) {
            String id = entry.modId != null ? entry.modId : "minecraft";
            Function<String, ResourceLocation> builder = path -> ResourceLocation.fromNamespaceAndPath(id, path);
            entry.provider().buildRecipes(consumer, registries, builder);
        }
    }
    
    public static Map<ResourceLocation, JsonElement> getRecipes() {
        return RECIPES;
    }
    
    public static Map<ResourceLocation, AdvancementHolder> getAdvancements() {
        return ADVANCEMENTS;
    }
    
    public static boolean isAllowedPack(String packId) {
        return ALLOWED_PACKS.contains(packId.toLowerCase(Locale.ROOT));
    }
}