package com.blackgear.platform.core;

import com.blackgear.platform.common.worldgen.BiomeManager;
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