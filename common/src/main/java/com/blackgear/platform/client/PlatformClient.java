package com.blackgear.platform.client;

import com.blackgear.platform.common.registry.PlatformBlockEntities;
import com.blackgear.platform.core.ParallelDispatch;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;

public class PlatformClient {
    public static void onInstance() {
        RendererRegistry.addBlockEntityRenderer(PlatformBlockEntities.SKULL, SkullBlockRenderer::new);
    }

    public static void postInstance(ParallelDispatch dispatch) {

    }
}