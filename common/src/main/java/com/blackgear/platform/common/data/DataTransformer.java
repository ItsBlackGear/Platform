package com.blackgear.platform.common.data;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.Collections;

/**
 * WARNING: This class is used onto the ResourceLocation constructor
 * which is called pretty much everywhere, so it should be handled carefully.
 * <br>
 * This class may get removed at some point in the short future...
 */
public class DataTransformer {
    private static final Map<String, ResourceLocation> DATA_TRANSFORMS = new ConcurrentHashMap<>();
    private static final Set<String> NAMESPACES = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static final String SEPARATOR = ":";

    public static void apply(ResourceLocation from, ResourceLocation to) {
        DATA_TRANSFORMS.put(from.getNamespace() + SEPARATOR + from.getPath(), to);
        NAMESPACES.add(from.getNamespace());
    }

    public static boolean shouldCheckNamespace(String namespace) {
        return !NAMESPACES.isEmpty() && NAMESPACES.contains(namespace);
    }

    public static ResourceLocation applyTransformsIfPossible(String namespace, String path) {
        if (NAMESPACES.isEmpty() || !NAMESPACES.contains(namespace)) return null;
        return DATA_TRANSFORMS.get(namespace + SEPARATOR + path);
    }
}