package com.blackgear.platform.core.neoforge;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.events.ConfigEvents;
import com.blackgear.platform.core.util.EventBus;
import com.blackgear.platform.core.util.config.ConfigBuilder;
import com.blackgear.platform.core.util.config.ModConfig;
import com.blackgear.platform.core.util.config.neoforge.ForgeConfigBuilder;
import com.blackgear.platform.core.util.config.neoforge.ModConfigImpl;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.util.LogicalSidedProvider;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class EnvironmentImpl {
    public static boolean isClientSide() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }
    
    public static boolean isProduction() {
        return FMLLoader.isProduction();
    }
    
    public static boolean isDevelopment() {
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
        return ForgeConfigHandler.register(modId, type, fileName, spec);
    }
    
    public static Optional<ModConfig> get(String modId, ModConfig.Type type) {
        return ForgeConfigHandler.get(modId, type);
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
    
    public static class ForgeConfigHandler {
        private static final Map<String, Map<ModConfig.Type, ModConfig>> CONFIGS = new ConcurrentHashMap<>();
        private static final Set<String> registeredEvents = ConcurrentHashMap.newKeySet();
        
        public static <T> T register(String modId, ModConfig.Type type, String fileName, Function<ConfigBuilder, T> consumer) {
            ModLoadingContext context = ModLoadingContext.get();
            IEventBus bus = EventBus.get(modId);
            
            Pair<T, ModConfigSpec> pair = new ForgeConfigBuilder(new ModConfigSpec.Builder()).configure(consumer);
            net.neoforged.fml.config.ModConfig config = ConfigTracker.INSTANCE.registerConfig(convert(type), pair.getRight(), context.getActiveContainer(), fileName);
            
            if (registeredEvents.add(modId)) {
                bus.<ModConfigEvent.Loading>addListener(event -> {
                    net.neoforged.fml.config.ModConfig modConfig = event.getConfig();
                    get(modConfig.getModId(), convert(modConfig.getType())).ifPresent(c -> ConfigEvents.LOADING.invoker().accept(c));
                });
                bus.<ModConfigEvent.Reloading>addListener(event -> {
                    net.neoforged.fml.config.ModConfig modConfig = event.getConfig();
                    get(modConfig.getModId(), convert(modConfig.getType())).ifPresent(c -> ConfigEvents.RELOADING.invoker().accept(c));
                });
            }
            
            CONFIGS.computeIfAbsent(modId, __ -> new EnumMap<>(ModConfig.Type.class)).put(type, new ModConfigImpl(config));
            return pair.getLeft();
        }
        
        public static Optional<ModConfig> get(String modId, ModConfig.Type type) {
            return !CONFIGS.containsKey(modId) ? Optional.empty() : Optional.ofNullable(CONFIGS.get(modId).get(type));
        }
        
        public static Type convert(ModConfig.Type type) {
            return switch (type) {
                case COMMON -> Type.COMMON;
                case CLIENT -> Type.CLIENT;
                case SERVER -> Type.SERVER;
            };
        }
        
        public static ModConfig.Type convert(Type type) {
            return switch (type) {
                case COMMON, STARTUP -> ModConfig.Type.COMMON;
                case CLIENT -> ModConfig.Type.CLIENT;
                case SERVER -> ModConfig.Type.SERVER;
            };
        }
    }
}