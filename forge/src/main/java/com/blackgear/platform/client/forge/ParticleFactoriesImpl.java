package com.blackgear.platform.client.forge;

import com.blackgear.platform.client.ParticleFactories;
import com.blackgear.platform.Platform;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(
    modid = Platform.MOD_ID,
    value = Dist.CLIENT,
    bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ParticleFactoriesImpl {
    private static final Set<Consumer<RegisterParticleProvidersEvent>> FACTORIES = ConcurrentHashMap.newKeySet();
    
    public static <T extends ParticleOptions, P extends ParticleType<T>> void create(Supplier<P> type, ParticleProvider<T> provider) {
        FACTORIES.add(event -> event.register(type.get(), provider));
    }
    
    public static <T extends ParticleOptions, P extends ParticleType<T>> void create(Supplier<P> type, ParticleFactories.Factory<T> factory) {
        FACTORIES.add(event -> event.register(type.get(), factory::create));
    }
    
    @SubscribeEvent
    public static void registerParticleProvider(RegisterParticleProvidersEvent event) {
        FACTORIES.forEach(consumer -> consumer.accept(event));
    }
}