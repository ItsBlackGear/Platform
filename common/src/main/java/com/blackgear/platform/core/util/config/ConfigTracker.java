package com.blackgear.platform.core.util.config;

import com.blackgear.platform.core.events.ConfigEvents;
import com.blackgear.platform.core.networking.packet.ClientboundConfigSyncPayload;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.mojang.datafixers.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ConfigTracker {
    public static final ConfigTracker INSTANCE = new ConfigTracker();
    public static final Logger LOGGER = LogManager.getLogger();
    private final ConcurrentHashMap<String, ModConfig> fileMap = new ConcurrentHashMap<>();
    private final EnumMap<ModConfig.Type, Set<ModConfig>> configSets = new EnumMap<>(ModConfig.Type.class);
    private final ConcurrentHashMap<String, Map<ModConfig.Type, ModConfig>> configsByMod = new ConcurrentHashMap<>();
    
    private ConfigTracker() {
        for (var type : ModConfig.Type.values()) {
            this.configSets.put(type, Collections.synchronizedSet(new LinkedHashSet<>()));
        }
    }
    
    void trackConfig(ModConfig config) {
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

    public List<Pair<String, ClientboundConfigSyncPayload>> syncConfigs(boolean isLocal) { // only sync configs for players joining and if the config actually exists
        return isLocal ? Collections.emptyList() : this.configSets.get(ModConfig.Type.SERVER).stream().filter(mc -> mc.getFullPath() != null).map(mc -> {
            try {
                return Pair.of("Config " + mc.getFileName(), new ClientboundConfigSyncPayload(mc.getFileName(), Files.readAllBytes(mc.getFullPath())));
            } catch (Exception exception) {
                LOGGER.error("Failed to sync {} config for {}", mc.getType(), mc.getModId(), exception);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private void openConfig(final ModConfig config, final Path configBasePath) {
        LOGGER.trace("Loading config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
        CommentedFileConfig configData = config.getHandler().reader(configBasePath).apply(config);
        config.setConfigData(configData);
        ConfigEvents.LOADING.invoker().onModConfig(config);
        config.save();
    }
    
    private void closeConfig(ModConfig config, Path configBasePath) {
        if (config.getConfigData() != null) {
            config.save();
            config.getHandler().unload(configBasePath, config);
            config.setConfigData(null);
        }
    }

    public void receiveSyncedConfig(ClientboundConfigSyncPayload payload) {
        if (this.fileMap.containsKey(payload.name())) {
            ModConfig config = this.fileMap.get(payload.name());
            config.setConfigData((TomlFormat.instance().createParser().parse(new ByteArrayInputStream(payload.data()))));
            ConfigEvents.RELOADING.invoker().onModConfig(config);
        }
    }

    public void loadDefaultServerConfigs() {
        configSets.get(ModConfig.Type.SERVER).forEach(config -> {
            CommentedConfig commentedConfig = CommentedConfig.inMemory();
            config.getSpec().correct(commentedConfig);
            config.setConfigData(commentedConfig);
            ConfigEvents.LOADING.invoker().onModConfig(config);
        });
    }

    public String getConfigFileName(String modId, ModConfig.Type type) {
        return Optional.ofNullable(this.configsByMod.getOrDefault(modId, Collections.emptyMap()).getOrDefault(type, null))
            .flatMap(config -> Optional.ofNullable(config.getFullPath()))
            .map(Object::toString)
            .orElse(null);
    }

    public Optional<ModConfig> getConfig(String modId, ModConfig.Type type) {
        return Optional.ofNullable(this.configsByMod.getOrDefault(modId, Collections.emptyMap()).getOrDefault(type, null));
    }
}