package com.blackgear.platform.core;

import com.blackgear.platform.core.util.config.*;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

public class Environment {
    @ExpectPlatform
    public static boolean isClientSide() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isProduction() {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static boolean hasModLoaded(String modId) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Optional<MinecraftServer> getCurrentServer() {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static BlockableEventLoop<?> getGameExecutor() {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Path getGameDir() {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static Path getConfigDir() {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static <T> T registerSafeConfig(String modId, ModConfig.Type type, Function<ConfigBuilder, T> spec) {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static <T> T registerSafeConfig(String modId, ModConfig.Type type, String fileName, Function<ConfigBuilder, T> spec) {
        throw new AssertionError();
    }
    
    /**
     * Registers a config that generates on early stages of the mod.
     * This doesn't affect fabric but may work differently on forge.
     */
    public static <T> T registerUnsafeConfig(String modId, ModConfig.Type type, Function<ConfigBuilder, T> spec) {
        Pair<T, SimpleConfigSpec> pair = new SimpleConfigBuilder().configure(spec);
        new ModConfig(type, pair.getRight(), modId);
        return pair.getLeft();
    }
    
    /**
     * Registers a config that generates on early stages of the mod.
     * This doesn't affect fabric but may work differently on forge.
     */
    public static <T> T registerUnsafeConfig(String modId, ModConfig.Type type, String fileName, Function<ConfigBuilder, T> spec) {
        Pair<T, SimpleConfigSpec> pair = new SimpleConfigBuilder().configure(spec);
        new ModConfig(type, pair.getRight(), modId, fileName);
        return pair.getLeft();
    }
}