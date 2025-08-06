package com.blackgear.platform.client.v2.emissive;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface SpriteLoaderStitchContext {
    ThreadLocal<SpriteLoaderStitchContext> THREAD_LOCAL = new ThreadLocal<>();

    Map<ResourceLocation, ResourceLocation> getEmissiveMappings();

    void markHasEmissives();
}