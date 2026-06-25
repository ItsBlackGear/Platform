package com.blackgear.platform;

import com.blackgear.platform.common.CommonSetup;
import com.blackgear.platform.common.resource.RegistryAwareJsonReloadListener;
import com.blackgear.platform.common.worldgen.modifier.BiomeManager;
import com.blackgear.platform.core.ModInstance;
import com.blackgear.platform.core.helper.AttachmentRegistry;
import com.blackgear.platform.core.networking.packet.ClientboundConfigSyncPayload;
import com.blackgear.platform.core.networking.Networking;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Platform {
	public static final String MOD_ID = "platform";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final ModInstance INSTANCE = ModInstance.create(MOD_ID)
		.common(CommonSetup::setup)
		.build();

	public static void bootstrap() {
		INSTANCE.bootstrap();

		Networking.register(registrar -> registrar.registerToClient(ClientboundConfigSyncPayload.TYPE, ClientboundConfigSyncPayload.STREAM_CODEC, ClientboundConfigSyncPayload::handler));

		BiomeManager.bootstrap();
		AttachmentRegistry.bootstrap();
	}

	public static void afterDataReload(RegistryAccess registryAccess, boolean client) {
		RegistryAwareJsonReloadListener.runReloads(registryAccess);
	}

	public static ResourceLocation resource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}