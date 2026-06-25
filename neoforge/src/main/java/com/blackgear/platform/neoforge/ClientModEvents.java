package com.blackgear.platform.neoforge;

import com.blackgear.platform.Platform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(modid = Platform.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    public static ShaderInstance envFogDebugShader;

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            // Registramos el core shader vinculándolo al formato de bloques
            event.registerShader(
                    new ShaderInstance(
                            event.getResourceProvider(),
                            ResourceLocation.fromNamespaceAndPath("minecraft", "rendertype_solid"),
                            DefaultVertexFormat.BLOCK
                    ),
                    shader -> envFogDebugShader = shader
            );
        } catch (IOException e) {
            throw new RuntimeException("Error crítico cargando Core Shader de niebla", e);
        }
    }
}