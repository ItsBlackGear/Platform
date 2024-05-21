package com.blackgear.platform;

import com.blackgear.platform.client.event.FogRenderEvents;
import com.blackgear.platform.common.worldgen.modifier.BiomeManager;
import com.blackgear.platform.core.ModInstance;
import com.blackgear.platform.core.util.config.ConfigLoader;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class Platform {
	public static final String MOD_ID = "platform";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final ModInstance INSTANCE = ModInstance.create(MOD_ID)
		.client(() -> {
			FogRenderEvents.FOG_COLOR.register((renderer, context, tickDelta) -> {
				BlockState state = context.getStateAtCamera();

				if (state.is(Blocks.GLASS)) {
					context.setRed(0.623F);
					context.setGreen(0.734F);
					context.setBlue(0.785F);
					context.build();
				}
			});
			FogRenderEvents.FOG_RENDERING.register((renderer, context, farPlaneDistance) -> {
				BlockState state = context.getStateAtCamera();
				LocalPlayer player = (LocalPlayer) context.camera().getEntity();
				
				Objects.requireNonNull(player, "Player cannot be null");
				
				if (player.isSpectator() && player.isEyeInFluid(FluidTags.LAVA)) {
					context.fogDensity(0.0225F);
					context.build();
				}
				
				if (state.is(Blocks.GLASS)) {
					if (player.isSpectator()) {
						context.fogStart(-8.0F);
						context.fogEnd(farPlaneDistance * 0.5F);
						context.fogDensity(0.0225F);
					} else {
						context.fogStart(0.0F);
						context.fogEnd(2.0F);
						context.fogDensity(1.0F);
					}
					
					context.fogMode(GlStateManager.FogMode.LINEAR);
					context.setupNvFogDistance();
					context.build();
				}
			});
		})
		.build();

	public static void bootstrap() {
		INSTANCE.bootstrap();

		ConfigLoader.bootstrap();
		BiomeManager.bootstrap();
	}
}