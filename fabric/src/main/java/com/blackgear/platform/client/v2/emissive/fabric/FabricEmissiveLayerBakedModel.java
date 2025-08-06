package com.blackgear.platform.client.v2.emissive.fabric;

import com.blackgear.platform.client.v2.emissive.Emissiveness;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class FabricEmissiveLayerBakedModel extends ForwardingBakedModel {
    private static RenderMaterial[] emissiveMaterials;

    private static final ConcurrentHashMap<BlockState, Boolean> SOLID_RENDER_TYPE_CACHE = new ConcurrentHashMap<>();

    public FabricEmissiveLayerBakedModel(BakedModel wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        this.processQuads(
            context,
            () -> super.emitBlockQuads(blockView, state, pos, randomSupplier, context),
            emitter -> new EmissiveBlockQuadTransform(emitter, state, context)
        );
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        this.processQuads(
            context,
            () -> super.emitItemQuads(stack, randomSupplier, context),
            EmissiveItemQuadTransform::new
        );
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    private static RenderMaterial[] getEmissiveMaterials() {
        if (emissiveMaterials == null) {
            emissiveMaterials = createEmissiveMaterials();
        }
        return emissiveMaterials;
    }

    private static RenderMaterial[] createEmissiveMaterials() {
        RenderMaterial[] materials = new RenderMaterial[BlendMode.values().length];
        MaterialFinder finder = RendererAccess.INSTANCE.getRenderer().materialFinder();

        for (BlendMode mode : BlendMode.values()) {
            materials[mode.ordinal()] = finder.emissive(true).disableDiffuse(true).ambientOcclusion(TriState.FALSE).blendMode(mode).find();
        }

        return materials;
    }

    private static boolean isDefaultLayerSolid(BlockState state) {
        return SOLID_RENDER_TYPE_CACHE.computeIfAbsent(state, s -> ItemBlockRenderTypes.getChunkRenderType(s) == RenderType.solid());
    }

    private void processQuads(RenderContext context, Runnable originalEmitter, Function<QuadEmitter, ? extends EmissiveQuadTransform> factory) {
        MeshBuilder meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        EmissiveQuadTransform transformer = factory.apply(meshBuilder.getEmitter());

        context.pushTransform(transformer);
        try {
            originalEmitter.run();
        } finally { // Ensure transform is always popped, even if exception occurs
            context.popTransform();
        }

        if (transformer.didEmit()) {
            meshBuilder.build().outputTo(context.getEmitter());
        }
    }

    private static void interpolateUV(MutableQuadView quad, TextureAtlasSprite from, TextureAtlasSprite to) {
        float fromU = from.getU0();
        float fromV = from.getV0();
        float toU = to.getU0();
        float toV = to.getV0();
        float scaleU = (to.getU1() - toU) / (from.getU1() - fromU);
        float scaleV = (to.getV1() - toV) / (from.getV1() - fromV);

        quad.uv(0, toU + (quad.u(0) - fromU) * scaleU, toV + (quad.v(0) - fromV) * scaleV);
        quad.uv(1, toU + (quad.u(1) - fromU) * scaleU, toV + (quad.v(1) - fromV) * scaleV);
        quad.uv(2, toU + (quad.u(2) - fromU) * scaleU, toV + (quad.v(2) - fromV) * scaleV);
        quad.uv(3, toU + (quad.u(3) - fromU) * scaleU, toV + (quad.v(3) - fromV) * scaleV);
    }

    private abstract static class EmissiveQuadTransform implements RenderContext.QuadTransform {
        protected final QuadEmitter emitter;
        private int emittedCount = 0;

        protected EmissiveQuadTransform(QuadEmitter emitter) {
            this.emitter = emitter;
        }

        public final boolean didEmit() {
            return this.emittedCount > 0;
        }

        protected final void emitEmissiveQuad(MutableQuadView quad, TextureAtlasSprite originalSprite, TextureAtlasSprite emissiveSprite, RenderMaterial material) {
            this.emitter.copyFrom(quad);
            this.emitter.material(material);
            interpolateUV(emitter, originalSprite, emissiveSprite);
            this.emitter.emit();
            this.emittedCount++;
        }

        protected final void tryEmitEmissiveQuad(MutableQuadView quad, RenderMaterial material) {
            TextureAtlasSprite originalSprite = BlockSpriteListener.getSprites().find(quad);
            if (originalSprite == null) return;

            TextureAtlasSprite emissiveSprite = Emissiveness.INSTANCE.getEmissiveSprite(originalSprite);
            if (emissiveSprite != null) {
                emitEmissiveQuad(quad, originalSprite, emissiveSprite, material);
            }
        }
    }

    private static final class EmissiveBlockQuadTransform extends EmissiveQuadTransform {
        private final RenderContext renderContext;
        private final boolean isDefaultLayerSolid;

        private EmissiveBlockQuadTransform(QuadEmitter emitter, BlockState state, RenderContext renderContext) {
            super(emitter);
            this.renderContext = renderContext;
            this.isDefaultLayerSolid = isDefaultLayerSolid(state);
        }

        @Override
        public boolean transform(MutableQuadView quad) {
            if (this.renderContext.isFaceCulled(quad.cullFace())) return false;

            this.tryEmitEmissiveQuad(quad, getEmissiveMaterial(quad.material().blendMode()));
            return true;
        }

        private RenderMaterial getEmissiveMaterial(BlendMode mode) {
            RenderMaterial[] materials = getEmissiveMaterials();
            return switch (mode) {
                case DEFAULT -> getDefaultEmissiveMaterial(materials);
                case SOLID -> materials[BlendMode.CUTOUT_MIPPED.ordinal()];
                default -> materials[mode.ordinal()];
            };
        }

        private RenderMaterial getDefaultEmissiveMaterial(RenderMaterial[] materials) {
            return this.isDefaultLayerSolid ?
                materials[BlendMode.CUTOUT_MIPPED.ordinal()] :
                materials[BlendMode.DEFAULT.ordinal()];
        }
    }

    private static final class EmissiveItemQuadTransform extends EmissiveQuadTransform {
        private EmissiveItemQuadTransform(QuadEmitter emitter) {
            super(emitter);
        }

        @Override
        public boolean transform(MutableQuadView quad) {
            this.tryEmitEmissiveQuad(quad, getEmissiveMaterials()[BlendMode.DEFAULT.ordinal()]);
            return true;
        }
    }
}