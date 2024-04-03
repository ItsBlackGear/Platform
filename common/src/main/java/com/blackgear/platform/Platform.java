package com.blackgear.platform;

import com.blackgear.platform.common.worldgen.BiomeManager;
import com.blackgear.platform.core.ModInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Platform {
	public static final String MOD_ID = "platform";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final ModInstance INSTANCE = ModInstance.create(MOD_ID).build();

	public static void bootstrap() {
		INSTANCE.bootstrap();

		BiomeManager.bootstrap();
	}
}