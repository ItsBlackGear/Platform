package com.blackgear.platform.common.v2.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public final class DynamicRecipeManager {
    private static final Map<ResourceLocation, JsonElement> RECIPES = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, JsonElement> ADVANCEMENTS = new ConcurrentHashMap<>();
    
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
    
    public static void generate() {
        RECIPES.clear();
        ADVANCEMENTS.clear();
        
        Consumer<FinishedRecipe> consumer = finishedRecipe -> {
            ResourceLocation id = finishedRecipe.getId();
            
            RECIPES.put(id, finishedRecipe.serializeRecipe());
            JsonObject advancement = finishedRecipe.serializeAdvancement();
            if (advancement != null) {
                ADVANCEMENTS.put(finishedRecipe.getAdvancementId(), advancement);
            }
        };
        
        for (ProviderEntry entry : PROVIDERS) {
            String id = entry.modId != null ? entry.modId : "minecraft";
            Function<String, ResourceLocation> builder = path -> new ResourceLocation(id, path);
            entry.provider().buildRecipes(consumer, builder);
        }
    }
    
    public static Map<ResourceLocation, JsonElement> getRecipes() {
        return RECIPES;
    }
    
    public static Map<ResourceLocation, JsonElement> getAdvancements() {
        return ADVANCEMENTS;
    }
    
    public static boolean isAllowedPack(String packId) {
        return ALLOWED_PACKS.contains(packId.toLowerCase(Locale.ROOT));
    }
}