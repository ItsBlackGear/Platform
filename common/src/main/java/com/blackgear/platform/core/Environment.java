package com.blackgear.platform.core;

import com.blackgear.platform.core.util.config.ConfigBuilder;
import com.blackgear.platform.core.util.config.ModConfig;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

/**
 * Provides platform-independent access to environment information and utilities.
 * Handles both Forge and Fabric implementations through platform-specific code.
 */
public class Environment {
    /**
     * Checks if the current environment is client-side.
     *
     * @return true if running on client-side, false otherwise
     */
    @ExpectPlatform
    public static boolean isClientSide() {
        throw new AssertionError();
    }

    /**
     * Checks if the environment is in production mode.
     *
     * @return true if in production, false if in development
     */
    @ExpectPlatform
    public static boolean isProduction() {
        throw new AssertionError();
    }
    
    /**
     * Checks if the environment is in development mode.
     *
     * @return true if in development, false if in production
     */
    @ExpectPlatform
    public static boolean isDevelopment() {
        throw new AssertionError();
    }

    /**
     * Checks if a mod with the specified ID is loaded.
     *
     * @param modId the mod ID to check
     * @return true if the mod is loaded, false otherwise
     */
    @ExpectPlatform
    public static boolean hasModLoaded(String modId) {
        throw new AssertionError();
    }

    /**
     * Gets the version of the specified mod.
     *
     * @param modId the mod ID
     * @return the mod version as a string, or null if the mod is not found
     */
    @ExpectPlatform
    public static String getModVersion(String modId) {
        throw new AssertionError();
    }

    /**
     * Gets the current Minecraft server instance, if available.
     *
     * @return an Optional containing the server instance, or empty if unavailable
     */
    @ExpectPlatform
    public static Optional<MinecraftServer> getCurrentServer() {
        throw new AssertionError();
    }
    
    /**
     * Gets the current Registry Access, if available.
     *
     * @return an Optional containing the server's Registry Access, or empty if unavailable
     */
    public static Optional<RegistryAccess> getRegistryAccess() {
        return getCurrentServer().map(MinecraftServer::registryAccess);
    }
    
    /**
     * Gets the game's main thread executor.
     *
     * @return the game executor
     * @throws IllegalStateException if no executor is available
     */
    @ExpectPlatform
    public static BlockableEventLoop<?> getGameExecutor() {
        throw new AssertionError();
    }

    /**
     * Gets the game directory path.
     *
     * @return the game directory path
     */
    @ExpectPlatform
    public static Path getGameDir() {
        throw new AssertionError();
    }

    /**
     * Gets the config directory path.
     *
     * @return the config directory path
     */
    @ExpectPlatform
    public static Path getConfigDir() {
        throw new AssertionError();
    }

    /**
     * Registers a configuration with a custom filename.
     *
     * @param modId the mod ID for the configuration
     * @param type the configuration type
     * @param fileName the custom filename
     * @param spec the configuration specification builder
     * @param <T> the type of configuration object
     * @return the created configuration object
     */
    @ExpectPlatform
    public static <T> T registerConfig(String modId, ModConfig.Type type, String fileName, Function<ConfigBuilder, T> spec) {
        throw new AssertionError();
    }
    
    /**
     * Registers a configuration.
     *
     * @param modId the mod ID for the configuration
     * @param type the configuration type
     * @param spec the configuration specification builder
     * @param <T> the type of configuration object
     * @return the created configuration object
     */
    public static <T> T registerConfig(String modId, ModConfig.Type type, Function<ConfigBuilder, T> spec) {
        return registerConfig(modId, type, defaultConfigName(type, modId), spec);
    }
    
    @ExpectPlatform
    public static Optional<ModConfig> get(String modId, ModConfig.Type type) {
        throw new AssertionError();
    }

    private static String defaultConfigName(ModConfig.Type type, String modId) {
        return String.format("%s-%s.toml", modId, type.extension());
    }

    /**
     * Gets the current mod loader type.
     *
     * @return the Loader enum representing the current mod loader
     */
    @ExpectPlatform
    public static Loader getLoader() {
        throw new AssertionError();
    }

    /**
     * Checks if the current environment is using Forge.
     *
     * @return true if using Forge, false otherwise
     */
    public static boolean isForge() {
        return getLoader() == Loader.FORGE;
    }

    /**
     * Checks if the current environment is using Fabric.
     *
     * @return true if using Fabric, false otherwise
     */
    public static boolean isFabric() {
        return getLoader() == Loader.FABRIC;
    }

    /**
     * Enum representing the supported mod loaders.
     */
    public enum Loader { FORGE, FABRIC }
    
    // For Removal
    
    @Deprecated(forRemoval = true)
    public static <T> T registerSafeConfig(String modId, ModConfig.Type type, Function<ConfigBuilder, T> spec) {
        return registerConfig(modId, type, spec);
    }
    
    @Deprecated(forRemoval = true)
    public static <T> T registerSafeConfig(String modId, ModConfig.Type type, String fileName, Function<ConfigBuilder, T> spec) {
        return registerConfig(modId, type, fileName, spec);
    }
    
    @Deprecated(forRemoval = true)
    public static <T> T registerUnsafeConfig(String modId, ModConfig.Type type, String fileName, Function<ConfigBuilder, T> spec) {
        return registerConfig(modId, type, fileName, spec);
    }
    
    @Deprecated(forRemoval = true)
    public static <T> T registerUnsafeConfig(String modId, ModConfig.Type type, Function<ConfigBuilder, T> spec) {
        return registerConfig(modId, type, spec);
    }
}