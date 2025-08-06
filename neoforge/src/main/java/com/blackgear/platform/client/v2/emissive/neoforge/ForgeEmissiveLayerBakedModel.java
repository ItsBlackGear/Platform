package com.blackgear.platform.client.v2.emissive.neoforge;


import com.blackgear.platform.client.v2.emissive.Emissiveness;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ForgeEmissiveLayerBakedModel implements BakedModel {
    private static final Direction[] ALL_DIRECTIONS = { null, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN };
    private static final ConcurrentHashMap<BlockState, RenderType> DEFAULT_RENDER_TYPE_CACHE = new ConcurrentHashMap<>();
    private static final int EMISSIVE_BRIGHTNESS = 0x00F000F0;
    private static final int VERTEX_SIZE = 8;
    private static final int VERTICES_PER_QUAD = 4;
    private static final int U_OFFSET = 4;
    private static final int V_OFFSET = 5;
    private static final int BRIGHTNESS_OFFSET = 6;

    private final BakedModel wrapped;

    public ForgeEmissiveLayerBakedModel(BakedModel wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource random, @NotNull ModelData data, @Nullable RenderType renderType) {
        List<BakedQuad> originalQuads = this.wrapped.getQuads(state, side, random, data, renderType);
        if (originalQuads.isEmpty()) return originalQuads;

        List<BakedQuad> emissiveQuads = this.createEmissiveQuads(originalQuads, state, renderType);
        return emissiveQuads.isEmpty()
            ? originalQuads
            : ImmutableList.<BakedQuad>builder()
            .addAll(originalQuads)
            .addAll(emissiveQuads)
            .build();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        List<BakedQuad> originalQuads = this.wrapped.getQuads(state, direction, random);
        if (originalQuads.isEmpty()) return originalQuads;

        if (state == null) {
            List<BakedQuad> emissiveQuads = this.createItemEmissiveQuads(originalQuads);
            if (!emissiveQuads.isEmpty()) {
                return ImmutableList.<BakedQuad>builder()
                    .addAll(originalQuads)
                    .addAll(emissiveQuads)
                    .build();
            }
        }

        return originalQuads;
    }

    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        ChunkRenderTypeSet originalTypes = this.wrapped.getRenderTypes(state, rand, data);
        if (!this.hasEmissiveQuads(state, rand, data)) return originalTypes;

        Set<RenderType> renderTypes = new HashSet<>();
        originalTypes.forEach(renderTypes::add);

        if (originalTypes.contains(RenderType.solid())) {
            renderTypes.add(RenderType.cutoutMipped());
        }

        return ChunkRenderTypeSet.of(renderTypes);
    }

    @Override
    public List<BakedModel> getRenderPasses(@NotNull ItemStack stack, boolean fabulous) {
        List<BakedModel> originalPasses = this.wrapped.getRenderPasses(stack, fabulous);

        if (originalPasses.size() == 1 && originalPasses.get(0) == this.wrapped) {
            return Collections.singletonList(this);
        }

        return originalPasses.stream()
            .map(model -> {
                if (model instanceof ForgeEmissiveLayerBakedModel || !hasEmissiveSprites(model)) {
                    return model;
                }
                return new ForgeEmissiveLayerBakedModel(model);
            })
            .collect(Collectors.toList());
    }

    @Override
    public @NotNull List<RenderType> getRenderTypes(@NotNull ItemStack stack, boolean fabulous) {
        List<RenderType> originalTypes = this.wrapped.getRenderTypes(stack, fabulous);

        if (this.hasEmissiveSpritesForItem()) {
            Set<RenderType> types = new HashSet<>(originalTypes);
            if (
                !types.contains(RenderType.cutout()) &&
                    !types.contains(RenderType.cutoutMipped()) &&
                    !types.contains(RenderType.translucent())
            ) {
                types.add(RenderType.cutout());
            }
            return new ArrayList<>(types);
        }

        return originalTypes;
    }

    private List<BakedQuad> createEmissiveQuads(List<BakedQuad> originalQuads, @Nullable BlockState state, @Nullable RenderType renderType) {
        List<BakedQuad> emissiveQuads = new ArrayList<>();

        for (BakedQuad quad : originalQuads) {
            TextureAtlasSprite sprite = quad.getSprite();
            TextureAtlasSprite emissiveSprite = Emissiveness.INSTANCE.getEmissiveSprite(sprite);
            if (emissiveSprite != null) {
                RenderType emissiveRenderType = this.getEmissiveRenderType(state, renderType);
                if (renderType == null || renderType == emissiveRenderType) {
                    emissiveQuads.add(this.createEmissiveQuad(quad, emissiveSprite));
                }
            }
        }

        return emissiveQuads;
    }

    private List<BakedQuad> createItemEmissiveQuads(List<BakedQuad> originalQuads) {
        if (originalQuads.isEmpty()) return Collections.emptyList();

        List<BakedQuad> emissiveQuads = new ArrayList<>();

        for (BakedQuad quad : originalQuads) {
            TextureAtlasSprite sprite = quad.getSprite();
            TextureAtlasSprite emissiveSprite = Emissiveness.INSTANCE.getEmissiveSprite(sprite);
            if (emissiveSprite != null) {
                emissiveQuads.add(this.createEmissiveQuad(quad, emissiveSprite));
            }
        }

        return emissiveQuads;
    }

    private boolean hasEmissiveQuads(@NotNull BlockState state, @NotNull RandomSource random, @NotNull ModelData data) {
        for (Direction direction : ALL_DIRECTIONS) {
            for (BakedQuad quad : wrapped.getQuads(state, direction, random, data, null)) {
                if (Emissiveness.INSTANCE.getEmissiveSprite(quad.getSprite()) != null) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasEmissiveSpritesForItem() {
        for (Direction direction : ALL_DIRECTIONS) {
            for (BakedQuad quad : this.wrapped.getQuads(null, direction, RandomSource.create())) {
                if (Emissiveness.INSTANCE.getEmissiveSprite(quad.getSprite()) != null) {
                    return true;
                }
            }
        }

        return false;
    }

    private RenderType getEmissiveRenderType(@Nullable BlockState state, @Nullable RenderType origin) {
        if (origin == RenderType.solid()) {
            return RenderType.cutoutMipped();
        }

        if (origin == RenderType.cutout() || origin == RenderType.cutoutMipped() || origin == RenderType.translucent()) {
            return origin;
        }

        if (state != null) {
            RenderType defaultType = DEFAULT_RENDER_TYPE_CACHE.computeIfAbsent(state, ItemBlockRenderTypes::getChunkRenderType);
            return defaultType == RenderType.solid() ? RenderType.cutoutMipped() : RenderType.cutout();
        }

        return RenderType.cutout();
    }

    private BakedQuad createEmissiveQuad(BakedQuad originalQuad, TextureAtlasSprite emissiveSprite) {
        int[] emissiveVertices = this.interpolateUVAndBrightness(
            originalQuad.getVertices(),
            originalQuad.getSprite(),
            emissiveSprite
        );

        return new BakedQuad(
            emissiveVertices,
            originalQuad.getTintIndex(),
            originalQuad.getDirection(),
            emissiveSprite,
            originalQuad.isShade()
        );
    }

    private int[] interpolateUVAndBrightness(int[] originalVertices, TextureAtlasSprite baseSprite, TextureAtlasSprite emissiveSprite) {
        int[] newVertices = originalVertices.clone();

        float baseU0 = baseSprite.getU0();
        float baseV0 = baseSprite.getV0();
        float baseURange = baseSprite.getU1() - baseU0;
        float baseVRange = baseSprite.getV1() - baseV0;
        float emissiveU0 = emissiveSprite.getU0();
        float emissiveV0 = emissiveSprite.getV0();
        float emissiveURange = emissiveSprite.getU1() - emissiveU0;
        float emissiveVRange = emissiveSprite.getV1() - emissiveV0;

        for (int vertex = 0; vertex < VERTICES_PER_QUAD; vertex++) {
            int vertexStart = vertex * VERTEX_SIZE;

            float originalU = Float.intBitsToFloat(newVertices[vertexStart + U_OFFSET]);
            float originalV = Float.intBitsToFloat(newVertices[vertexStart + V_OFFSET]);

            float normalizedU = baseURange > 0 ? (originalU - baseU0) / baseURange : 0;
            float normalizedV = baseVRange > 0 ? (originalV - baseV0) / baseVRange : 0;

            float emissiveU = emissiveU0 + normalizedU * emissiveURange;
            float emissiveV = emissiveV0 + normalizedV * emissiveVRange;

            newVertices[vertexStart + U_OFFSET] = Float.floatToRawIntBits(emissiveU);
            newVertices[vertexStart + V_OFFSET] = Float.floatToRawIntBits(emissiveV);
            newVertices[vertexStart + BRIGHTNESS_OFFSET] = EMISSIVE_BRIGHTNESS;
        }

        return newVertices;
    }

    public static boolean shouldWrapModel(BakedModel model) {
        if (model == null || model instanceof ForgeEmissiveLayerBakedModel) return false;

        try {
            if (model.isCustomRenderer()) return false;

            List<BakedModel> renderPasses = model.getRenderPasses(ItemStack.EMPTY, false);
            if (renderPasses.size() > 1) return false;

            return hasEmissiveSprites(model);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean hasEmissiveSprites(BakedModel model) {
        if (model instanceof ForgeEmissiveLayerBakedModel) {
            return false;
        }

        try {
            RandomSource random = RandomSource.create();
            for (Direction direction : ALL_DIRECTIONS) {
                List<BakedQuad> quads = model.getQuads(null, direction, random);
                for (BakedQuad quad : quads) {
                    if (quad != null) {
                        if (Emissiveness.INSTANCE.getEmissiveSprite(quad.getSprite()) != null) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception exception) {
            return false;
        }

        return false;
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        return this.wrapped.getModelData(level, pos, state, modelData);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.wrapped.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.wrapped.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.wrapped.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return this.wrapped.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.wrapped.getParticleIcon();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
        return this.wrapped.getParticleIcon(data);
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.wrapped.getOverrides();
    }

    @Override
    public ItemTransforms getTransforms() {
        return this.wrapped.getTransforms();
    }
}