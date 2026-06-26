package com.blackgear.platform.core.fabric;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.util.config.ConfigBuilder;
import com.blackgear.platform.core.util.config.ModConfig;
import com.blackgear.platform.core.util.config.fabric.FabricConfigBuilder;
import com.blackgear.platform.core.util.config.fabric.ConfigTracker;
import com.blackgear.platform.core.util.config.fabric.FabricConfigSpec;
import com.blackgear.platform.core.util.config.fabric.ModConfigImpl;
import com.blackgear.platform.fabric.PlatformFabric;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class EnvironmentImpl {
    private static final Supplier<Supplier<BlockableEventLoop<?>>> CLIENT_EXECUTOR = () -> Minecraft::getInstance;

    public static boolean isClientSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
    
    public static boolean isProduction() {
        return !FabricLoader.getInstance().isDevelopmentEnvironment();
    }
    
    public static boolean isDevelopment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
    
    public static boolean hasModLoaded(String modId) {
        Objects.requireNonNull(modId, "Mod ID cannot be null");
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static String getModVersion(String modId) {
        Objects.requireNonNull(modId, "Mod ID cannot be null");
        return FabricLoader.getInstance()
            .getModContainer(modId)
            .map(container -> container.getMetadata().getVersion().toString())
            .orElse(null);
    }

    public static Optional<MinecraftServer> getCurrentServer() {
        return Optional.ofNullable(PlatformFabric.getServer());
    }
    
    public static BlockableEventLoop<?> getGameExecutor() {
        if (Environment.isClientSide()) {
            return CLIENT_EXECUTOR.get().get();
        } else {
            return Environment.getCurrentServer().orElseThrow(() -> new IllegalStateException("No server available"));
        }
    }
    
    public static <T> T registerConfig(String modId, ModConfig.Type type, String fileName, Function<ConfigBuilder, T> spec) {
        Pair<T, FabricConfigSpec> pair = new FabricConfigBuilder().configure(spec);
        ConfigTracker.INSTANCE.trackConfig(new ModConfigImpl(type, pair.getRight(), FabricLoader.getInstance().getModContainer(modId).orElseThrow(() -> new IllegalStateException("Unknown mod: " + modId)), fileName));
        return pair.getLeft();
    }
    
    public static Optional<ModConfigImpl> get(String modId, ModConfig.Type type) {
        return ConfigTracker.INSTANCE.getConfig(modId, type);
    }
    
    public static Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }
    
    public static Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static Environment.Loader getLoader() {
        return Environment.Loader.FABRIC;
    }
}