package com.blackgear.platform.common.worldgen.modifier.neoforge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.worldgen.modifier.BiomeContext;
import com.blackgear.platform.common.worldgen.modifier.BiomeManager;
import com.blackgear.platform.common.worldgen.modifier.BiomeWriter;
import com.blackgear.platform.core.Environment;
import com.blackgear.platform.core.util.EventBus;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class BiomeManagerImpl {
    @Nullable private static MapCodec<PlatformBiomeModifier> codec = null;
    
    public static void bootstrap() {
        EventBus.get(EventBus.MOD).<RegisterEvent>addListener(event -> {
            event.register(
                NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS,
                entry -> entry.register(Platform.resource("biome_modifier_codec"),
                codec = MapCodec.unit(PlatformBiomeModifier.INSTANCE))
            );
            event.register(
                NeoForgeRegistries.Keys.BIOME_MODIFIERS,
                entry -> entry.register(Platform.resource("biome_modifier"),
                PlatformBiomeModifier.INSTANCE)
            );
        });
    }
    
    static class PlatformBiomeModifier implements BiomeModifier {
        private static final PlatformBiomeModifier INSTANCE = new PlatformBiomeModifier();
        
        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.ADD) BiomeManager.INSTANCE.register(new ForgeBiomeWriter(biome, builder));
        }

        @Override
        public MapCodec<? extends BiomeModifier> codec() {
            return codec != null ? codec : MapCodec.unit(INSTANCE);
        }
    }
    
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static class ForgeBiomeWriter extends BiomeWriter {
        private final Holder<Biome> biome;
        private final ModifiableBiomeInfo.BiomeInfo.Builder builder;
        private final Optional<RegistryAccess> registries;

        ForgeBiomeWriter(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            Optional<MinecraftServer> server = Environment.getCurrentServer();
            this.registries = server.map(MinecraftServer::registryAccess);
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
                    return biome.value();
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
                public boolean hasFeature(ResourceKey<PlacedFeature> key) {
                    List<HolderSet<PlacedFeature>> features = builder.getGenerationSettings().build().features();

                    for (HolderSet<PlacedFeature> featureSet : features) {
                        for (Holder<PlacedFeature> feature : featureSet) {
                            Optional<ResourceKey<PlacedFeature>> featureKey = feature.unwrapKey();
                            if (featureKey.isPresent() && featureKey.get() == key) {
                                return true;
                            }
                        }
                    }

                    return false;
                }
            };
        }

        @Override
        public void addFeature(GenerationStep.Decoration decoration, ResourceKey<PlacedFeature> key) {
            this.registries.ifPresent(registry -> {
                Registry<PlacedFeature> features = registry.registryOrThrow(Registries.PLACED_FEATURE);
                builder.getGenerationSettings().addFeature(decoration, features.getHolderOrThrow(key));
            });
        }

        @Override
        public void removeFeature(GenerationStep.Decoration decoration, ResourceKey<PlacedFeature> key) {
            this.registries.ifPresent(registry -> {
                Registry<PlacedFeature> features = registry.registryOrThrow(Registries.PLACED_FEATURE);
                builder.getGenerationSettings()
                    .getFeatures(decoration)
                    .removeIf(holder -> holder.value() == features.getOrThrow(key));
            });
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

        @Override
        public void addCarver(GenerationStep.Carving carving, ResourceKey<ConfiguredWorldCarver<?>> key) {
            this.registries.ifPresent(registry -> {
                Registry<ConfiguredWorldCarver<?>> carvers = registry.registryOrThrow(Registries.CONFIGURED_CARVER);
                builder.getGenerationSettings().addCarver(carving, carvers.getHolderOrThrow(key));
            });
        }

        @Override
        public void removeCarver(GenerationStep.Carving carving, ResourceKey<ConfiguredWorldCarver<?>> key) {
            this.registries.ifPresent(registry -> {
                Registry<ConfiguredWorldCarver<?>> carvers = registry.registryOrThrow(Registries.CONFIGURED_CARVER);
                builder.getGenerationSettings()
                    .getCarvers(carving)
                    .removeIf(holder -> holder.value() == carvers.getOrThrow(key));
            });
        }
    }
}