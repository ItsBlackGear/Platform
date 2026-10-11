package com.blackgear.platform.common.v2.recipe;

import com.google.common.collect.ImmutableMap;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class DynamicRecipeProvider {
    protected static final Map<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>> SHAPE_BUILDERS = ImmutableMap.<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>>builder()
        .put(BlockFamily.Variant.BUTTON, (result, base) -> buttonBuilder(result, Ingredient.of(base)))
        .put(BlockFamily.Variant.CHISELED, (result, base) -> chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, result, Ingredient.of(base)))
        .put(BlockFamily.Variant.CUT, (result, base) -> cutBuilder(RecipeCategory.BUILDING_BLOCKS, result, Ingredient.of(base)))
        .put(BlockFamily.Variant.DOOR, (result, base) -> doorBuilder(result, Ingredient.of(base)))
        .put(BlockFamily.Variant.CUSTOM_FENCE, (result, base) -> fenceBuilder(result, Ingredient.of(base)))
        .put(BlockFamily.Variant.FENCE, (result, base) -> fenceBuilder(result, Ingredient.of(base)))
        .put(BlockFamily.Variant.CUSTOM_FENCE_GATE, (result, base) -> fenceGateBuilder(result, Ingredient.of(base)))
        .put(BlockFamily.Variant.FENCE_GATE, (result, base) -> fenceGateBuilder(result, Ingredient.of(base)))
        .put(BlockFamily.Variant.SIGN, (result, base) -> signBuilder(result, Ingredient.of(base)))
        .put(BlockFamily.Variant.SLAB, (result, base) -> slabBuilder(RecipeCategory.BUILDING_BLOCKS, result, Ingredient.of(base)))
        .put(BlockFamily.Variant.STAIRS, (result, base) -> stairBuilder(result, Ingredient.of(base)))
        .put(BlockFamily.Variant.PRESSURE_PLATE, (result, base) -> pressurePlateBuilder(RecipeCategory.REDSTONE, result, Ingredient.of(base)))
        .put(BlockFamily.Variant.POLISHED, (result, base) -> polishedBuilder(RecipeCategory.BUILDING_BLOCKS, result, Ingredient.of(base)))
        .put(BlockFamily.Variant.TRAPDOOR, (result, base) -> trapdoorBuilder(result, Ingredient.of(base)))
        .put(BlockFamily.Variant.WALL, (result, base) -> wallBuilder(RecipeCategory.DECORATIONS, result, Ingredient.of(base)))
        .build();
    
    public abstract void buildRecipes(RecipeOutput output, HolderLookup.Provider registries, Function<String, ResourceLocation> path);
    
    protected static void generateForEnabledBlockFamilies(RecipeOutput output, FeatureFlagSet enabledFeatures) {
        BlockFamilies.getAllFamilies()
            .filter(BlockFamily::shouldGenerateRecipe)
            .forEach(family -> generateRecipes(output, family, enabledFeatures));
    }
    
    protected static void oneToOneConversionRecipe(RecipeOutput output, ItemLike result, ItemLike ingredient, @Nullable String group) {
        oneToOneConversionRecipe(output, result, ingredient, group, 1);
    }
    
    protected static void oneToOneConversionRecipe(RecipeOutput output, ItemLike result, ItemLike ingredient, @Nullable String group, int resultCount) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result, resultCount).requires(ingredient).group(group).unlockedBy(getHasName(ingredient), has(ingredient)).save(output, getConversionRecipeName(result, ingredient));
    }
    
    protected static void oreSmelting(RecipeOutput output, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group) {
        oreCooking(output, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, ingredients, category, result, experience, cookingTime, group, "_from_smelting");
    }
    
    protected static void oreBlasting(RecipeOutput output, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group) {
        oreCooking(output, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, ingredients, category, result, experience, cookingTime, group, "_from_blasting");
    }
    
    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput output, RecipeSerializer<T> serializer, AbstractCookingRecipe.Factory<T> recipeFactory, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, String recipeName) {
        for(ItemLike entry : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(entry), category, result, experience, cookingTime, serializer, recipeFactory)
                .group(group)
                .unlockedBy(getHasName(entry), has(entry))
                .save(output, getItemName(result) + recipeName + "_" + getItemName(entry));
        }
    }
    
    protected static void netheriteSmithing(RecipeOutput output, Item ingredientItem, RecipeCategory category, Item resultItem) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ingredientItem), Ingredient.of(Items.NETHERITE_INGOT), category, resultItem)
            .unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT))
            .save(output, getItemName(resultItem) + "_smithing");
    }
    
    protected static void trimSmithing(RecipeOutput output, Item ingredientItem, ResourceLocation location) {
        SmithingTrimRecipeBuilder.smithingTrim(Ingredient.of(ingredientItem), Ingredient.of(ItemTags.TRIMMABLE_ARMOR), Ingredient.of(ItemTags.TRIM_MATERIALS), RecipeCategory.MISC)
            .unlocks("has_smithing_trim_template", has(ingredientItem))
            .save(output, location);
    }
    
    protected static void twoByTwoPacker(RecipeOutput output, RecipeCategory category, ItemLike packed, ItemLike unpacked) {
        ShapedRecipeBuilder.shaped(category, packed, 1)
            .define('#', unpacked)
            .pattern("##")
            .pattern("##")
            .unlockedBy(getHasName(unpacked), has(unpacked))
            .save(output);
    }
    
    protected static void threeByThreePacker(RecipeOutput output, RecipeCategory category, ItemLike packed, ItemLike unpacked, String criterionName) {
        ShapelessRecipeBuilder.shapeless(category, packed)
            .requires(unpacked, 9)
            .unlockedBy(criterionName, has(unpacked))
            .save(output);
    }
    
    protected static void threeByThreePacker(RecipeOutput output, RecipeCategory category, ItemLike packed, ItemLike unpacked) {
        threeByThreePacker(output, category, packed, unpacked, getHasName(unpacked));
    }
    
    protected static void planksFromLog(RecipeOutput output, ItemLike planks, TagKey<Item> logs, int resultCount) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, planks, resultCount)
            .requires(logs)
            .group("planks")
            .unlockedBy("has_log", has(logs))
            .save(output);
    }
    
    protected static void planksFromLogs(RecipeOutput output, ItemLike planks, TagKey<Item> logs, int resultCount) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, planks, resultCount)
            .requires(logs)
            .group("planks")
            .unlockedBy("has_logs", has(logs))
            .save(output);
    }
    
    protected static void woodFromLogs(RecipeOutput output, ItemLike wood, ItemLike log) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, wood, 3)
            .define('#', log)
            .pattern("##")
            .pattern("##")
            .group("bark")
            .unlockedBy("has_log", has(log))
            .save(output);
    }
    
    protected static void woodenBoat(RecipeOutput output, ItemLike boat, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, boat)
            .define('#', material)
            .pattern("# #")
            .pattern("###")
            .group("boat")
            .unlockedBy("in_water", insideOf(Blocks.WATER))
            .save(output);
    }
    
    protected static void chestBoat(RecipeOutput output, ItemLike boat, ItemLike material) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, boat)
            .requires(Blocks.CHEST)
            .requires(material)
            .group("chest_boat")
            .unlockedBy("has_boat", has(ItemTags.BOATS))
            .save(output);
    }
    
    protected static RecipeBuilder buttonBuilder(ItemLike button, Ingredient material) {
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, button)
            .requires(material);
    }
    
    protected static RecipeBuilder doorBuilder(ItemLike door, Ingredient material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, door, 3)
            .define('#', material)
            .pattern("##")
            .pattern("##")
            .pattern("##");
    }
    
    protected static RecipeBuilder fenceBuilder(ItemLike fence, Ingredient material) {
        int amount = fence == Blocks.NETHER_BRICK_FENCE ? 6 : 3;
        Item item = fence == Blocks.NETHER_BRICK_FENCE ? Items.NETHER_BRICK : Items.STICK;
        return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, fence, amount)
            .define('W', material)
            .define('#', item)
            .pattern("W#W")
            .pattern("W#W");
    }
    
    protected static RecipeBuilder fenceGateBuilder(ItemLike fenceGate, Ingredient material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, fenceGate)
            .define('#', Items.STICK)
            .define('W', material)
            .pattern("#W#")
            .pattern("#W#");
    }
    
    protected static void pressurePlate(RecipeOutput output, ItemLike pressurePlate, ItemLike material) {
        pressurePlateBuilder(RecipeCategory.REDSTONE, pressurePlate, Ingredient.of(material))
            .unlockedBy(getHasName(material), has(material))
            .save(output);
    }
    
    protected static RecipeBuilder pressurePlateBuilder(RecipeCategory category, ItemLike pressurePlate, Ingredient material) {
        return ShapedRecipeBuilder.shaped(category, pressurePlate)
            .define('#', material)
            .pattern("##");
    }
    
    protected static void slab(RecipeOutput output, RecipeCategory category, ItemLike pressurePlate, ItemLike material) {
        slabBuilder(category, pressurePlate, Ingredient.of(material))
            .unlockedBy(getHasName(material), has(material))
            .save(output);
    }
    
    protected static RecipeBuilder slabBuilder(RecipeCategory category, ItemLike slab, Ingredient material) {
        return ShapedRecipeBuilder.shaped(category, slab, 6).define('#', material).pattern("###");
    }
    
    protected static RecipeBuilder stairBuilder(ItemLike stairs, Ingredient material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, stairs, 4).define('#', material).pattern("#  ").pattern("## ").pattern("###");
    }
    
    protected static RecipeBuilder trapdoorBuilder(ItemLike trapdoor, Ingredient material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, trapdoor, 2).define('#', material).pattern("###").pattern("###");
    }
    
    protected static RecipeBuilder signBuilder(ItemLike sign, Ingredient material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, sign, 3)
            .group("sign")
            .define('#', material)
            .define('X', Items.STICK)
            .pattern("###")
            .pattern("###")
            .pattern(" X ");
    }
    
    protected static void hangingSign(RecipeOutput output, ItemLike sign, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, sign, 6)
            .group("hanging_sign")
            .define('#', material)
            .define('X', Items.CHAIN)
            .pattern("X X")
            .pattern("###")
            .pattern("###")
            .unlockedBy("has_stripped_logs", has(material))
            .save(output);
    }
    
    protected static void colorBlockWithDye(RecipeOutput output, List<Item> dyes, List<Item> dyeableItems, String group) {
        for (int i = 0; i < dyes.size(); i++) {
            Item item = dyes.get(i);
            Item item2 = dyeableItems.get(i);
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, item2)
                .requires(item)
                .requires(Ingredient.of(dyeableItems.stream().filter(item2x -> !item2x.equals(item2)).map(ItemStack::new)))
                .group(group)
                .unlockedBy("has_needed_dye", has(item))
                .save(output, "dye_" + getItemName(item2));
        }
    }
    
    protected static void carpet(RecipeOutput output, ItemLike carpet, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, carpet, 3)
            .define('#', material)
            .pattern("##")
            .group("carpet")
            .unlockedBy(getHasName(material), has(material))
            .save(output);
    }
    
    protected static void bedFromPlanksAndWool(RecipeOutput output, ItemLike bed, ItemLike wool) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, bed)
            .define('#', wool)
            .define('X', ItemTags.PLANKS)
            .pattern("###")
            .pattern("XXX")
            .group("bed")
            .unlockedBy(getHasName(wool), has(wool))
            .save(output);
    }
    
    protected static void banner(RecipeOutput output, ItemLike banner, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, banner)
            .define('#', material)
            .define('|', Items.STICK)
            .pattern("###")
            .pattern("###")
            .pattern(" | ")
            .group("banner")
            .unlockedBy(getHasName(material), has(material))
            .save(output);
    }
    
    protected static void stainedGlassFromGlassAndDye(RecipeOutput output, ItemLike stainedGlass, ItemLike dye) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, stainedGlass, 8)
            .define('#', Blocks.GLASS)
            .define('X', dye)
            .pattern("###")
            .pattern("#X#")
            .pattern("###")
            .group("stained_glass")
            .unlockedBy("has_glass", has(Blocks.GLASS))
            .save(output);
    }
    
    protected static void stainedGlassPaneFromStainedGlass(RecipeOutput output, ItemLike stainedGlassPane, ItemLike stainedGlass) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, stainedGlassPane, 16)
            .define('#', stainedGlass)
            .pattern("###")
            .pattern("###")
            .group("stained_glass_pane")
            .unlockedBy("has_glass", has(stainedGlass))
            .save(output);
    }
    
    protected static void stainedGlassPaneFromGlassPaneAndDye(RecipeOutput output, ItemLike stainedGlassPane, ItemLike dye) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, stainedGlassPane, 8)
            .define('#', Blocks.GLASS_PANE)
            .define('$', dye)
            .pattern("###")
            .pattern("#$#")
            .pattern("###")
            .group("stained_glass_pane")
            .unlockedBy("has_glass_pane", has(Blocks.GLASS_PANE))
            .unlockedBy(getHasName(dye), has(dye))
            .save(output, getConversionRecipeName(stainedGlassPane, Blocks.GLASS_PANE));
    }
    
    protected static void coloredTerracottaFromTerracottaAndDye(RecipeOutput output, ItemLike coloredTerracotta, ItemLike dye) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, coloredTerracotta, 8)
            .define('#', Blocks.TERRACOTTA)
            .define('X', dye)
            .pattern("###")
            .pattern("#X#")
            .pattern("###")
            .group("stained_terracotta")
            .unlockedBy("has_terracotta", has(Blocks.TERRACOTTA))
            .save(output);
    }
    
    protected static void concretePowder(RecipeOutput output, ItemLike dyedConcretePowder, ItemLike dye) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, dyedConcretePowder, 8)
            .requires(dye)
            .requires(Blocks.SAND, 4)
            .requires(Blocks.GRAVEL, 4)
            .group("concrete_powder")
            .unlockedBy("has_sand", has(Blocks.SAND))
            .unlockedBy("has_gravel", has(Blocks.GRAVEL))
            .save(output);
    }
    
    protected static void candle(RecipeOutput output, ItemLike candle, ItemLike dye) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, candle)
            .requires(Blocks.CANDLE)
            .requires(dye)
            .group("dyed_candle")
            .unlockedBy(getHasName(dye), has(dye))
            .save(output);
    }
    
    protected static void wall(RecipeOutput output, RecipeCategory category, ItemLike wall, ItemLike material) {
        wallBuilder(category, wall, Ingredient.of(material)).unlockedBy(getHasName(material), has(material)).save(output);
    }
    
    protected static RecipeBuilder wallBuilder(RecipeCategory category, ItemLike wall, Ingredient material) {
        return ShapedRecipeBuilder.shaped(category, wall, 6).define('#', material).pattern("###").pattern("###");
    }
    
    protected static void polished(RecipeOutput output, RecipeCategory category, ItemLike result, ItemLike material) {
        polishedBuilder(category, result, Ingredient.of(material)).unlockedBy(getHasName(material), has(material)).save(output);
    }
    
    protected static RecipeBuilder polishedBuilder(RecipeCategory category, ItemLike result, Ingredient material) {
        return ShapedRecipeBuilder.shaped(category, result, 4).define('S', material).pattern("SS").pattern("SS");
    }
    
    protected static void cut(RecipeOutput output, RecipeCategory category, ItemLike cutResult, ItemLike material) {
        cutBuilder(category, cutResult, Ingredient.of(material)).unlockedBy(getHasName(material), has(material)).save(output);
    }
    
    protected static ShapedRecipeBuilder cutBuilder(RecipeCategory category, ItemLike cutResult, Ingredient material) {
        return ShapedRecipeBuilder.shaped(category, cutResult, 4).define('#', material).pattern("##").pattern("##");
    }
    
    protected static void chiseled(RecipeOutput output, RecipeCategory category, ItemLike chiseledResult, ItemLike material) {
        chiseledBuilder(category, chiseledResult, Ingredient.of(material)).unlockedBy(getHasName(material), has(material)).save(output);
    }
    
    protected static void mosaicBuilder(RecipeOutput output, RecipeCategory category, ItemLike result, ItemLike material) {
        ShapedRecipeBuilder.shaped(category, result)
            .define('#', material)
            .pattern("#")
            .pattern("#")
            .unlockedBy(getHasName(material), has(material))
            .save(output);
    }
    
    protected static ShapedRecipeBuilder chiseledBuilder(RecipeCategory category, ItemLike chiseledResult, Ingredient material) {
        return ShapedRecipeBuilder.shaped(category, chiseledResult).define('#', material).pattern("#").pattern("#");
    }
    
    protected static void stonecutterResultFromBase(RecipeOutput output, RecipeCategory category, ItemLike result, ItemLike material) {
        stonecutterResultFromBase(output, category, result, material, 1);
    }
    
    protected static void stonecutterResultFromBase(RecipeOutput output, RecipeCategory category, ItemLike result, ItemLike material, int resultCount) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(material), category, result, resultCount)
            .unlockedBy(getHasName(material), has(material))
            .save(output, getConversionRecipeName(result, material) + "_stonecutting");
    }
    protected static void smeltingResultFromBase(RecipeOutput output, ItemLike result, ItemLike ingredient) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredient), RecipeCategory.BUILDING_BLOCKS, result, 0.1F, 200)
            .unlockedBy(getHasName(ingredient), has(ingredient))
            .save(output);
    }
    
    protected static void nineBlockStorageRecipes(RecipeOutput output, RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed) {
        nineBlockStorageRecipes(output, unpackedCategory, unpacked, packedCategory, packed, getSimpleRecipeName(packed), null, getSimpleRecipeName(unpacked), null);
    }
    
    protected static void nineBlockStorageRecipesWithCustomPacking(RecipeOutput output, RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed, String packedName, String packedGroup) {
        nineBlockStorageRecipes(output, unpackedCategory, unpacked, packedCategory, packed, packedName, packedGroup, getSimpleRecipeName(unpacked), null);
    }
    
    protected static void nineBlockStorageRecipesRecipesWithCustomUnpacking(RecipeOutput output, RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed, String unpackedName, String unpackedGroup) {
        nineBlockStorageRecipes(output, unpackedCategory, unpacked, packedCategory, packed, getSimpleRecipeName(packed), null, unpackedName, unpackedGroup);
    }
    
    protected static void nineBlockStorageRecipes(RecipeOutput output, RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed, String packedName, @Nullable String packedGroup, String unpackedName, @Nullable String unpackedGroup) {
        ShapelessRecipeBuilder.shapeless(unpackedCategory, unpacked, 9)
            .requires(packed)
            .group(unpackedGroup)
            .unlockedBy(getHasName(packed), has(packed))
            .save(output, ResourceLocation.parse(unpackedName));
        ShapedRecipeBuilder.shaped(packedCategory, packed)
            .define('#', unpacked)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .group(packedGroup)
            .unlockedBy(getHasName(unpacked), has(unpacked))
            .save(output, ResourceLocation.parse(packedName));
    }
    
    protected static void copySmithingTemplate(RecipeOutput output, ItemLike result, TagKey<Item> baseMaterial) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result, 2)
            .define('#', Items.DIAMOND)
            .define('C', baseMaterial)
            .define('S', result)
            .pattern("#S#")
            .pattern("#C#")
            .pattern("###")
            .unlockedBy(getHasName(result), has(result))
            .save(output);
    }
    
    protected static void copySmithingTemplate(RecipeOutput output, ItemLike result, ItemLike baseItem) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result, 2)
            .define('#', Items.DIAMOND)
            .define('C', baseItem)
            .define('S', result)
            .pattern("#S#")
            .pattern("#C#")
            .pattern("###")
            .unlockedBy(getHasName(result), has(result))
            .save(output);
    }
    
    protected static void copySmithingTemplate(RecipeOutput output, ItemLike result, Ingredient baseItem) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result, 2)
            .define('#', Items.DIAMOND)
            .define('C', baseItem)
            .define('S', result)
            .pattern("#S#")
            .pattern("#C#")
            .pattern("###")
            .unlockedBy(getHasName(result), has(result))
            .save(output);
    }
    
    protected static <T extends AbstractCookingRecipe> void cookRecipes(RecipeOutput output, String cookingMethod, RecipeSerializer<T> cookingSerializer, AbstractCookingRecipe.Factory<T> recipeFactory, int cookingTime) {
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.BEEF, Items.COOKED_BEEF, 0.35F);
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35F);
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.COD, Items.COOKED_COD, 0.35F);
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.KELP, Items.DRIED_KELP, 0.1F);
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.SALMON, Items.COOKED_SALMON, 0.35F);
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.MUTTON, Items.COOKED_MUTTON, 0.35F);
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35F);
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.POTATO, Items.BAKED_POTATO, 0.35F);
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, recipeFactory, cookingTime, Items.RABBIT, Items.COOKED_RABBIT, 0.35F);
    }
    
    protected static <T extends AbstractCookingRecipe> void simpleCookingRecipe(RecipeOutput output, String cookingMethod, RecipeSerializer<T> cookingSerializer, AbstractCookingRecipe.Factory<T> recipeFactory, int cookingTime, ItemLike ingredient, ItemLike result, float experience) {
        SimpleCookingRecipeBuilder.generic(Ingredient.of(ingredient), RecipeCategory.FOOD, result, experience, cookingTime, cookingSerializer, recipeFactory)
            .unlockedBy(getHasName(ingredient), has(ingredient))
            .save(output, getItemName(result) + "_from_" + cookingMethod);
    }
    
    protected static void waxRecipes(RecipeOutput output) {
        HoneycombItem.WAXABLES.get().forEach((base, waxed) -> ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, waxed)
            .requires(base)
            .requires(Items.HONEYCOMB)
            .group(getItemName(waxed))
            .unlockedBy(getHasName(base), has(base))
            .save(output, getConversionRecipeName(waxed, Items.HONEYCOMB))
        );
    }
    
    protected static void generateRecipes(RecipeOutput output, BlockFamily family, FeatureFlagSet requiredFeatures) {
        family.getVariants().forEach((variant, block) -> {
            if (block.requiredFeatures().isSubsetOf(requiredFeatures)) {
                BiFunction<ItemLike, ItemLike, RecipeBuilder> builders = SHAPE_BUILDERS.get(variant);
                ItemLike entry = getBaseBlock(family, variant);
                if (builders != null) {
                    RecipeBuilder builder = builders.apply(block, entry);
                    family.getRecipeGroupPrefix().ifPresent(string -> builder.group(string + (variant == BlockFamily.Variant.CUT ? "" : "_" + variant.getRecipeGroup())));
                    builder.unlockedBy(family.getRecipeUnlockedBy().orElseGet(() -> getHasName(entry)), has(entry));
                    builder.save(output);
                }
                
                if (variant == BlockFamily.Variant.CRACKED) {
                    smeltingResultFromBase(output, block, entry);
                }
            }
        });
    }
    
    protected static Block getBaseBlock(BlockFamily family, BlockFamily.Variant variant) {
        if (variant == BlockFamily.Variant.CHISELED) {
            if (!family.getVariants().containsKey(BlockFamily.Variant.SLAB)) {
                throw new IllegalStateException("Slab is not defined for the family.");
            } else {
                return family.get(BlockFamily.Variant.SLAB);
            }
        } else {
            return family.getBaseBlock();
        }
    }
    
    protected static Criterion<EnterBlockTrigger.TriggerInstance> insideOf(Block block) {
        return CriteriaTriggers.ENTER_BLOCK.createCriterion(new EnterBlockTrigger.TriggerInstance(Optional.empty(), Optional.of(block.builtInRegistryHolder()), Optional.empty()));
    }
    
    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(MinMaxBounds.Ints count, ItemLike item) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(item).withCount(count));
    }
    
    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike itemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(itemLike));
    }
    
    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(tag));
    }
    
    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate.Builder... items) {
        return inventoryTrigger(Arrays.stream(items).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
    }
    
    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate... predicates) {
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(predicates)));
    }
    
    protected static String getHasName(ItemLike itemLike) {
        return "has_" + getItemName(itemLike);
    }
    
    protected static String getItemName(ItemLike itemLike) {
        return BuiltInRegistries.ITEM.getKey(itemLike.asItem()).getPath();
    }
    
    protected static String getSimpleRecipeName(ItemLike itemLike) {
        return getItemName(itemLike);
    }
    
    protected static String getConversionRecipeName(ItemLike result, ItemLike ingredient) {
        return getItemName(result) + "_from_" + getItemName(ingredient);
    }
    
    protected static String getSmeltingRecipeName(ItemLike itemLike) {
        return getItemName(itemLike) + "_from_smelting";
    }
    
    protected static String getBlastingRecipeName(ItemLike itemLike) {
        return getItemName(itemLike) + "_from_blasting";
    }
}