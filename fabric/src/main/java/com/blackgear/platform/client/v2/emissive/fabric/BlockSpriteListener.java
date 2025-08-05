package com.blackgear.platform.client.v2.emissive.fabric;

import com.blackgear.platform.Platform;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Collection;
import java.util.List;

public class BlockSpriteListener implements SimpleSynchronousResourceReloadListener {
    public static final ResourceLocation ID = Platform.resource("block_sprite");
    private static final BlockSpriteListener INSTANCE = new BlockSpriteListener();
    private static SpriteFinder sprites;

    public static void init() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(INSTANCE);
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        ModelManager models = Minecraft.getInstance().getModelManager();
        sprites = SpriteFinder.get(models.getAtlas(TextureAtlas.LOCATION_BLOCKS));
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public Collection<ResourceLocation> getFabricDependencies() {
        return List.of(ResourceReloadListenerKeys.MODELS);
    }

    public static SpriteFinder getSprites() {
        return sprites;
    }
}