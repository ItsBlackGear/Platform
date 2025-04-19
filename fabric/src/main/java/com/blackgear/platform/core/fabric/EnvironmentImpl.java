package com.blackgear.platform.core.fabric;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.util.config.*;
import com.blackgear.platform.fabric.PlatformFabric;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class EnvironmentImpl {
    public static boolean isClientSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    public static boolean isProduction() {
        return !FabricLoader.getInstance().isDevelopmentEnvironment();
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
            try {
                // Use the client-only executor via reflection to avoid class loading on server
                Class<?> clazz = Class.forName("com.blackgear.platform.core.fabric.ClientEnvironmentImpl");
                java.lang.reflect.Field field = clazz.getField("CLIENT_EXECUTOR");
                @SuppressWarnings("unchecked")
                java.util.function.Supplier<BlockableEventLoop<?>> supplier = (java.util.function.Supplier<BlockableEventLoop<?>>) field.get(null);
                return supplier.get();
            } catch (Exception exception) {
                throw new IllegalStateException("Failed to get client executor", exception);
            }
        } else {
            return Environment.getCurrentServer().orElseThrow(() -> new IllegalStateException("No server executor available"));
        }
    }

    public static Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    public static Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static <T> T registerSafeConfig(String modId, ModConfig.Type type, Function<ConfigBuilder, T> spec) {
        Pair<T, SimpleConfigSpec> pair = new SimpleConfigBuilder().configure(spec);
        new ModConfig(type, pair.getRight(), modId);
        return pair.getLeft();
    }

    public static <T> T registerSafeConfig(String modId, ModConfig.Type type, String fileName, Function<ConfigBuilder, T> spec) {
        Pair<T, SimpleConfigSpec> pair = new SimpleConfigBuilder().configure(spec);
        new ModConfig(type, pair.getRight(), modId, fileName);
        return pair.getLeft();
    }

    public static Environment.Loader getLoader() {
        return Environment.Loader.FABRIC;
    }
}