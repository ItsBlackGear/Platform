package com.blackgear.platform.core.util;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public final class ExtraCodecs {
    public static final Codec<Integer> POSITIVE_INT = intRangeWithMessage(1, Integer.MAX_VALUE, integer -> "Value must be positive: " + integer);
    public static final Codec<Float> POSITIVE_FLOAT = floatRangeMinExclusiveWithMessage(0.0F, Float.MAX_VALUE, float_ -> "Value must be positive: " + float_);
    
    /**
     * Creates a codec that represents an exclusive-or (XOR) relationship between two codecs.
     *
     * @param first  The first codec.
     * @param second The second codec.
     * @param <F>    The type of the first codec.
     * @param <S>    The type of the second codec.
     * @return The created XOR codec.
     */
    public static <F, S> Codec<Either<F, S>> xor(Codec<F> first, Codec<S> second) {
        return new XorCodec<>(first, second);
    }
    
    /**
     * Validates a codec by applying a function to it.
     *
     * @param codec    The codec to be validated.
     * @param function The function to be applied to the codec.
     * @return The validated codec.
     */
    public static <T> Codec<T> validate(Codec<T> codec, Function<T, DataResult<T>> function) {
        return codec.flatXmap(function, function);
    }
    
    /**
     * Checks if a list is non-empty.
     *
     * @return A function that takes a list and returns a DataResult of the list if it's non-empty, or an error otherwise.
     */
    public static <T> Function<List<T>, DataResult<List<T>>> nonEmptyListCheck() {
        return list -> list.isEmpty()
            ? DataResult.error("List must have contents")
            : DataResult.success(list);
    }
    
    /**
     * Validates a codec of a list by checking if the list is non-empty.
     *
     * @param codec The codec of the list to be validated.
     * @return The validated codec of the list.
     */
    public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> codec) {
        return codec.flatXmap(nonEmptyListCheck(), nonEmptyListCheck());
    }
    
    /**
     * Creates a function to check if a number is within a specified range, inclusive, with a custom error message.
     *
     * @param min          The minimum value of the range.
     * @param max          The maximum value of the range.
     * @param errorMessage The function to generate an error message.
     * @param <N>          The type of the number.
     * @return The function to check the range.
     */
    private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> checkRangeWithMessage(N min, N max, Function<N, String> errorMessage) {
        return value -> value.compareTo(min) >= 0 && value.compareTo(max) <= 0
            ? DataResult.success(value)
            : DataResult.error(errorMessage.apply(value));
    }
    
    /**
     * Creates a codec for an integer within a specified range, inclusive, with a custom error message.
     *
     * @param min          The minimum value of the range.
     * @param max          The maximum value of the range.
     * @param errorMessage The function to generate an error message.
     * @return The created codec.
     */
    private static Codec<Integer> intRangeWithMessage(int min, int max, Function<Integer, String> errorMessage) {
        Function<Integer, DataResult<Integer>> function = checkRangeWithMessage(min, max, errorMessage);
        return Codec.INT.flatXmap(function, function);
    }
    
    /**
     * Creates a function to check if a number is within a specified range, exclusive of the minimum, with a custom error message.
     *
     * @param min          The minimum value of the range, exclusive.
     * @param max          The maximum value of the range.
     * @param errorMessage The function to generate an error message.
     * @param <N>          The type of the number.
     * @return The function to check the range.
     */
    private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> checkRangeMinExclusiveWithMessage(N min, N max, Function<N, String> errorMessage) {
        return value -> value.compareTo(min) > 0 && value.compareTo(max) <= 0
            ? DataResult.success(value)
            : DataResult.error(errorMessage.apply(value));
    }
    
    /**
     * Creates a codec for a float within a specified range, exclusive of the minimum, with a custom error message.
     *
     * @param min          The minimum value of the range, exclusive.
     * @param max          The maximum value of the range.
     * @param errorMessage The function to generate an error message.
     * @return The created codec.
     */
    private static Codec<Float> floatRangeMinExclusiveWithMessage(float min, float max, Function<Float, String> errorMessage) {
        Function<Float, DataResult<Float>> function = checkRangeMinExclusiveWithMessage(min, max, errorMessage);
        return Codec.FLOAT.flatXmap(function, function);
    }
    
    /**
     * Serializes and compares two configured features.
     * @author TelepathicGrunt
     */
    public static boolean serializeAndCompareFeature(ConfiguredFeature<?, ?> first, ConfiguredFeature<?, ?> second) {
        if (first == second) return true;
        
        Optional<JsonElement> firstElement = WorldGenCodecs.encodeFeature(first);
        if (!firstElement.isPresent()) return false;
        
        Optional<JsonElement> secondElement = WorldGenCodecs.encodeFeature(second);
        if (!secondElement.isPresent()) return false;
        
        JsonElement firstJson = firstElement.get();
        JsonElement secondJson = secondElement.get();
        return firstJson.equals(secondJson);
    }
    
    public static boolean serializeAndCompareStructure(ConfiguredStructureFeature<?, ?> first, ConfiguredStructureFeature<?, ?> second) {
        if (first == second) return true;
        
        Optional<JsonElement> firstElement = WorldGenCodecs.encodeStructure(first);
        if (!firstElement.isPresent()) return false;
        
        Optional<JsonElement> secondElement = WorldGenCodecs.encodeStructure(second);
        if (!secondElement.isPresent()) return false;
        
        JsonElement firstJson = firstElement.get();
        JsonElement secondJson = secondElement.get();
        return firstJson.equals(secondJson);
    }
    
    public static boolean serializeAndCompareCarver(ConfiguredWorldCarver<?> first, ConfiguredWorldCarver<?> second) {
        if (first == second) return true;
        
        Optional<JsonElement> firstElement = WorldGenCodecs.encodeCarver(first);
        if (!firstElement.isPresent()) return false;
        
        Optional<JsonElement> secondElement = WorldGenCodecs.encodeCarver(second);
        if (!secondElement.isPresent()) return false;
        
        JsonElement firstJson = firstElement.get();
        JsonElement secondJson = secondElement.get();
        return firstJson.equals(secondJson);
    }
    
    /**
     * A codec that represents an exclusive-or (XOR) relationship between two codecs.
     *
     * @param <F> The type of the first codec.
     * @param <S> The type of the second codec.
     */
    static final class XorCodec<F, S> implements Codec<Either<F, S>> {
        private final Codec<F> first;
        private final Codec<S> second;
        
        public XorCodec(Codec<F> first, Codec<S> second) {
            this.first = first;
            this.second = second;
        }
        
        @Override
        public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> ops, T value) {
            DataResult<Pair<Either<F, S>, T>> firstData = this.first.decode(ops, value).map(pair -> pair.mapFirst(Either::left));
            DataResult<Pair<Either<F, S>, T>> secondData = this.second.decode(ops, value).map(pair -> pair.mapFirst(Either::right));
            Optional<Pair<Either<F, S>, T>> firstResult = firstData.result();
            Optional<Pair<Either<F, S>, T>> secondResult = secondData.result();
            
            if (firstResult.isPresent() && secondResult.isPresent()) {
                return DataResult.error(
                    "Both alternatives read successfully, can not pick the correct one; first: " + firstResult.get() + " second: " + secondResult.get(),
                    firstResult.get()
                );
            } else {
                return firstResult.isPresent() ? firstData : secondData;
            }
        }
        
        public <T> DataResult<T> encode(Either<F, S> either, DynamicOps<T> ops, T value) {
            return either.map(entry -> this.first.encode(entry, ops, value), entry -> this.second.encode(entry, ops, value));
        }
        
        @SuppressWarnings("rawtypes")
        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (object != null && this.getClass() == object.getClass()) {
                ExtraCodecs.XorCodec<?, ?> codec = (ExtraCodecs.XorCodec) object;
                return Objects.equals(this.first, codec.first) && Objects.equals(this.second, codec.second);
            } else {
                return false;
            }
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.first, this.second);
        }
        
        @Override
        public String toString() {
            return "XorCodec[" + this.first + ", " + this.second + "]";
        }
    }
    
    public static final class WorldGenCodecs {
        public static final CodecCache<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE_CODEC_CACHE = CodecCache.of(ConfiguredFeature.DIRECT_CODEC);
        public static final CodecCache<ConfiguredStructureFeature<?, ?>> CONFIGURED_STRUCTURE_CODEC_CACHE = CodecCache.of(ConfiguredStructureFeature.DIRECT_CODEC);
        public static final CodecCache<ConfiguredWorldCarver<?>> CONFIGURED_CARVER_CODEC_CACHE = CodecCache.of(ConfiguredWorldCarver.DIRECT_CODEC);
        
        public static Optional<JsonElement> encodeFeature(ConfiguredFeature<?, ?> feature) {
            return CONFIGURED_FEATURE_CODEC_CACHE.get(feature);
        }
        
        public static Optional<JsonElement> encodeStructure(ConfiguredStructureFeature<?, ?> structure) {
            return CONFIGURED_STRUCTURE_CODEC_CACHE.get(structure);
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
        private final AtomicInteger requestCount = new AtomicInteger(0);
        
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