package com.blackgear.platform.core.api.registrar.fabric;

import com.blackgear.platform.core.api.registrar.RegistrarBuilder;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class RegistrarBuilderImpl extends RegistrarBuilder {
    protected RegistrarBuilderImpl(String modId) {
        super(modId);
    }

    public static RegistrarBuilder create(String modId) {
        return new RegistrarBuilderImpl(modId);
    }

    @Override
    public <T> Registry<T> registerSimple(String name) {
        ResourceKey<Registry<T>> key = this.resource(name);
        return FabricRegistryBuilder.createSimple(key).buildAndRegister();
    }
}