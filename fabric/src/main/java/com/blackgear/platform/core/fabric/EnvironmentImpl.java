package com.blackgear.platform.core.fabric;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.util.config.*;
import com.blackgear.platform.fabric.PlatformFabric;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
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
    
    public static boolean hasModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
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
}