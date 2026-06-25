package com.blackgear.platform.core.neoforge;

import com.blackgear.platform.core.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.common.util.LogicalSidedProvider;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class EnvironmentImpl {
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
    
    public static Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }
    
    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static Environment.Loader getLoader() {
        return Environment.Loader.FORGE;
    }
}