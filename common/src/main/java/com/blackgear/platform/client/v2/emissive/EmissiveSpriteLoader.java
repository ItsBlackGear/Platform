package com.blackgear.platform.client.v2.emissive;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface EmissiveSpriteLoader {
    ThreadLocal<EmissiveSpriteLoader> THREAD_LOCAL = new ThreadLocal<>();

    @Nullable
    EmissiveSpriteLoader.Controller getController(ResourceLocation atlas);

    interface Controller {
        @Nullable
        Map<ResourceLocation, ResourceLocation> getEmissiveMappings();

        void setEmissiveMappings(Map<ResourceLocation, ResourceLocation> mappings);

        void markHasEmissives();
    }
}