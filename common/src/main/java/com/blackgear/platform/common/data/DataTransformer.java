package com.blackgear.platform.common.data;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * WARNING: This class is used onto the ResourceLocation constructor
 * which is called pretty much everywhere, so it should be handled carefully.
 * <br>
 * This class may get removed at some point in the short future...
 */
public class DataTransformer {
    private static final ThreadLocal<Boolean> REENTRANT_GUARD = ThreadLocal.withInitial(() -> false);
    private static final List<Function<ResourceLocation, ResourceLocation>> TRANSFORMERS = new CopyOnWriteArrayList<>();

    public static void onDataTransformation(Consumer<Transformer> consumer) {
        consumer.accept(TRANSFORMERS::add);
    }

    public static boolean shouldCheckNamespace() {
        return !TRANSFORMERS.isEmpty();
    }

    public static ResourceLocation applyTransformsIfPossible(String namespace, String path) {
        if (TRANSFORMERS.isEmpty() || REENTRANT_GUARD.get()) return null;
        REENTRANT_GUARD.set(true);
        try {
            ResourceLocation original = ResourceLocation.fromNamespaceAndPath(namespace, path);
            for (Function<ResourceLocation, ResourceLocation> transformer : TRANSFORMERS) {
                ResourceLocation result = transformer.apply(original);
                if (result != null) {
                    return result;
                }
            }
            return null;
        } finally {
            REENTRANT_GUARD.set(false);
        }
    }

    public interface Transformer {
        void add(Function<ResourceLocation, ResourceLocation> transformer);

        default void remap(ResourceLocation original, ResourceLocation remapped) {
            this.add(path -> {
                if (path.equals(original)) {
                    return remapped;
                }

                return null;
            });
        }
    }
}