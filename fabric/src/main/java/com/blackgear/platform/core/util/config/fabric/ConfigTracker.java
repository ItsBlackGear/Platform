package com.blackgear.platform.core.util.config.fabric;

import com.blackgear.platform.core.events.ConfigEvents;
import com.blackgear.platform.core.network.packet.ClientboundConfigSyncPacket;
import com.blackgear.platform.core.util.config.ModConfig;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ConfigTracker {
    public static final ConfigTracker INSTANCE = new ConfigTracker();
    public static final Logger LOGGER = LogManager.getLogger();
    private final ConcurrentHashMap<String, ModConfigImpl> fileMap = new ConcurrentHashMap<>();
    private final EnumMap<ModConfig.Type, Set<ModConfigImpl>> configSets = new EnumMap<>(ModConfig.Type.class);
    private final ConcurrentHashMap<String, Map<ModConfig.Type, ModConfigImpl>> configsByMod = new ConcurrentHashMap<>();

    private ConfigTracker() {
        for (ModConfig.Type type : ModConfig.Type.values()) {
            this.configSets.put(type, Collections.synchronizedSet(new LinkedHashSet<>()));
        }
    }
    
    public void trackConfig(ModConfigImpl config) {
        if (this.fileMap.containsKey(config.getFileName())) {
            LOGGER.error("Detected config file conflict {} between {} and {}", config.getFileName(), this.fileMap.get(config.getFileName()).getModId(), config.getModId());
            throw new RuntimeException("Config conflict detected!");
        }

        this.fileMap.put(config.getFileName(), config);
        this.configSets.get(config.getType()).add(config);
        this.configsByMod.computeIfAbsent(config.getModId(), (k) -> new EnumMap<>(ModConfig.Type.class)).put(config.getType(), config);
        LOGGER.debug("Config file {} for {} tracking", config.getFileName(), config.getModId());
    }

    public void loadConfigs(ModConfig.Type type, Path configBasePath) {
        LOGGER.debug("Loading configs type {}", type);
        this.configSets.get(type).forEach(config -> openConfig(config, configBasePath));
    }
    
    public void unloadConfigs(ModConfig.Type type, Path configBasePath) {
        LOGGER.debug("Unloading configs type {}", type);
        this.configSets.get(type).forEach(config -> closeConfig(config, configBasePath));
    }

    public List<Pair<String, ClientboundConfigSyncPacket>> syncConfigs(boolean isLocal) { // only sync configs for players joining and if the config actually exists
        return isLocal ? Collections.emptyList() : this.configSets.get(ModConfig.Type.SERVER).stream().filter(mc -> mc.getFullPath() != null).map(mc -> {
            try {
                return Pair.of("Config " + mc.getFileName(), new ClientboundConfigSyncPacket(mc.getFileName(), Files.readAllBytes(mc.getFullPath())));
            } catch (Exception exception) {
                LOGGER.error("Failed to sync {} config for {}", mc.getType(), mc.getModId(), exception);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private void openConfig(ModConfigImpl config, Path configBasePath) {
        CommentedFileConfig configData = config.getHandler().reader(configBasePath).apply(config);
        config.setConfigData(configData);
        ConfigEvents.LOADING.invoker().accept(config);
        config.save();
    }
    
    private void closeConfig(ModConfigImpl config, Path configBasePath) {
        if (config.getConfigData() != null) {
            config.save();
            config.getHandler().unload(configBasePath, config);
            config.setConfigData(null);
        }
    }

    public void receiveSyncedConfig(ClientboundConfigSyncPacket packet) {
        if (!Minecraft.getInstance().isLocalServer() && this.fileMap.containsKey(packet.name())) {
            ModConfigImpl config = this.fileMap.get(packet.name());
            config.setConfigData(TomlFormat.instance().createParser().parse(new ByteArrayInputStream(packet.data())));
            ConfigEvents.RELOADING.invoker().accept(config);
        }
    }

    public void loadDefaultServerConfigs() {
        this.configSets.get(ModConfig.Type.SERVER).forEach(config -> {
            CommentedConfig commentedConfig = CommentedConfig.inMemory();
            config.getSpec().correct(commentedConfig);
            config.setConfigData(commentedConfig);
            ConfigEvents.LOADING.invoker().accept(config);
        });
    }

    @Nullable
    public String getConfigFileName(String modId, ModConfig.Type type) {
        return Optional.ofNullable(this.configsByMod.getOrDefault(modId, Collections.emptyMap()).getOrDefault(type, null)).flatMap(config -> Optional.ofNullable(config.getFullPath())).map(Object::toString).orElse(null);
    }

    public Optional<ModConfigImpl> getConfig(String modId, ModConfig.Type type) {
        return Optional.ofNullable(this.configsByMod.getOrDefault(modId, Collections.emptyMap()).getOrDefault(type, null));
    }
}