package com.blackgear.platform.client;

import com.blackgear.platform.client.v2.render.HandHeldItemRenderer;
import com.blackgear.platform.client.v2.render.ItemRendererRegistry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class GameRendering {
    public static final Map<ResourceLocation, ResourceLocation> MODEL_OVERRIDES = new ConcurrentHashMap<>();

    @ExpectPlatform
    public static void registerBlockColors(Consumer<BlockColorEvent> listener) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerItemColors(Consumer<ItemColorEvent> listener) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerBlockRenderers(Consumer<BlockRendererEvent> listener) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerEntityRenderers(Consumer<EntityRendererEvent> listener) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerBlockEntityRenderers(Consumer<BlockEntityRendererEvent> listener) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerModelLayers(Consumer<ModelLayerEvent> listener) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerSpecialModels(Consumer<SpecialModelEvent> listener) {
        throw new AssertionError();
    }

    public static void registerHandHeldModels(Consumer<HandHeldModelEvent> listener) {
        HandHeldModelEvent event = (item, original, handHeld, perspectives) -> ItemRendererRegistry.INSTANCE.get().register(item, new HandHeldItemRenderer(original, handHeld));
        listener.accept(event);
    }

    @ExpectPlatform
    public static void registerModelOverrides(Consumer<ModelOverrideEvent> listener) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerSkullRenderers(Consumer<SkullRendererEvent> listener) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerParticleFactories(Consumer<ParticleFactoryEvent> listener) {
        throw new AssertionError();
    }

    public interface BlockColorEvent {
        void register(BlockColor color, Block... blocks);

        int getColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tint);
    }

    public interface ItemColorEvent {
        void register(ItemColor color, ItemLike... items);

        int getColor(ItemStack stack, int tint);
    }

    public interface BlockRendererEvent {
        void register(RenderType type, Block... blocks);

        void register(RenderType type, Fluid... fluids);
    }

    public interface EntityRendererEvent {
        <E extends Entity> void register(EntityType<? extends E> type, EntityRendererProvider<E> renderer);
    }

    public interface BlockEntityRendererEvent {
        <E extends BlockEntity> void register(BlockEntityType<? extends E> type, BlockEntityRendererProvider<E> renderer);
    }

    public interface ModelLayerEvent {
        void register(ModelLayerLocation layer, Supplier<LayerDefinition> definition);
    }

    public interface SpecialModelEvent {
        void register(ResourceLocation model);

        void register(ResourceLocation... models);
    }

    public interface ModelOverrideEvent {
        default void register(ResourceLocation original, ResourceLocation override, boolean condition) {
            if (condition) {
                MODEL_OVERRIDES.put(original, override);
            } else {
                MODEL_OVERRIDES.remove(original);
            }
        }

        default void register(ResourceLocation original, ResourceLocation override) {
            this.register(original, override, true);
        }
    }

    public interface HandHeldModelEvent {
        default void register(Item item, ModelResourceLocation handHeld) {
            this.register(item, new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(item), "inventory"), handHeld);
        }

        void register(Item item, ModelResourceLocation original, ModelResourceLocation handheld, Set<ItemDisplayContext> perspectives);

        default void register(Item item, ResourceLocation original, ResourceLocation handheld, Set<ItemDisplayContext> perspectives) {
            this.register(item, new ModelResourceLocation(original, "inventory"), new ModelResourceLocation(handheld, "inventory"), perspectives);
        }

        default void register(Item item, ModelResourceLocation original, ModelResourceLocation handheld) {
            this.register(item, original, handheld, Set.of(ItemDisplayContext.GUI, ItemDisplayContext.GROUND, ItemDisplayContext.FIXED));
        }

        default void register(Item item, ResourceLocation original, ResourceLocation handheld) {
            this.register(item, new ModelResourceLocation(original, "inventory"), new ModelResourceLocation(handheld, "inventory"));
        }

        record Models(ModelResourceLocation original, ModelResourceLocation handheld, Set<ItemDisplayContext> perspectives) {}
    }

    public interface SkullRendererEvent {
        void registerSkullModel(SkullBlock.Type type, Function<ModelPart, SkullModelBase> model, ModelLayerLocation layer);

        void registerSkullTexture(SkullBlock.Type type, ResourceLocation texture);
    }

    public interface ParticleFactoryEvent {
        <T extends ParticleOptions, P extends ParticleType<T>> void register(Supplier<P> type, ParticleProvider<T> provider);

        <T extends ParticleOptions, P extends ParticleType<T>> void register(Supplier<P> type, Factory<T> factory);

        @FunctionalInterface
        interface Factory<T extends ParticleOptions> {
            @NotNull ParticleProvider<T> create(SpriteSet sprites);
        }
    }
}