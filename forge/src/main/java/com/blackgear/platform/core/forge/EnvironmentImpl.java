package com.blackgear.platform.core.forge;

import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.events.ConfigEvents;
import com.blackgear.platform.core.mixin.forge.access.ModContainerAccessor;
import com.blackgear.platform.core.util.EventBus;
import com.blackgear.platform.core.util.config.ConfigBuilder;
import com.blackgear.platform.core.util.config.ModConfig;
import com.blackgear.platform.core.util.config.forge.ForgeConfigBuilder;
import com.blackgear.platform.core.util.config.forge.ModConfigImpl;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;
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
            
            Pair<T, ForgeConfigSpec> pair = new ForgeConfigBuilder(new ForgeConfigSpec.Builder()).configure(consumer);
            net.minecraftforge.fml.config.ModConfig config = new net.minecraftforge.fml.config.ModConfig(convert(type), pair.getRight(), context.getContainer(), fileName);
            context.getContainer().addConfig(config);
            
            if (registeredEvents.add(modId)) {
                bus.<ModConfigEvent.Loading>addListener(event -> {
                    net.minecraftforge.fml.config.ModConfig modConfig = event.getConfig();
                    get(modConfig.getModId(), convert(modConfig.getType())).ifPresent(c -> ConfigEvents.LOADING.invoker().accept(c));
                });
                bus.<ModConfigEvent.Reloading>addListener(event -> {
                    net.minecraftforge.fml.config.ModConfig modConfig = event.getConfig();
                    get(modConfig.getModId(), convert(modConfig.getType())).ifPresent(c -> ConfigEvents.RELOADING.invoker().accept(c));
                });
            }
            
            CONFIGS.computeIfAbsent(modId, __ -> new EnumMap<>(ModConfig.Type.class)).put(type, new ModConfigImpl(config));
            return pair.getLeft();
        }
        
        public static Optional<ModConfig> get(String modId, ModConfig.Type type) {
            Map<ModConfig.Type, ModConfig> map = CONFIGS.computeIfAbsent(modId, __ -> {
                EnumMap<ModConfig.Type, ModConfig> newMap = new EnumMap<>(ModConfig.Type.class);
                getRawConfigData(modId).ifPresent(forgeMap -> forgeMap.forEach((forgeType, config) -> newMap.put(convert(forgeType), new ModConfigImpl(config))));
                return newMap;
            });
            return Optional.ofNullable(map.get(type));
        }
        
        static Optional<EnumMap<Type, net.minecraftforge.fml.config.ModConfig>> getRawConfigData(String modId) {
            return ModList.get().getModContainerById(modId).map(container -> ((ModContainerAccessor) container).getConfigs());
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
                case COMMON -> ModConfig.Type.COMMON;
                case CLIENT -> ModConfig.Type.CLIENT;
                case SERVER -> ModConfig.Type.SERVER;
            };
        }
    }
}