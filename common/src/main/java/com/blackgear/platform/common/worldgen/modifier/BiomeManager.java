package com.blackgear.platform.common.worldgen.modifier;

import com.google.common.collect.Lists;
import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Utility class to register all biome modifications.
 *
 * @author ItsBlackGear
 **/
public class BiomeManager {
    private static final List<BiConsumer<BiomeWriter, BiomeContext>> MODIFICATIONS = Lists.newArrayList();

    public static final BiomeManager INSTANCE = new BiomeManager();

    /**
     * Registers the biome manager, however this should not be needed outside this mod.
     */
    @ExpectPlatform
    public static void bootstrap() {
        throw new AssertionError();
    }

    /**
     * Registers a biome writer to the biome manager.
     * This should not be needed outside this mod as it is only required to register a biome writer per mod loader.
     */
    public void register(BiomeWriter writer) {
        MODIFICATIONS.forEach(writer::add);
    }

    /**
     * Registers a biome modifier to the biome manager.
     * This can be used to add custom biome modifications by using the writer and the context of a biome.
     *
     * <p>- The writer can be used to add features, spawns and carvers to a biome.
     * <p>- The context can be used to check if a biome is a certain biome or has a certain tag.
     *
     * <p>Example of biome modification registry:</p>
     *
     * <pre>{@code
     *
     * BiomeManager.add((writer, context) -> {
     *      // ========== Biome Tags ==========
     *      if (context.is(BiomeTags.IS_OVERWORLD)) {
     *          writer.addFeature(
     *              GenerationStep.Decoration.RAW_GENERATION,
     *              SurfacePlacements.BOULDER
     *          );
     *      }
     *
     *      // ========== Custom Biome ==========
     *      if (context.is(CUSTOM_BIOME)) {
     *          writer.addSpawn(
     *              MobCategory.CREATURE,
     *              new MobSpawnSettings.SpawnerData(
     *                  ModEntities.DUCK.get(), 4, 2, 4
     *              )
     *          );
     *      }
     * });
     *
     * }</pre>
     *
     * @param modifier biome modifications to register
     */
    public static void add(BiConsumer<BiomeWriter, BiomeContext> modifier) {
        MODIFICATIONS.add(modifier);
    }
}