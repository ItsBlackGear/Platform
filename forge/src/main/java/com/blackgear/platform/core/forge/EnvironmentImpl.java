package com.blackgear.platform.core.forge;

import com.blackgear.platform.core.util.config.ConfigBuilder;
import com.blackgear.platform.core.util.config.forge.ForgeConfigBuilder;
import com.blackgear.platform.core.util.config.ModConfig.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.util.Locale;
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
        return ModList.get().isLoaded(modId);
    }
    
    public static Optional<MinecraftServer> getCurrentServer() {
        return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer());
    }
    
    public static BlockableEventLoop<?> getGameExecutor() {
        return LogicalSidedProvider.WORKQUEUE.get(EffectiveSide.get());
    }
    
    public static Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }
    
    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }
    
    public static <T> T registerSafeConfig(String modId, Type type, Function<ConfigBuilder, T> spec) {
        ModLoadingContext context = ModLoadingContext.get();
        String fileName = String.format("%s-%s.toml", modId, type.name().toLowerCase(Locale.ROOT));
        
        Pair<T, ForgeConfigSpec> pair = new ForgeConfigBuilder(new ForgeConfigSpec.Builder()).configure(spec);
        ModConfig config = new ModConfig(forge(type), pair.getRight(), context.getActiveContainer(), fileName);
        context.getActiveContainer().addConfig(config);
        
        return pair.getLeft();
    }
    
    public static <T> T registerSafeConfig(String modId, Type type, String fileName, Function<ConfigBuilder, T> spec) {
        ModLoadingContext context = ModLoadingContext.get();
        
        Pair<T, ForgeConfigSpec> pair = new ForgeConfigBuilder(new ForgeConfigSpec.Builder()).configure(spec);
        ModConfig config = new ModConfig(forge(type), pair.getRight(), context.getActiveContainer(), fileName);
        context.getActiveContainer().addConfig(config);
        
        return pair.getLeft();
    }
    
    private static ModConfig.Type forge(Type type) {
        switch (type) {
            case COMMON:
                return ModConfig.Type.COMMON;
            case CLIENT:
                return ModConfig.Type.CLIENT;
            case SERVER:
                return ModConfig.Type.SERVER;
            default:
                throw new UnsupportedOperationException("Unknown config type: " + type);
        }
    }
}