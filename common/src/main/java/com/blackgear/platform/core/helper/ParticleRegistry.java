package com.blackgear.platform.core.helper;

import com.blackgear.platform.core.CoreRegistry;
import com.blackgear.platform.core.mixin.access.SimpleParticleTypeAccessor;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;
import java.util.function.Supplier;

public class ParticleRegistry {
    private final CoreRegistry<ParticleType<?>> particles;

    private ParticleRegistry(String modId) {
        this.particles = CoreRegistry.create(BuiltInRegistries.PARTICLE_TYPE, modId);
    }

    public static ParticleRegistry create(String modId) {
        return new ParticleRegistry(modId);
    }

    public Supplier<SimpleParticleType> register(String name) {
        return register(name, false);
    }

    public Supplier<SimpleParticleType> register(String name, boolean overrideLimiter) {
        return this.particles.register(name, () -> SimpleParticleTypeAccessor.createSimpleParticleType(overrideLimiter));
    }

    public <T extends ParticleOptions> Supplier<ParticleType<T>> register(
        String name,
        boolean overrideLimiter,
        ParticleOptions.Deserializer<T> deserializer,
        Function<ParticleType<T>, Codec<T>> factory
    ) {
        return this.particles.register(name, () -> new ParticleType<
            >(overrideLimiter, deserializer) {
            @Override
            public Codec<T> codec() {
                return factory.apply(this);
            }
        });
    }

    public void register() {
        this.particles.register();
    }

    public CoreRegistry<ParticleType<?>> registry() {
        return this.particles;
    }
    
    
    public static <T extends ParticleOptions> void sendParticles(ServerLevel level, T particle, double x, double y, double z, int particleCount, double xOffset, double yOffset, double zOffset, double speed) {
        sendParticles(level, particle, false, false, x, y, z, particleCount, xOffset, yOffset, zOffset, speed);
    }
    
    public static <T extends ParticleOptions> void sendParticles(ServerLevel level, T particle, boolean longDistance, boolean overrideLimiter, double x, double y, double z, int particleCount, double xOffset, double yOffset, double zOffset, double speed) {
        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(particle, overrideLimiter, x, y, z, (float) xOffset, (float) yOffset, (float) zOffset, (float) speed, particleCount);
        for (int i = 0; i < level.players().size(); i++) {
            ServerPlayer player = level.players().get(i);
            sendParticles(level, player, longDistance, x, y, z, packet);
        }
    }
    
    public static <T extends ParticleOptions> void sendParticles(ServerLevel level, ServerPlayer player, T particle, boolean longDistance, boolean overrideLimiter, double x, double y, double z, int particleCount, double xOffset, double yOffset, double zOffset, double speed) {
        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(particle, overrideLimiter, x, y, z, (float) xOffset, (float) yOffset, (float) zOffset, (float) speed, particleCount);
        sendParticles(level, player, longDistance, x, y, z, packet);
    }
    
    public static void sendParticles(ServerLevel level, ServerPlayer player, boolean longDistance, double x, double y, double z, Packet<?> packet) {
        if (player.level() == level) {
            BlockPos pos = player.blockPosition();
            if (pos.closerToCenterThan(new Vec3(x, y, z), longDistance ? 512.0 : 32.0)) {
                player.connection.send(packet);
            }
        }
    }
}