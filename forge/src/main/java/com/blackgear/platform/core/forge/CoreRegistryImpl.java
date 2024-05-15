package com.blackgear.platform.core.forge;

import com.blackgear.platform.core.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

import java.util.function.Supplier;

public class CoreRegistryImpl<T extends IForgeRegistryEntry<T>> extends CoreRegistry<T> {
    private final DeferredRegister<T> registry;

    protected CoreRegistryImpl(IForgeRegistry<T> entry, Registry<T> registry, String modId) {
        super(registry, modId);
        this.registry = DeferredRegister.create(entry, modId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> CoreRegistry<T> create(Registry<T> registry, String modId) {
        IForgeRegistry entry = RegistryManager.ACTIVE.getRegistry((ResourceKey) registry.key());
        return entry != null ? new CoreRegistryImpl(entry, registry, modId) : new SimpleRegistry<>(registry, modId);
    }

    @Override
    public <E extends T> Supplier<E> register(String key, Supplier<E> entry) {
        return this.registry.register(key, entry);
    }
    
    @Override
    protected void bootstrap() {
        this.registry.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}