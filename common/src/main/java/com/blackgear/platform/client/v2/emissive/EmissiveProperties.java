package com.blackgear.platform.client.v2.emissive;

import com.blackgear.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmissiveProperties {
    public static final ResourceLocation PROPERTIES = new ResourceLocation("optifine/emissive.properties");
    private static String suffix;

    @Nullable
    public static String getSuffix() {
        return suffix;
    }

    public static void load(ResourceManager manager) {
        suffix = null;
        manager.getResource(PROPERTIES).ifPresent(resource -> {
            try (InputStream stream = resource.open()) {
                Properties properties = new Properties();
                properties.load(stream);
                suffix = properties.getProperty("suffix.emissive");
            } catch (IOException exception) {
                Platform.LOGGER.error("Failed to load emissive suffix from file '{}'", PROPERTIES, exception);
            }
        });
    }
}