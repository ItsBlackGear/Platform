package com.blackgear.platform.core.forge;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.mixin.forge.access.ModContainerAccessor;
import com.blackgear.platform.core.util.config.ConfigBuilder;
import com.blackgear.platform.core.util.config.ModConfig;
import com.blackgear.platform.core.util.config.forge.ForgeConfigBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class EnvironmentImpl {
    private static final Map<String, Map<net.minecraftforge.fml.config.ModConfig.Type, net.minecraftforge.fml.config.ModConfig>> CONFIGS = new ConcurrentHashMap<>();
    
    public static boolean isClientSide() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }
    
    public static boolean isProduction() {
        return !FMLLoader.isProduction();
    }

    public static boolean hasModLoaded(String modId) {
        Objects.requireNonNull(modId, "Mod ID cannot be null");
        ModList modList = ModList.get();
        return modList != null && modList.isLoaded(modId);
    }

    public static String getModVersion(String modId) {
        Objects.requireNonNull(modId, "Mod ID cannot be null");
        ModList modList = ModList.get();
        return modList != null
            ? modList.getModContainerById(modId)
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse(null)
            : null;
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
    
    public static <T> T registerConfig(String modId, ModConfig.Type type, String fileName, Function<ConfigBuilder, T> spec) {
        ModLoadingContext context = ModLoadingContext.get();
        
        Pair<T, ForgeConfigSpec> pair = new ForgeConfigBuilder(new ForgeConfigSpec.Builder()).configure(spec);
        net.minecraftforge.fml.config.ModConfig config = new net.minecraftforge.fml.config.ModConfig(convert(type), pair.getRight(), context.getActiveContainer(), fileName);
        context.getActiveContainer().addConfig(config);
        
        CONFIGS.computeIfAbsent(modId, __ -> new EnumMap<>(net.minecraftforge.fml.config.ModConfig.Type.class)).put(convert(type), config);
        return pair.getLeft();
    }
    
    public static Optional<net.minecraftforge.fml.config.ModConfig> get(String modId, ModConfig.Type type) {
        Map<net.minecraftforge.fml.config.ModConfig.Type, net.minecraftforge.fml.config.ModConfig> map = CONFIGS.computeIfAbsent(modId, __ -> {
            EnumMap<net.minecraftforge.fml.config.ModConfig.Type, net.minecraftforge.fml.config.ModConfig> newMap = new EnumMap<>(net.minecraftforge.fml.config.ModConfig.Type.class);
            getRawConfigData(modId).ifPresent(newMap::putAll);
            return newMap;
        });
        return Optional.ofNullable(map.get(type));
    }
    
    private static Optional<EnumMap<net.minecraftforge.fml.config.ModConfig.Type, net.minecraftforge.fml.config.ModConfig>> getRawConfigData(String modId) {
        return ModList.get().getModContainerById(modId).map(o -> ((ModContainerAccessor)o).getConfigs());
    }
    
    public static Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }
    
    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static Environment.Loader getLoader() {
        return Environment.Loader.FORGE;
    }
    
    public static net.minecraftforge.fml.config.ModConfig.Type convert(ModConfig.Type type) {
        return switch (type) {
            case COMMON -> net.minecraftforge.fml.config.ModConfig.Type.COMMON;
            case CLIENT -> net.minecraftforge.fml.config.ModConfig.Type.CLIENT;
            case SERVER -> net.minecraftforge.fml.config.ModConfig.Type.SERVER;
        };
    }
    
    public static ModConfig.Type convert(net.minecraftforge.fml.config.ModConfig.Type type) {
        return switch (type) {
            case COMMON -> ModConfig.Type.COMMON;
            case CLIENT -> ModConfig.Type.CLIENT;
            case SERVER -> ModConfig.Type.SERVER;
        };
    }
}