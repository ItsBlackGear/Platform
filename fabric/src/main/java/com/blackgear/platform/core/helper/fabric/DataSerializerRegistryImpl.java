package com.blackgear.platform.core.helper.fabric;

import com.blackgear.platform.Platform;
import com.blackgear.platform.core.CoreRegistry;
import com.blackgear.platform.core.RegistryBuilder;
import com.blackgear.platform.core.helper.DataSerializerRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public class DataSerializerRegistryImpl extends DataSerializerRegistry {
    private static final RegistryBuilder BUILDER = RegistryBuilder.create(Platform.MOD_ID);
    private static final ResourceKey<Registry<EntityDataSerializer<?>>> REGISTRY_KEY = BUILDER.resource("entity_data_serializers");
    private static final Supplier<Registry<EntityDataSerializer<?>>> REGISTRY = BUILDER.registry(REGISTRY_KEY);

    private final CoreRegistry<EntityDataSerializer<?>> registry;

    public DataSerializerRegistryImpl(String modId) {
        this.registry = CoreRegistry.create(REGISTRY_KEY, modId);
    }

    public static DataSerializerRegistry create(String modId) {
        return new DataSerializerRegistryImpl(modId);
    }

    @Override
    public <T> Supplier<EntityDataSerializer<T>> register(String name, Supplier<EntityDataSerializer<T>> serializer) {
        return this.registry.register(name, serializer);
    }

    @Override
    public void register() {
        this.registry.register();
    }

    private static final int VANILLA_SERIALIZER_LIMIT = 256;

    public static EntityDataSerializer<?> getSerializer(int id) {
        return REGISTRY.get().byId(id - VANILLA_SERIALIZER_LIMIT);
    }

    public static int getSerializedId(EntityDataSerializer<?> serializer, int vanilla) {
        int id = REGISTRY.get().getId(serializer);
        if (id >= 0) return id + VANILLA_SERIALIZER_LIMIT;

        return vanilla;
    }
}