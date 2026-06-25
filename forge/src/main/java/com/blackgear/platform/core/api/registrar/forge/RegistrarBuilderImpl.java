package com.blackgear.platform.core.api.registrar.forge;

import com.blackgear.platform.core.api.registrar.RegistrarBuilder;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;

public class RegistrarBuilderImpl extends RegistrarBuilder {
    protected RegistrarBuilderImpl(String modId) {
        super(modId);
    }

    public static RegistrarBuilder create(String modId) {
        return new RegistrarBuilderImpl(modId);
    }

    @Override
    public <T> Registry<T> registerSimple(String name) {
        return new MappedRegistry<>(this.resource(name), Lifecycle.stable());
    }
}