package com.blackgear.platform.core.api.registrar.resource;

import com.blackgear.platform.common.resource.RegistryAwareJsonReloadListener;
import com.blackgear.platform.core.BuiltInCoreRegistry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.Map;

public class BuiltInRegistryReloadListener<T> extends RegistryAwareJsonReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final BuiltInCoreRegistry<T> registry;
    private final Codec<T> codec;
    private final String path;
    
    protected BuiltInRegistryReloadListener(
        Gson gson,
        String directory,
        BuiltInCoreRegistry<T> registry,
        Codec<T> codec
    ) {
        super(gson, directory);
        this.registry = registry;
        this.codec = codec;
        this.path = directory;
    }
    
    public static <T> BuiltInRegistryReloadListener<T> create(BuiltInCoreRegistry<T> registry, Codec<T> codec, String path) {
        return new BuiltInRegistryReloadListener<>(GSON, path, registry, codec);
    }
    
    @Override
    public void parse(Map<ResourceLocation, JsonElement> resources, RegistryAccess access, ResourceManager manager, ProfilerFiller profiler) {
        String type = this.path.replace("_", " ");
        profiler.push("Loading " + type);
        
        this.registry.clearDataDrivenEntries();
        
        DynamicOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, access);
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            ResourceLocation name = entry.getKey();
            JsonElement element = entry.getValue();
            
            try {
                this.codec.parse(ops, element)
                    .resultOrPartial(error -> LOGGER.error("Failed to parse {} {}: {}", type, name, error))
                    .ifPresent(value -> this.registry.registerDataDriven(name, value));
            } catch (JsonParseException exception) {
                LOGGER.error("Failed to parse {} JSON {}: {}", type, name, exception.getMessage(), exception);
            } catch (Exception exception) {
                LOGGER.error("Unexpected error processing {} {}", type, name, exception);
            }
        }
        
        profiler.pop();
    }
}