package com.blackgear.platform;

import com.blackgear.platform.common.valueproviders.FloatProviderType;
import com.blackgear.platform.common.valueproviders.IntProviderType;
import com.blackgear.platform.core.registry.PlatformRegistries;
import com.blackgear.platform.core.tags.PlatformTags;
import com.blackgear.platform.common.worldgen.modifier.BiomeManager;
import com.blackgear.platform.core.ModInstance;
import com.blackgear.platform.core.util.config.ConfigLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Platform {
	public static final String MOD_ID = "platform";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final ModInstance INSTANCE = ModInstance.create(MOD_ID).build();

	public static void bootstrap() {
		INSTANCE.bootstrap();
		
		PlatformRegistries.bootstrap();
		
		IntProviderType.PROVIDERS.register();
		FloatProviderType.PROVIDERS.register();
		
		PlatformTags.TAGS.instance();
		
		ConfigLoader.bootstrap();
		BiomeManager.bootstrap();
	}
}