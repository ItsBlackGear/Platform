package com.blackgear.platform.common.worldgen.modifier.fabric;

import com.blackgear.platform.common.worldgen.modifier.BiomeContext;
import com.blackgear.platform.common.worldgen.modifier.BiomeManager;
import com.blackgear.platform.common.worldgen.modifier.BiomeWriter;
import com.blackgear.platform.Platform;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
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

public class BiomeManagerImpl {
    public static void bootstrap() {
        BiomeModifications.create(new ResourceLocation(Platform.MOD_ID, "biome_modifier"))
            .add(
                ModificationPhase.ADDITIONS,
                context -> true,
                (selector, modifier) -> BiomeManager.INSTANCE.register(new FabricBiomeWriter(selector, modifier))
            );
    }

    static class FabricBiomeWriter extends BiomeWriter {
        private final BiomeSelectionContext selector;
        private final BiomeModificationContext modifier;

        FabricBiomeWriter(BiomeSelectionContext selector, BiomeModificationContext modifier) {
            this.selector = selector;
            this.modifier = modifier;
        }

        @Override
        public ResourceLocation name() {
            return this.selector.getBiomeKey().location();
        }

        @Override
        public BiomeContext context() {
            return new BiomeContext() {
                @Override
                public boolean is(TagKey<Biome> tag) {
                    return FabricBiomeWriter.this.selector.hasTag(tag);
                }

                @Override
                public boolean is(ResourceKey<Biome> biome) {
                    return FabricBiomeWriter.this.selector.getBiomeKey() == biome;
                }
            };
        }

        @Override
        public void addFeature(GenerationStep.Decoration decoration, ResourceKey<PlacedFeature> feature) {
            this.modifier.getGenerationSettings().addFeature(decoration, feature);
        }

        @Override
        public void addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data) {
            this.modifier.getSpawnSettings().addSpawn(category, data);
        }

        @Override
        public void addCarver(GenerationStep.Carving carving, ResourceKey<ConfiguredWorldCarver<?>> carver) {
            this.modifier.getGenerationSettings().addCarver(carving, carver);
        }
    }
}