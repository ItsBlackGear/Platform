package com.blackgear.platform.core.mixin.client.emissive;

import com.blackgear.platform.client.v2.emissive.SpriteResourceLoaderContext;
import com.blackgear.platform.client.v2.emissive.EmissiveSpriteHolder;
import com.blackgear.platform.client.v2.emissive.EmissiveSpriteLoader;
import com.blackgear.platform.client.v2.emissive.SpriteLoaderStitchContext;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(SpriteLoader.class)
public class SpriteLoaderMixin {
    @Shadow @Final private ResourceLocation location;

    @ModifyArg(
        method = "loadAndStitch(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/ResourceLocation;ILjava/util/concurrent/Executor;Ljava/util/Collection;)Ljava/util/concurrent/CompletableFuture;",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/concurrent/CompletableFuture;supplyAsync(Ljava/util/function/Supplier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
            ordinal = 0
        ),
        index = 0
    )
    private Supplier<List<Supplier<SpriteContents>>> platform$wrapContents(Supplier<List<Supplier<SpriteContents>>> contents) {
        EmissiveSpriteLoader context = EmissiveSpriteLoader.THREAD_LOCAL.get();
        if (context == null) return contents;

        EmissiveSpriteLoader.Controller control = context.getController(location);
        if (control == null) return contents;

        return () -> {
            SpriteResourceLoaderContext.THREAD_LOCAL.set(control::setEmissiveMappings);
            try {
                return contents.get();
            } finally {
                SpriteResourceLoaderContext.THREAD_LOCAL.remove();
            }
        };
    }

    @ModifyArg(
        method = "loadAndStitch(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/ResourceLocation;ILjava/util/concurrent/Executor;Ljava/util/Collection;)Ljava/util/concurrent/CompletableFuture;",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/concurrent/CompletableFuture;thenApply(Ljava/util/function/Function;)Ljava/util/concurrent/CompletableFuture;",
            ordinal = 0
        ),
        index = 0
    )
    private Function<List<SpriteContents>, SpriteLoader.Preparations> platform$wrapPreparations(Function<List<SpriteContents>, SpriteLoader.Preparations> preparations) {
        EmissiveSpriteLoader context = EmissiveSpriteLoader.THREAD_LOCAL.get();
        if (context == null) return preparations;

        EmissiveSpriteLoader.Controller control = context.getController(this.location);
        if (control == null) return preparations;

        return contents -> {
            Map<ResourceLocation, ResourceLocation> emissiveMappings = control.getEmissiveMappings();
            if (emissiveMappings == null) return preparations.apply(contents);

            SpriteLoaderStitchContext stitch = new SpriteLoaderStitchContext() {
                @Override
                public Map<ResourceLocation, ResourceLocation> getEmissiveMappings() {
                    return emissiveMappings;
                }

                @Override
                public void markHasEmissives() {
                    control.markHasEmissives();
                }
            };

            SpriteLoaderStitchContext.THREAD_LOCAL.set(stitch);
            try {
                return preparations.apply(contents);
            } finally {
                SpriteLoaderStitchContext.THREAD_LOCAL.remove();
            }
        };
    }

    @Inject(
        method = "stitch",
        at = @At("RETURN")
    )
    private void platform$linkEmissiveSprites(List<SpriteContents> spriteContentsList, int mipmapLevels, Executor executor, CallbackInfoReturnable<SpriteLoader.Preparations> cir) {
        SpriteLoaderStitchContext context = SpriteLoaderStitchContext.THREAD_LOCAL.get();
        if (context == null) return;

        Map<ResourceLocation, ResourceLocation> emissiveMappings = context.getEmissiveMappings();
        Map<ResourceLocation, TextureAtlasSprite> sprites = cir.getReturnValue().regions();

        emissiveMappings.forEach((id, emissiveId) -> {
            TextureAtlasSprite originalSprite = sprites.get(id);
            if (originalSprite == null) return;

            TextureAtlasSprite emissiveSprite = sprites.get(emissiveId);
            if (emissiveSprite != null) {
                ((EmissiveSpriteHolder) originalSprite).setEmissiveSprite(emissiveSprite);
                context.markHasEmissives();
            }
        });
    }
}