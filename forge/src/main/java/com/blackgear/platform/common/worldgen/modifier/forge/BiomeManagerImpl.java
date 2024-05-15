package com.blackgear.platform.common.worldgen.modifier.forge;

import com.blackgear.platform.common.worldgen.modifier.BiomeContext;
import com.blackgear.platform.common.worldgen.modifier.BiomeManager;
import com.blackgear.platform.common.worldgen.modifier.BiomeWriter;
import com.blackgear.platform.Platform;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
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
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

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
                BiomeManager.INSTANCE.register(new ForgeBiomeWriter(biome.unwrapKey(), builder));
            }
        }
        
        @Override
        public Codec<? extends BiomeModifier> codec() {
            return codec != null ? codec : Codec.unit(INSTANCE);
        }
    }
    
    static class ForgeBiomeWriter extends BiomeWriter {
        private final Optional<ResourceKey<Biome>> biome;
        private final ModifiableBiomeInfo.BiomeInfo.Builder builder;
        
        ForgeBiomeWriter(Optional<ResourceKey<Biome>> biome, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            this.biome = biome;
            this.builder = builder;
        }
        
        @Override
        public ResourceLocation name() {
            return ForgeBiomeWriter.this.biome.get().location();
        }

        @Override
        public BiomeContext context() {
            return new BiomeContext() {
                @Override
                public ResourceKey<Biome> key() {
                    return ForgeBiomeWriter.this.biome.get();
                }
                
                @Override
                public Biome biome() {
                    return ForgeRegistries.BIOMES.getValue(ForgeBiomeWriter.this.name());
                }
                
                @Override
                public boolean is(TagKey<Biome> tag) {
                    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                    if (server != null) {
                        server.registryAccess()
                            .registry(Registries.BIOME)
                            .flatMap(biomes -> biomes.getHolder(ForgeBiomeWriter.this.biome.get()))
                            .ifPresent(holder -> holder.is(tag));
                    }
                    
                    return false;
                }
                
                @Override
                public boolean is(ResourceKey<Biome> biome) {
                    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                    if (server != null) {
                        server.registryAccess()
                            .registry(Registries.BIOME)
                            .flatMap(biomes -> biomes.getHolder(ForgeBiomeWriter.this.biome.get()))
                            .ifPresent(holder -> holder.is(biome));
                    }
                    
                    return false;
                }
                
                @Override
                public boolean is(Predicate<BiomeContext> context) {
                    return context.test(this);
                }
            };
        }
        
        @Override
        public void addFeature(GenerationStep.Decoration decoration, ResourceKey<PlacedFeature> feature) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                server.registryAccess()
                    .registry(Registries.PLACED_FEATURE)
                    .flatMap(features -> features.getHolder(feature))
                    .ifPresent(placedFeature -> {
                        this.builder.getGenerationSettings().addFeature(decoration, placedFeature);
                    });
            }
        }
        
        @Override
        public void addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data) {
            this.builder.getMobSpawnSettings().addSpawn(category, data);
        }
        
        @Override
        public void addCarver(GenerationStep.Carving carving, ResourceKey<ConfiguredWorldCarver<?>> carver) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                server.registryAccess()
                    .registry(Registries.CONFIGURED_CARVER)
                    .flatMap(carvers -> carvers.getHolder(carver))
                    .ifPresent(configuredCarver -> {
                        this.builder.getGenerationSettings().addCarver(carving, configuredCarver);
                    });
            }
        }
    }
}