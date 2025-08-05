package com.blackgear.platform.client.v2.emissive;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface SpriteResourceLoaderContext {
    ThreadLocal<SpriteResourceLoaderContext> THREAD_LOCAL = new ThreadLocal<>();

    void setEmissiveMappings(@Nullable Map<ResourceLocation, ResourceLocation> mappings);
}