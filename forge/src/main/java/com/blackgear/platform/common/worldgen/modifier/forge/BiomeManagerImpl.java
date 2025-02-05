package com.blackgear.platform.common.worldgen.modifier.forge;

import com.blackgear.platform.common.worldgen.modifier.BiomeContext;
import com.blackgear.platform.common.worldgen.modifier.BiomeManager;
import com.blackgear.platform.common.worldgen.modifier.BiomeWriter;
import com.blackgear.platform.Platform;
import com.blackgear.platform.core.util.WorldGenSerialization;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
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

import java.util.Arrays;
import java.util.function.Predicate;

public class BiomeManagerImpl {
    @Nullable
    private static Codec<PlatformBiomeModifier> codec = null;
    
    public static void bootstrap() {
        FMLJavaModLoadingContext.get().getModEventBus().<RegisterEvent>addListener(event -> {
            event.register(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, entry -> entry.register(Platform.resource("biome_modifier_codec"), codec = Codec.unit(PlatformBiomeModifier.INSTANCE)));
            event.register(ForgeRegistries.Keys.BIOME_MODIFIERS, entry -> entry.register(Platform.resource("biome_modifier"), PlatformBiomeModifier.INSTANCE));
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
            return biome.unwrapKey().orElseThrow().location();
        }

        @Override
        public BiomeContext context() {
            return new BiomeContext() {
                @Override
                public ResourceKey<Biome> resource() {
                    return biome.unwrapKey().orElseThrow();
                }
                
                @Override
                public Biome biome() {
                    return ForgeRegistries.BIOMES.getValue(name());
                }
                
                @Override
                public boolean is(TagKey<Biome> tag) {
                    return biome.is(tag);
                }
                
                @Override
                public boolean is(ResourceKey<Biome> biome) {
                    return this.resource() == biome;
                }
                
                @Override
                public boolean is(Predicate<BiomeContext> context) {
                    return context.test(this);
                }

                @Override
                public boolean hasFeature(Holder<PlacedFeature> feature) {
                    return Arrays.stream(GenerationStep.Decoration.values())
                        .anyMatch(decoration -> builder.getGenerationSettings()
                        .getFeatures(decoration)
                        .stream()
                        .anyMatch(match -> WorldGenSerialization.serializeAndCompareFeature(match.get(), feature.get())));
                }
            };
        }
        
        @Override
        public void addFeature(GenerationStep.Decoration decoration, Holder<PlacedFeature> feature) {
            this.builder.getGenerationSettings().addFeature(decoration, feature);
        }

        @Override
        public void removeFeature(GenerationStep.Decoration decoration, Holder<PlacedFeature> feature) {
            this.builder.getGenerationSettings()
                .getFeatures(decoration)
                .removeIf(match -> WorldGenSerialization.serializeAndCompareFeature(match.get(), feature.get()));
        }

        @Override
        public void addCarver(GenerationStep.Carving carving, Holder<? extends ConfiguredWorldCarver<?>> carver) {
            this.builder.getGenerationSettings().addCarver(carving, carver);
        }

        @Override
        public void removeCarver(GenerationStep.Carving carving, Holder<? extends ConfiguredWorldCarver<?>> carver) {
            this.builder.getGenerationSettings()
                .getCarvers(carving)
                .removeIf(match -> WorldGenSerialization.serializeAndCompareCarver(match.get(), carver.get()));
        }

        @Override
        public void addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data) {
            this.builder.getMobSpawnSettings().addSpawn(category, data);
        }

        @Override
        public void removeSpawn(EntityType<?> entity) {
            this.builder.getMobSpawnSettings()
                .getSpawner(entity.getCategory())
                .removeIf(spawner -> spawner.type == entity);
        }
    }
}