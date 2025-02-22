package com.blackgear.platform;

import com.blackgear.platform.client.PlatformClient;
import com.blackgear.platform.common.providers.height.HeightProviderType;
import com.blackgear.platform.common.providers.math.FloatProviderType;
import com.blackgear.platform.common.providers.math.IntProviderType;
import com.blackgear.platform.common.registry.PlatformDecorators;
import com.blackgear.platform.common.registry.PlatformFeatures;
import com.blackgear.platform.core.registry.PlatformBlockEntities;
import com.blackgear.platform.core.registry.PlatformRegistries;
import com.blackgear.platform.core.tags.PBlockTags;
import com.blackgear.platform.common.worldgen.modifier.BiomeManager;
import com.blackgear.platform.core.ModInstance;
import com.blackgear.platform.core.util.config.ConfigLoader;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Platform {
	public static final String MOD_ID = "platform";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final ModInstance INSTANCE = ModInstance.create(MOD_ID)
		.postClient(PlatformClient::onInstance)
		.build();

	public static void bootstrap() {
		INSTANCE.bootstrap();
		
		IntProviderType.PROVIDERS.register();
		FloatProviderType.PROVIDERS.register();
		HeightProviderType.PROVIDERS.register();
		
		PlatformBlockEntities.BLOCK_ENTITIES.register();

		PlatformFeatures.FEATURES.register();
		PlatformDecorators.DECORATOR.register();

		PlatformRegistries.BUILDER.bootstrap();
		PBlockTags.TAGS.instance();
		
		ConfigLoader.bootstrap();
		BiomeManager.bootstrap();
	}

	public static ResourceLocation resource(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}