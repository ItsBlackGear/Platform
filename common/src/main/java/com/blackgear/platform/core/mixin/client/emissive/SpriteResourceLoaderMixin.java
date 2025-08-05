package com.blackgear.platform.core.mixin.client.emissive;

import com.blackgear.platform.client.v2.emissive.SpriteResourceLoaderContext;
import com.blackgear.platform.client.v2.emissive.EmissiveProperties;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(SpriteResourceLoader.class)
public class SpriteResourceLoaderMixin {
    @Inject(
        method = "list",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableList;builder()Lcom/google/common/collect/ImmutableList$Builder;",
            remap = false
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void platform$onReloadPost(ResourceManager resourceManager, CallbackInfoReturnable<List<Supplier<SpriteContents>>> cir, Map<ResourceLocation, SpriteSource.SpriteSupplier> contents) {
        SpriteResourceLoaderContext context = SpriteResourceLoaderContext.THREAD_LOCAL.get();
        if (context == null) return;

        String suffix = EmissiveProperties.getSuffix();
        if (suffix == null) return;

        Map<ResourceLocation, SpriteSource.SpriteSupplier> emissiveContents = new Object2ObjectOpenHashMap<>();
        Map<ResourceLocation, ResourceLocation> emissiveMappings = new Object2ObjectOpenHashMap<>();

        contents.entrySet()
            .stream()
            .filter(entry -> !entry.getKey().getPath().endsWith(suffix))
            .forEach(entry -> {
                ResourceLocation id = entry.getKey();
                ResourceLocation emissiveId = id.withPath(id.getPath() + suffix);

                if (contents.containsKey(emissiveId)) {
                    emissiveMappings.put(id, emissiveId);
                } else {
                    ResourceLocation emissive = emissiveId.withPath("textures/" + emissiveId.getPath() + ".png");
                    resourceManager.getResource(emissive).ifPresent(resource -> {
                        emissiveContents.put(emissiveId, () -> SpriteLoader.loadSprite(emissive, resource));
                        emissiveMappings.put(id, emissiveId);
                    });
                }
            });

        contents.putAll(emissiveContents);
        if (!emissiveMappings.isEmpty()) {
            context.setEmissiveMappings(emissiveMappings);
        }
    }
}