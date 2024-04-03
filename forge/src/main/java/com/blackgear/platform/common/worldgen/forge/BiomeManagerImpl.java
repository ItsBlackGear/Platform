package com.blackgear.platform.common.worldgen.forge;

import com.blackgear.platform.common.worldgen.BiomeContext;
import com.blackgear.platform.common.worldgen.BiomeManager;
import com.blackgear.platform.common.worldgen.BiomeWriter;
import com.blackgear.platform.core.Platform;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class BiomeManagerImpl {
    @Nullable
    private static Codec<PlatformBiomeModifier> codec = null;

    public static void bootstrap() {
        FMLJavaModLoadingContext.get().getModEventBus().<RegisterEvent>addListener(event -> {
            event.register(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, entry -> {
                entry.register(new ResourceLocation(Platform.MOD_ID, "biome_modifier_codec"), codec = Codec.unit(PlatformBiomeModifier.INSTANCE));
            });
            event.register(ForgeRegistries.Keys.BIOME_MODIFIERS, entry -> {
                entry.register(new ResourceLocation(Platform.MOD_ID, "biome_modifier"), PlatformBiomeModifier.INSTANCE);
            });
        });
    }

    static class PlatformBiomeModifier implements BiomeModifier {
        private static final PlatformBiomeModifier INSTANCE = new PlatformBiomeModifier();

        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.ADD) {
                BiomeManager.INSTANCE.register(new ForgeBiomeWriter(biome, builder));
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec() {
            return codec != null ? codec : Codec.unit(INSTANCE);
        }
    }

    static class ForgeBiomeWriter extends BiomeWriter {
        private final Holder<Biome> biome;
        private final ModifiableBiomeInfo.BiomeInfo.Builder builder;

        ForgeBiomeWriter(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            this.biome = biome;
            this.builder = builder;
        }

        @Override
        public ResourceLocation name() {
            return ForgeBiomeWriter.this.biome.unwrapKey().get().location();
        }

        @Override
        public BiomeContext context() {
            return new BiomeContext() {
                @Override
                public boolean is(TagKey<Biome> tag) {
                    return ForgeBiomeWriter.this.biome.is(tag);
                }

                @Override
                public boolean is(ResourceKey<Biome> biome) {
                    return ForgeBiomeWriter.this.biome.is(biome);
                }
            };
        }

        @Override
        public void addFeature(GenerationStep.Decoration decoration, Holder<PlacedFeature> feature) {
            this.builder.getGenerationSettings().addFeature(decoration, feature);
        }

        @Override
        public void addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data) {
            this.builder.getMobSpawnSettings().addSpawn(category, data);
        }

        @Override
        public void addCarver(GenerationStep.Carving carving, Holder<? extends ConfiguredWorldCarver<?>> carver) {
            this.builder.getGenerationSettings().addCarver(carving, carver);
        }
    }
}