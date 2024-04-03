package com.blackgear.platform;

import com.blackgear.platform.common.worldgen.BiomeManager;
import com.blackgear.platform.core.ModInstance;
import com.blackgear.platform.core.RegistryBuilder;
import com.blackgear.platform.core.registry.PlatformDataSerializers;
import com.blackgear.platform.core.registry.PlatformRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Platform {
	public static final String MOD_ID = "platform";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final ModInstance INSTANCE = ModInstance.create(MOD_ID).build();

	public static void bootstrap() {
		INSTANCE.bootstrap();

		RegistryBuilder.bootstrap();
		PlatformDataSerializers.bootstrap();
		BiomeManager.bootstrap();
	}
}