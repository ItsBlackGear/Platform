package com.blackgear.platform.core.neoforge;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.util.config.ConfigBuilder;
import com.blackgear.platform.core.util.config.ModConfig.Type;
import com.blackgear.platform.core.util.config.neoforge.ForgeConfigBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.util.LogicalSidedProvider;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class EnvironmentImpl {
    public static boolean isClientSide() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }
    
    public static boolean isProduction() {
        return !FMLLoader.isProduction();
    }
    
    public static boolean hasModLoaded(String modId) {
        Objects.requireNonNull(modId, "Mod ID cannot be null");
        return ModList.get().isLoaded(modId);
    }

    public static String getModVersion(String modId) {
        Objects.requireNonNull(modId, "Mod ID cannot be null");
        return ModList.get().getModContainerById(modId)
            .map(container -> container.getModInfo().getVersion().toString())
            .orElse(null);
    }

    public static Optional<MinecraftServer> getCurrentServer() {
        return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer());
    }
    
    public static BlockableEventLoop<?> getGameExecutor() {
        try {
            return LogicalSidedProvider.WORKQUEUE.get(EffectiveSide.get());
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to get game executor", exception);
        }
    }
    
    public static Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }
    
    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }
    
    public static <T> T registerSafeConfig(String modId, Type type, Function<ConfigBuilder, T> spec) {
        ModLoadingContext context = ModLoadingContext.get();
        if (context == null || context.getActiveContainer() == null) {
            throw new IllegalStateException("Cannot register config outside of mod loading context");
        }

        String fileName = String.format("%s-%s.toml", modId, type.name().toLowerCase(Locale.ROOT));

        Pair<T, ModConfigSpec> pair = new ForgeConfigBuilder(new ModConfigSpec.Builder()).configure(spec);
        context.getActiveContainer().registerConfig(forge(type), pair.getRight(), fileName);

        return pair.getLeft();
    }
    
    public static <T> T registerSafeConfig(String modId, Type type, String fileName, Function<ConfigBuilder, T> spec) {
        ModLoadingContext context = ModLoadingContext.get();
        if (context == null || context.getActiveContainer() == null) {
            throw new IllegalStateException("Cannot register config outside of mod loading context");
        }

        Pair<T, ModConfigSpec> pair = new ForgeConfigBuilder(new ModConfigSpec.Builder()).configure(spec);
        context.getActiveContainer().registerConfig(forge(type), pair.getRight(), fileName);

        return pair.getLeft();
    }
    
    private static ModConfig.Type forge(Type type) {
        return switch (type) {
            case COMMON -> ModConfig.Type.COMMON;
            case CLIENT -> ModConfig.Type.CLIENT;
            case SERVER -> ModConfig.Type.SERVER;
        };
    }

    public static Environment.Loader getLoader() {
        return Environment.Loader.FORGE;
    }
}