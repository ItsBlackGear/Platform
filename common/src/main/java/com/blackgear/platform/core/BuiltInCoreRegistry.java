package com.blackgear.platform.core;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

import java.util.*;

/**
 * An adaptation of CoreRegistry that allows custom data-driven entries
 * @param <T> The type of objects being registered
 */
public class BuiltInCoreRegistry<T> {
    private final Map<ResourceLocation, T> entries = new HashMap<>();

    private final String modId;
    private final Registry<T> registry;

    protected boolean isPresent = false;

    public BuiltInCoreRegistry(Registry<T> registry, String modId) {
        this.modId = modId;
        this.registry = registry;
        this.registry.keySet().forEach(id -> this.entries.putIfAbsent(id, this.registry.get(id)));
    }

    public T register(ResourceLocation name, T entry) {
        return this.entries.put(name, entry);
    }

    public T register(String name, T entry) {
        return this.entries.put(new ResourceLocation(this.modId, name), entry);
    }

    public <E extends T> ResourceKey<T> resource(String name, E entry) {
        this.register(name, entry);
        return ResourceKey.create(this.registry.key(), new ResourceLocation(this.modId, name));
    }

    public T getOrDefault(ResourceLocation name, T fallback) {
        if (this.registry.containsKey(name)) {
            return this.registry.get(name);
        } else if (this.entries.containsKey(name)) {
            return this.entries.get(name);
        }

        return fallback;
    }

    public T get(ResourceLocation name) {
        return this.entries.get(name);
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
        return this.entries.entrySet().stream()
            .filter(entry -> Objects.equals(entry.getValue(), value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Value not found in registry: " + value))
            .getKey();
    }

    public Optional<List<T>> fromTag(TagKey<T> tag) {
        List<T> values = this.entries.values().stream()
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
        if (this.entries.isEmpty()) {
            throw new IllegalStateException("Registry is empty");
        }

        return this.getRandomElement(this.entries.values(), random);
    }

    public Collection<T> values() {
        return Collections.unmodifiableCollection(this.entries.values());
    }

    public Map<ResourceLocation, T> entries() {
        return Collections.unmodifiableMap(this.entries);
    }

    public void register() {
        if (this.isPresent) {
            throw new IllegalStateException("Duplication of BuiltIn-Registry: " + this.registry);
        }

        this.isPresent = true;
    }
}