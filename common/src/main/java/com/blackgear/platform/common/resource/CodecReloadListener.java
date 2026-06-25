package com.blackgear.platform.common.resource;

import com.blackgear.platform.Platform;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public abstract class CodecReloadListener<T> extends SimplePreparableReloadListener<Map<ResourceLocation, T>> {
    protected final Codec<T> codec;
    protected final FileToIdConverter converter;
    private final HolderLookup.Provider registries;

    public CodecReloadListener(Codec<T> codec, FileToIdConverter converter) {
        this(codec, converter, null);
    }

    public CodecReloadListener(Codec<T> codec, FileToIdConverter converter, HolderLookup.Provider registries) {
        this.codec = codec;
        this.converter = converter;
        this.registries = registries;
    }

    @Override
    protected Map<ResourceLocation, T> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, T> data = new HashMap<>();

        DynamicOps<JsonElement> ops = this.registries != null ? RegistryOps.create(JsonOps.INSTANCE, this.registries) : JsonOps.INSTANCE;
        Map<ResourceLocation, Resource> resources = this.converter.listMatchingResources(resourceManager);
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            ResourceLocation location = entry.getKey();
            ResourceLocation id = this.converter.fileToId(location);

            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement element = JsonParser.parseReader(reader);
                DataResult<T> result = this.codec.parse(ops, element);

                if (result.error().isPresent()) {
                    throw new JsonSyntaxException(result.error().get().message());
                }

                if (data.put(id, result.result().orElseThrow()) != null) {
                    throw new IllegalStateException("Duplicate data file ignored with ID " + id);
                }
            } catch (Exception e) {
                Platform.LOGGER.error("Couldn't parse data file {} from {}", id, location, e);
            }
        }

        return data;
    }
}