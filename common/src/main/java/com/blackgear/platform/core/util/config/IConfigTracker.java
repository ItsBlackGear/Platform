package com.blackgear.platform.core.util.config;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface IConfigTracker {
    Map<ModConfig.Type, Set<ModConfig>> configSets();

    ConcurrentHashMap<String, ModConfig> fileMap();
}