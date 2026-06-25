package com.blackgear.platform.core.mixin.forge.access;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RegistryBuilder.class)
public interface RegistryBuilderAccessor {
    @Invoker
    <T> IForgeRegistry<T> callCreate();
}
