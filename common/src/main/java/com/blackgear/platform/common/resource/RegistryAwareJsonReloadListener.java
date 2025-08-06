package com.blackgear.platform.common.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class RegistryAwareJsonReloadListener extends SimpleJsonResourceReloadListener {
    private static final List<RegistryAwareJsonReloadListener> INSTANCES = Collections.synchronizedList(new ArrayList<>());

    @Nullable private Map<ResourceLocation, JsonElement> resources;
    @Nullable private ResourceManager manager;
    @Nullable private ProfilerFiller profiler;

    public RegistryAwareJsonReloadListener(Gson gson, String directory) {
        super(gson, directory);
        INSTANCES.add(this);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        this.resources = resources;
        this.manager = manager;
        this.profiler = profiler;
    }

    public abstract void parse(Map<ResourceLocation, JsonElement> resources, RegistryAccess access, ResourceManager manager, ProfilerFiller profiler);

    public static void runReloads(RegistryAccess access) {
        for (var listener : INSTANCES) {
            if (listener.resources != null && listener.manager != null && listener.profiler != null) {
                listener.parse(listener.resources, access, listener.manager, listener.profiler);
                listener.resources = null;
                listener.manager = null;
                listener.profiler = null;
            }
        }
    }
}