package com.blackgear.platform.core.helper.forge;

import com.blackgear.platform.core.CoreRegistry;
import com.blackgear.platform.core.helper.DataSerializerRegistry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class DataSerializerRegistryImpl extends DataSerializerRegistry {
    public final CoreRegistry<EntityDataSerializer<?>> registry;

    public DataSerializerRegistryImpl(String modId) {
        this.registry = CoreRegistry.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, modId);
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
}