package com.blackgear.platform.core;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

public interface RegistryHolder<T> extends Supplier<T> {
    @Override T get();

    default Optional<T> asOptional() {
        return this.isPresent() ? Optional.ofNullable(this.get()) : Optional.empty();
    }

    Optional<Holder<T>> getHolder();

    boolean isPresent();

    ResourceLocation getId();

    ResourceKey<T> getKey();
}
