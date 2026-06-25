package com.blackgear.platform.core.api.registrar;

import com.blackgear.platform.core.helper.DynamicRegistry;
import com.mojang.serialization.Codec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public abstract class RegistrarBuilder {
    private final ArrayList<DynamicEntry<?>> entries = new ArrayList<>();
    protected final String modId;
    protected boolean isPresent = false;

    protected RegistrarBuilder(String modId) {
        this.modId = modId;
    }

    @ExpectPlatform
    public static RegistrarBuilder create(String modId) {
        throw new AssertionError();
    }

    public abstract <T> Registry<T> registerSimple(String name);

    public <T> ResourceKey<Registry<T>> registerDynamic(String name, Codec<T> codec) {
        ResourceKey<Registry<T>> key = this.resource(name);
        this.entries.add(new DynamicEntry<>(key, codec));
        return key;
    }

    protected <T> ResourceKey<Registry<T>> resource(String name) {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(this.modId, name));
    }

    public void bootstrap() {
        if (this.isPresent) return;

        this.isPresent = true;
        DynamicRegistry.onRegister(registrar -> this.entries.forEach(entry -> this.registerDynamic(registrar, entry)));
    }

    private <T> void registerDynamic(DynamicRegistry.Registrar registrar, DynamicEntry<T> entry) {
        registrar.registerDynamicRegistry(entry.key, entry.codec);
    }

    private record DynamicEntry<T>(ResourceKey<Registry<T>> key, Codec<T> codec) {}
}