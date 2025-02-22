package com.blackgear.platform.client;

import com.blackgear.platform.core.ParallelDispatch;
import com.blackgear.platform.core.registry.PlatformBlockEntities;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;

public class PlatformClient {
    public static void onInstance(ParallelDispatch dispatch) {
        RendererRegistry.addBlockEntityRenderer(PlatformBlockEntities.SKULL, SkullBlockRenderer::new);
    }
}