package com.blackgear.platform;

import com.blackgear.platform.common.worldgen.modifier.BiomeManager;
import com.blackgear.platform.core.ModInstance;
import com.blackgear.platform.core.util.config.ConfigLoader;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Platform {
	public static final String MOD_ID = "platform";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final ModInstance INSTANCE = ModInstance.create(MOD_ID).build();

	public static void bootstrap() {
		INSTANCE.bootstrap();

		ConfigLoader.bootstrap();
		BiomeManager.bootstrap();
	}

	public static ResourceLocation resource(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}