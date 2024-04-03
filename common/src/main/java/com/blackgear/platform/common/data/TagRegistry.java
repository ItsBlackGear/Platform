package com.blackgear.platform.common.data;

import com.blackgear.platform.Platform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class TagRegistry<T> {
    protected final ResourceKey<? extends Registry<T>> registry;
    protected final String modId;

    protected TagRegistry(ResourceKey<? extends Registry<T>> registry, String modId) {
        this.registry = registry;
        this.modId = modId;
    }

    public static <T> TagRegistry<T> create(ResourceKey<? extends Registry<T>> registry, String modId) {
        return new TagRegistry<>(registry, modId);
    }

    public TagKey<T> register(String key) {
        return TagKey.create(this.registry, new ResourceLocation(this.modId, key));
    }

    public void register() {
        Platform.LOGGER.info("Initializing tags of type: " + this.registry.location().getPath() + " for mod: " + this.modId);
    }
}