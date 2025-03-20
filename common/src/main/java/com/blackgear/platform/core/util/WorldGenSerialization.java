package com.blackgear.platform.core.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class WorldGenSerialization {
    /**
     * Serializes and compares two configured features.
     * @author TelepathicGrunt
     */
    public static boolean serializeAndCompareFeature(PlacedFeature first, PlacedFeature second) {
        if (first == second) return true;

        Optional<JsonElement> firstElement = WorldGenCodecs.encodeFeature(first);
        if (firstElement.isEmpty()) return false;

        Optional<JsonElement> secondElement = WorldGenCodecs.encodeFeature(second);
        if (secondElement.isEmpty()) return false;

        JsonElement firstJson = firstElement.get();
        JsonElement secondJson = secondElement.get();
        return firstJson.equals(secondJson);
    }

    public static boolean serializeAndCompareCarver(ConfiguredWorldCarver<?> first, ConfiguredWorldCarver<?> second) {
        if (first == second) return true;

        Optional<JsonElement> firstElement = WorldGenCodecs.encodeCarver(first);
        if (firstElement.isEmpty()) return false;

        Optional<JsonElement> secondElement = WorldGenCodecs.encodeCarver(second);
        if (secondElement.isEmpty()) return false;

        JsonElement firstJson = firstElement.get();
        JsonElement secondJson = secondElement.get();
        return firstJson.equals(secondJson);
    }

    public static final class WorldGenCodecs {
        public static final CodecCache<PlacedFeature> CONFIGURED_FEATURE_CODEC_CACHE = CodecCache.of(PlacedFeature.DIRECT_CODEC);
        public static final CodecCache<ConfiguredWorldCarver<?>> CONFIGURED_CARVER_CODEC_CACHE = CodecCache.of(ConfiguredWorldCarver.DIRECT_CODEC);

        public static Optional<JsonElement> encodeFeature(PlacedFeature feature) {
            return CONFIGURED_FEATURE_CODEC_CACHE.get(feature);
        }

        public static Optional<JsonElement> encodeCarver(ConfiguredWorldCarver<?> carver) {
            return CONFIGURED_CARVER_CODEC_CACHE.get(carver);
        }
    }

    /**
     * @author Won-Ton.
     */
    public static final class CodecCache<T> {
        public static final int DEFAULT_CACHE_SIZE = 4096;

        private final Codec<T> codec;
        private final Map<T, Optional<JsonElement>> cache;
        private final AtomicInteger requestCount = new AtomicInteger();

        private CodecCache(Codec<T> codec, Map<T, Optional<JsonElement>> backing) {
            this.codec = codec;
            this.cache = backing;
        }

        public void clear() {
            cache.clear();
            requestCount.set(0);
        }

        public Optional<JsonElement> get(T value) {
            requestCount.incrementAndGet();
            return cache.computeIfAbsent(value, this::encode);
        }

        public String getStats() {
            int size = cache.size();
            int requests = requestCount.get();
            return String.format("Size: %s, Requests: %s, Hits: %s", size, requests, requests - size);
        }

        private Optional<JsonElement> encode(T value) {
            return codec.encodeStart(JsonOps.INSTANCE, value).result();
        }

        public static <T> CodecCache<T> of(Codec<T> codec) {
            return new CodecCache<>(codec, new IdentityHashMap<>(DEFAULT_CACHE_SIZE));
        }

        public static <T> CodecCache<T> concurrent(Codec<T> codec) {
            return new CodecCache<>(codec, new ConcurrentHashMap<>(DEFAULT_CACHE_SIZE));
        }
    }
}
