package com.blackgear.platform.client.v2.emissive;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class EmissiveModelReloader {
    private final AtomicBoolean shouldWrap = new AtomicBoolean();
    private final EmissiveSpriteLoaderImpl spriteLoader;

    public EmissiveModelReloader(ResourceManager manager) {
        this.spriteLoader = new EmissiveSpriteLoaderImpl(this.shouldWrap);
        EmissiveProperties.load(manager);
    }

    public void setContext() {
        EmissiveSpriteLoader.THREAD_LOCAL.set(this.spriteLoader);
    }

    public void clearContext() {
        EmissiveSpriteLoader.THREAD_LOCAL.remove();
    }

    public void beforeBaking(ModelBakery bakery) {
        EmissiveModelWrapper wrapper = EmissiveModelWrapper.create(this.shouldWrap.get());
        ((EmissiveModelWrapperHolder) bakery).setModelWrapper(wrapper);
    }

    private static class EmissiveSpriteLoaderImpl implements EmissiveSpriteLoader {
        private final Controller controller;

        private EmissiveSpriteLoaderImpl(AtomicBoolean markDirty) {
            this.controller = new ControllerImpl(markDirty);
        }

        @Override
        public @Nullable EmissiveSpriteLoader.Controller getController(ResourceLocation atlas) {
            return atlas.equals(TextureAtlas.LOCATION_BLOCKS) ? this.controller : null;
        }

        private static class ControllerImpl implements Controller {
            @Nullable private volatile Map<ResourceLocation, ResourceLocation> mappings;
            private final AtomicBoolean markDirty;

            private ControllerImpl(AtomicBoolean markDirty) {
                this.markDirty = markDirty;
            }

            @Override
            public @Nullable Map<ResourceLocation, ResourceLocation> getEmissiveMappings() {
                return this.mappings;
            }

            @Override
            public void setEmissiveMappings(@Nullable Map<ResourceLocation, ResourceLocation> mappings) {
                this.mappings = mappings != null ? Map.copyOf(mappings) : null;
            }

            @Override
            public void markHasEmissives() {
                this.markDirty.set(true);
            }
        }
    }
}