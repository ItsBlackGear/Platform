package com.blackgear.platform.core;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

import java.util.*;
import java.util.function.Function;

public class BuiltInCoreRegistry<T> {
    private final Map<ResourceLocation, T> hardcodedEntries = new HashMap<>();
    private final Map<ResourceLocation, T> dataDrivenEntries = new HashMap<>();

    private final String modId;
    private final Registry<T> registry;
    protected boolean isPresent = false;
    
    public static <T> BuiltInCoreRegistry<T> create(Registry<T> registry, String modId) {
        return new BuiltInCoreRegistry<>(registry, modId);
    }
    
    public BuiltInCoreRegistry(Registry<T> registry, String modId) {
        this.modId = modId;
        this.registry = registry;
        this.registry.keySet().forEach(id -> this.hardcodedEntries.putIfAbsent(id, this.registry.get(id)));
    }

    public T register(ResourceLocation name, T entry) {
        return this.hardcodedEntries.put(name, entry);
    }

    public T register(String name, T entry) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(this.modId, name);
        return this.hardcodedEntries.put(location, entry);
    }

    public ResourceKey<T> resource(String name, T entry) {
        return this.resource(name, key -> entry);
    }
    
    public <E extends T> ResourceKey<T> resource(String name, Function<ResourceKey<T>, E> entry) {
        ResourceKey<T> key = ResourceKey.create(this.registry.key(), ResourceLocation.fromNamespaceAndPath(this.modId, name));
        this.register(name, entry.apply(key));
        return key;
    }

    public T getOrDefault(ResourceLocation name, T fallback) {
        if (this.registry.containsKey(name)) {
            return this.registry.get(name);
        }

        // Data-driven entries have priority over hardcoded ones
        if (this.dataDrivenEntries.containsKey(name)) {
            return this.dataDrivenEntries.get(name);
        } else if (this.hardcodedEntries.containsKey(name)) {
            return this.hardcodedEntries.get(name);
        }

        return fallback;
    }
    
    public T get(ResourceLocation name) {
        return this.dataDrivenEntries.containsKey(name)
            ? this.dataDrivenEntries.get(name)
            : this.hardcodedEntries.get(name);
    }

    public T get(ResourceKey<T> name) {
        return this.getOrDefault(name.location(), null);
    }

    public T getOrThrow(ResourceKey<T> key) {
        T value = this.get(key);
        if (value == null) {
            ResourceKey<?> resource = this.registry.key();
            throw new IllegalStateException("Missing key in " + resource + ":" + key);
        } else {
            return value;
        }
    }

    public ResourceLocation getKey(T value) {
        Optional<ResourceLocation> dataDrivenKey = this.dataDrivenEntries.entrySet().stream()
            .filter(entry -> Objects.equals(entry.getValue(), value))
            .findFirst()
            .map(Map.Entry::getKey);

        return dataDrivenKey.orElseGet(() -> this.hardcodedEntries.entrySet().stream()
            .filter(entry -> Objects.equals(entry.getValue(), value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Value not found in registry: " + value))
            .getKey());
    }

    public Optional<List<T>> fromTag(TagKey<T> tag) {
        List<T> values = this.getAllEntries().values().stream()
            .filter(value -> Holder.direct(value).is(tag))
            .toList();

        return values.isEmpty() ? Optional.empty() : Optional.of(values);
    }

    public Optional<T> getRandomFromTag(TagKey<T> tag, RandomSource random) {
        return this.fromTag(tag).map(values -> this.getRandomElement(values, random));
    }

    public T getRandomElement(Collection<T> collection, RandomSource random) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("Cannot get random element from empty collection");
        }

        if (collection instanceof List<T> list) {
            return list.get(random.nextInt(list.size()));
        } else {
            int index = random.nextInt(collection.size());
            Iterator<T> iterator = collection.iterator();
            for (int i = 0; i < index; i++) {
                iterator.next();
            }

            return iterator.next();
        }
    }

    public T getRandomElement(RandomSource random) {
        Map<ResourceLocation, T> allEntries = this.getAllEntries();
        if (allEntries.isEmpty()) {
            throw new IllegalStateException("Registry is empty");
        }

        return this.getRandomElement(allEntries.values(), random);
    }

    private Map<ResourceLocation, T> getAllEntries() {
        Map<ResourceLocation, T> combined = new HashMap<>();
        this.registry.entrySet().forEach(entry -> combined.put(entry.getKey().location(), entry.getValue()));
        combined.putAll(this.hardcodedEntries);
        combined.putAll(this.dataDrivenEntries); // Data-driven overrides hardcoded
        return combined;
    }

    public Collection<T> values() {
        return Collections.unmodifiableCollection(this.getAllEntries().values());
    }

    public Map<ResourceLocation, T> entries() {
        return Collections.unmodifiableMap(this.getAllEntries());
    }
    
    public void registerDataDriven(ResourceLocation name, T entry) {
        this.dataDrivenEntries.put(name, entry);
    }
    
    public void clearDataDrivenEntries() {
        this.dataDrivenEntries.clear();
    }

    public void register() {
        if (this.isPresent) {
            throw new IllegalStateException("Duplication of BuiltIn-Registry: " + this.registry);
        }

        this.isPresent = true;
    }
}