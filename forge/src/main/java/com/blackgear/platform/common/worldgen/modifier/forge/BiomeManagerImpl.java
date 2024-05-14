package com.blackgear.platform.common.worldgen.modifier.forge;

import com.blackgear.platform.Platform;
import com.blackgear.platform.common.worldgen.modifier.BiomeContext;
import com.blackgear.platform.common.worldgen.modifier.BiomeManager;
import com.blackgear.platform.common.worldgen.modifier.BiomeWriter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Predicate;

@Mod.EventBusSubscriber(
    modid = Platform.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class BiomeManagerImpl {
    public static void bootstrap() {}
    
    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        BiomeManager.INSTANCE.register(new ForgeBiomeWriter(event));
    }
    
    static class ForgeBiomeWriter extends BiomeWriter {
        private final BiomeLoadingEvent event;

        ForgeBiomeWriter(BiomeLoadingEvent event) {
            this.event = event;
        }

        @Override
        public ResourceLocation name() {
            return ForgeBiomeWriter.this.event.getName();
        }

        @Override
        public BiomeContext context() {
            return new BiomeContext() {
                @Override
                public ResourceKey<Biome> key() {
                    return ResourceKey.create(Registry.BIOME_REGISTRY, ForgeBiomeWriter.this.name());
                }
                
                @Override
                public Biome biome() {
                    return ForgeRegistries.BIOMES.getValue(ForgeBiomeWriter.this.event.getName());
                }
                
                @Override
                public boolean is(Biome.BiomeCategory category) {
                    return ForgeBiomeWriter.this.event.getCategory() == category;
                }
                
                @Override
                public boolean is(ResourceKey<Biome> biome) {
                    return this.key() == biome;
                }
                
                @Override
                public boolean is(Predicate<BiomeContext> context) {
                    return context.test(this);
                }
            };
        }

        @Override
        public void addFeature(GenerationStep.Decoration decoration, ConfiguredFeature<?, ?> feature) {
            this.event.getGeneration().addFeature(decoration, feature);
        }

        @Override
        public void addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data) {
            this.event.getSpawns().addSpawn(category, data);
        }

        @Override
        public void addCarver(GenerationStep.Carving carving, ConfiguredWorldCarver<?> carver) {
            this.event.getGeneration().addCarver(carving, carver);
        }
    }
}