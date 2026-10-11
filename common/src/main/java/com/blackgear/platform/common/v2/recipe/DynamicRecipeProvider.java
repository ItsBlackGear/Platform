package com.blackgear.platform.common.v2.recipe;

import com.google.common.collect.ImmutableMap;
import net.minecraft.advancements.critereon.*;
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
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
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
    
    public abstract void buildRecipes(Consumer<FinishedRecipe> output, Function<String, ResourceLocation> path);
    
    protected static void generateForEnabledBlockFamilies(Consumer<FinishedRecipe> output, FeatureFlagSet enabledFeatures) {
        BlockFamilies.getAllFamilies()
            .filter(family -> family.shouldGenerateRecipe(enabledFeatures))
            .forEach(family -> generateRecipes(output, family));
    }
    
    protected static void oneToOneConversionRecipe(Consumer<FinishedRecipe> output, ItemLike result, ItemLike ingredient, @Nullable String group) {
        oneToOneConversionRecipe(output, result, ingredient, group, 1);
    }
    
    protected static void oneToOneConversionRecipe(Consumer<FinishedRecipe> output, ItemLike result, ItemLike ingredient, @Nullable String group, int resultCount) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result, resultCount)
            .requires(ingredient)
            .group(group)
            .unlockedBy(getHasName(ingredient), has(ingredient))
            .save(output, getConversionRecipeName(result, ingredient));
    }
    
    protected static void oreSmelting(Consumer<FinishedRecipe> output, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTIme, String group) {
        oreCooking(output, RecipeSerializer.SMELTING_RECIPE, ingredients, category, result, experience, cookingTIme, group, "_from_smelting");
    }
    
    protected static void oreBlasting(Consumer<FinishedRecipe> output, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group) {
        oreCooking(output, RecipeSerializer.BLASTING_RECIPE, ingredients, category, result, experience, cookingTime, group, "_from_blasting");
    }
    
    protected static void oreCooking(Consumer<FinishedRecipe> output, RecipeSerializer<? extends AbstractCookingRecipe> cookingSerializer, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, String recipeName) {
        for (ItemLike entry : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(entry), category, result, experience, cookingTime, cookingSerializer)
                .group(group)
                .unlockedBy(getHasName(entry), has(entry))
                .save(output, getItemName(result) + recipeName + "_" + getItemName(entry));
        }
    }
    
    protected static void netheriteSmithing(Consumer<FinishedRecipe> output, Item ingredientItem, RecipeCategory category, Item resultItem) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ingredientItem), Ingredient.of(Items.NETHERITE_INGOT), category, resultItem)
            .unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT))
            .save(output, getItemName(resultItem) + "_smithing");
    }
    
    protected static void trimSmithing(Consumer<FinishedRecipe> output, Item ingredientItem, ResourceLocation location) {
        SmithingTrimRecipeBuilder.smithingTrim(Ingredient.of(ingredientItem), Ingredient.of(ItemTags.TRIMMABLE_ARMOR), Ingredient.of(ItemTags.TRIM_MATERIALS), RecipeCategory.MISC)
            .unlocks("has_smithing_trim_template", has(ingredientItem))
            .save(output, location);
    }
    
    protected static void twoByTwoPacker(Consumer<FinishedRecipe> output, RecipeCategory category, ItemLike packed, ItemLike unpacked) {
        ShapedRecipeBuilder.shaped(category, packed, 1)
            .define('#', unpacked)
            .pattern("##")
            .pattern("##")
            .unlockedBy(getHasName(unpacked), has(unpacked))
            .save(output);
    }
    
    protected static void threeByThreePacker(Consumer<FinishedRecipe> output, RecipeCategory category, ItemLike packed, ItemLike unpacked, String criterionName) {
        ShapelessRecipeBuilder.shapeless(category, packed)
            .requires(unpacked, 9)
            .unlockedBy(criterionName, has(unpacked))
            .save(output);
    }
    
    protected static void threeByThreePacker(Consumer<FinishedRecipe> output, RecipeCategory category, ItemLike packed, ItemLike unpacked) {
        threeByThreePacker(output, category, packed, unpacked, getHasName(unpacked));
    }
    
    protected static void planksFromLog(Consumer<FinishedRecipe> output, ItemLike planks, TagKey<Item> logs, int resultCount) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, planks, resultCount)
            .requires(logs)
            .group("planks")
            .unlockedBy("has_log", has(logs))
            .save(output);
    }
    
    protected static void planksFromLogs(Consumer<FinishedRecipe> output, ItemLike planks, TagKey<Item> logs, int resultCount) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, planks, resultCount)
            .requires(logs)
            .group("planks")
            .unlockedBy("has_logs", has(logs))
            .save(output);
    }
    
    protected static void woodFromLogs(Consumer<FinishedRecipe> output, ItemLike wood, ItemLike log) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, wood, 3)
            .define('#', log)
            .pattern("##")
            .pattern("##")
            .group("bark")
            .unlockedBy("has_log", has(log))
            .save(output);
    }
    
    protected static void woodenBoat(Consumer<FinishedRecipe> output, ItemLike boat, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, boat)
            .define('#', material)
            .pattern("# #")
            .pattern("###")
            .group("boat")
            .unlockedBy("in_water", insideOf(Blocks.WATER))
            .save(output);
    }
    
    protected static void chestBoat(Consumer<FinishedRecipe> output, ItemLike boat, ItemLike material) {
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
    
    protected static void pressurePlate(Consumer<FinishedRecipe> output, ItemLike pressurePlate, ItemLike material) {
        pressurePlateBuilder(RecipeCategory.REDSTONE, pressurePlate, Ingredient.of(material))
            .unlockedBy(getHasName(material), has(material))
            .save(output);
    }
    
    protected static RecipeBuilder pressurePlateBuilder(RecipeCategory category, ItemLike pressurePlate, Ingredient material) {
        return ShapedRecipeBuilder.shaped(category, pressurePlate)
            .define('#', material)
            .pattern("##");
    }
    
    protected static void slab(Consumer<FinishedRecipe> output, RecipeCategory category, ItemLike pressurePlate, ItemLike material) {
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
    
    protected static void hangingSign(Consumer<FinishedRecipe> output, ItemLike sign, ItemLike material) {
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
    
    protected static void colorBlockWithDye(Consumer<FinishedRecipe> output, List<Item> dyes, List<Item> dyeableItems, String group) {
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
    
    protected static void carpet(Consumer<FinishedRecipe> output, ItemLike carpet, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, carpet, 3)
            .define('#', material)
            .pattern("##")
            .group("carpet")
            .unlockedBy(getHasName(material), has(material))
            .save(output);
    }
    
    protected static void bedFromPlanksAndWool(Consumer<FinishedRecipe> output, ItemLike bed, ItemLike wool) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, bed)
            .define('#', wool)
            .define('X', ItemTags.PLANKS)
            .pattern("###")
            .pattern("XXX")
            .group("bed")
            .unlockedBy(getHasName(wool), has(wool))
            .save(output);
    }
    
    protected static void banner(Consumer<FinishedRecipe> output, ItemLike banner, ItemLike material) {
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
    
    protected static void stainedGlassFromGlassAndDye(Consumer<FinishedRecipe> output, ItemLike stainedGlass, ItemLike dye) {
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
    
    protected static void stainedGlassPaneFromStainedGlass(Consumer<FinishedRecipe> output, ItemLike stainedGlassPane, ItemLike stainedGlass) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, stainedGlassPane, 16)
            .define('#', stainedGlass)
            .pattern("###")
            .pattern("###")
            .group("stained_glass_pane")
            .unlockedBy("has_glass", has(stainedGlass))
            .save(output);
    }
    
    protected static void stainedGlassPaneFromGlassPaneAndDye(Consumer<FinishedRecipe> output, ItemLike stainedGlassPane, ItemLike dye) {
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
    
    protected static void coloredTerracottaFromTerracottaAndDye(Consumer<FinishedRecipe> output, ItemLike coloredTerracotta, ItemLike dye) {
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
    
    protected static void concretePowder(Consumer<FinishedRecipe> output, ItemLike dyedConcretePowder, ItemLike dye) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, dyedConcretePowder, 8)
            .requires(dye)
            .requires(Blocks.SAND, 4)
            .requires(Blocks.GRAVEL, 4)
            .group("concrete_powder")
            .unlockedBy("has_sand", has(Blocks.SAND))
            .unlockedBy("has_gravel", has(Blocks.GRAVEL))
            .save(output);
    }
    
    protected static void candle(Consumer<FinishedRecipe> output, ItemLike candle, ItemLike dye) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, candle)
            .requires(Blocks.CANDLE)
            .requires(dye)
            .group("dyed_candle")
            .unlockedBy(getHasName(dye), has(dye))
            .save(output);
    }
    
    protected static void wall(Consumer<FinishedRecipe> output, RecipeCategory category, ItemLike wall, ItemLike material) {
        wallBuilder(category, wall, Ingredient.of(material)).unlockedBy(getHasName(material), has(material)).save(output);
    }
    
    protected static RecipeBuilder wallBuilder(RecipeCategory category, ItemLike wall, Ingredient material) {
        return ShapedRecipeBuilder.shaped(category, wall, 6).define('#', material).pattern("###").pattern("###");
    }
    
    protected static void polished(Consumer<FinishedRecipe> output, RecipeCategory category, ItemLike result, ItemLike material) {
        polishedBuilder(category, result, Ingredient.of(material)).unlockedBy(getHasName(material), has(material)).save(output);
    }
    
    protected static RecipeBuilder polishedBuilder(RecipeCategory category, ItemLike result, Ingredient material) {
        return ShapedRecipeBuilder.shaped(category, result, 4).define('S', material).pattern("SS").pattern("SS");
    }
    
    protected static void cut(Consumer<FinishedRecipe> output, RecipeCategory category, ItemLike cutResult, ItemLike material) {
        cutBuilder(category, cutResult, Ingredient.of(material)).unlockedBy(getHasName(material), has(material)).save(output);
    }
    
    protected static ShapedRecipeBuilder cutBuilder(RecipeCategory category, ItemLike cutResult, Ingredient material) {
        return ShapedRecipeBuilder.shaped(category, cutResult, 4).define('#', material).pattern("##").pattern("##");
    }
    
    protected static void chiseled(Consumer<FinishedRecipe> output, RecipeCategory category, ItemLike chiseledResult, ItemLike material) {
        chiseledBuilder(category, chiseledResult, Ingredient.of(material)).unlockedBy(getHasName(material), has(material)).save(output);
    }
    
    protected static void mosaicBuilder(Consumer<FinishedRecipe> output, RecipeCategory category, ItemLike result, ItemLike material) {
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
    
    protected static void stonecutterResultFromBase(Consumer<FinishedRecipe> output, RecipeCategory category, ItemLike result, ItemLike material) {
        stonecutterResultFromBase(output, category, result, material, 1);
    }
    
    protected static void stonecutterResultFromBase(Consumer<FinishedRecipe> output, RecipeCategory category, ItemLike result, ItemLike material, int resultCount) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(material), category, result, resultCount)
            .unlockedBy(getHasName(material), has(material))
            .save(output, getConversionRecipeName(result, material) + "_stonecutting");
    }
    
    protected static void smeltingResultFromBase(Consumer<FinishedRecipe> output, ItemLike result, ItemLike ingredient) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredient), RecipeCategory.BUILDING_BLOCKS, result, 0.1F, 200)
            .unlockedBy(getHasName(ingredient), has(ingredient))
            .save(output);
    }
    
    protected static void nineBlockStorageRecipes(Consumer<FinishedRecipe> output, RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed) {
        nineBlockStorageRecipes(output, unpackedCategory, unpacked, packedCategory, packed, getSimpleRecipeName(packed), null, getSimpleRecipeName(unpacked), null);
    }
    
    protected static void nineBlockStorageRecipesWithCustomPacking(Consumer<FinishedRecipe> output, RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed, String packedName, String packedGroup) {
        nineBlockStorageRecipes(output, unpackedCategory, unpacked, packedCategory, packed, packedName, packedGroup, getSimpleRecipeName(unpacked), null);
    }
    
    protected static void nineBlockStorageRecipesRecipesWithCustomUnpacking(Consumer<FinishedRecipe> output, RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed, String unpackedName, String unpackedGroup) {
        nineBlockStorageRecipes(output, unpackedCategory, unpacked, packedCategory, packed, getSimpleRecipeName(packed), null, unpackedName, unpackedGroup);
    }
    
    protected static void nineBlockStorageRecipes(Consumer<FinishedRecipe> output, RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed, String packedName, @Nullable String packedGroup, String unpackedName, @Nullable String unpackedGroup) {
        ShapelessRecipeBuilder.shapeless(unpackedCategory, unpacked, 9)
            .requires(packed)
            .group(unpackedGroup)
            .unlockedBy(getHasName(packed), has(packed))
            .save(output, new ResourceLocation(unpackedName));
        ShapedRecipeBuilder.shaped(packedCategory, packed)
            .define('#', unpacked)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .group(packedGroup)
            .unlockedBy(getHasName(unpacked), has(unpacked))
            .save(output, new ResourceLocation(packedName));
    }
    
    protected static void copySmithingTemplate(Consumer<FinishedRecipe> output, ItemLike result, TagKey<Item> baseItem) {
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
    
    protected static void copySmithingTemplate(Consumer<FinishedRecipe> output, ItemLike result, ItemLike baseItem) {
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
    
    protected static void cookRecipes(Consumer<FinishedRecipe> output, String cookingMethod, RecipeSerializer<? extends AbstractCookingRecipe> cookingSerializer, int cookingTime) {
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, cookingTime, Items.BEEF, Items.COOKED_BEEF, 0.35F);
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, cookingTime, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35F);
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, cookingTime, Items.COD, Items.COOKED_COD, 0.35F);
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, cookingTime, Items.KELP, Items.DRIED_KELP, 0.1F);
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, cookingTime, Items.SALMON, Items.COOKED_SALMON, 0.35F);
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, cookingTime, Items.MUTTON, Items.COOKED_MUTTON, 0.35F);
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, cookingTime, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35F);
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, cookingTime, Items.POTATO, Items.BAKED_POTATO, 0.35F);
        simpleCookingRecipe(output, cookingMethod, cookingSerializer, cookingTime, Items.RABBIT, Items.COOKED_RABBIT, 0.35F);
    }
    
    protected static void simpleCookingRecipe(Consumer<FinishedRecipe> output, String cookingMethod, RecipeSerializer<? extends AbstractCookingRecipe> cookingSerializer, int cookingTime, ItemLike ingredient, ItemLike result, float experience) {
        SimpleCookingRecipeBuilder.generic(Ingredient.of(ingredient), RecipeCategory.FOOD, result, experience, cookingTime, cookingSerializer)
            .unlockedBy(getHasName(ingredient), has(ingredient))
            .save(output, getItemName(result) + "_from_" + cookingMethod);
    }
    
    protected static void waxRecipes(Consumer<FinishedRecipe> output) {
        HoneycombItem.WAXABLES.get().forEach((base, waxed) -> ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, waxed)
            .requires(base)
            .requires(Items.HONEYCOMB)
            .group(getItemName(waxed))
            .unlockedBy(getHasName(base), has(base))
            .save(output, getConversionRecipeName(waxed, Items.HONEYCOMB))
        );
    }
    
    protected static void generateRecipes(Consumer<FinishedRecipe> output, BlockFamily family) {
        family.getVariants().forEach((variant, block) -> {
            BiFunction<ItemLike, ItemLike, RecipeBuilder> builders = SHAPE_BUILDERS.get(variant);
            ItemLike entry = getBaseBlock(family, variant);
            if (builders != null) {
                RecipeBuilder builder = builders.apply(block, entry);
                family.getRecipeGroupPrefix().ifPresent(string -> builder.group(string + (variant == BlockFamily.Variant.CUT ? "" : "_" + variant.getName())));
                builder.unlockedBy(family.getRecipeUnlockedBy().orElseGet(() -> getHasName(entry)), has(entry));
                builder.save(output);
            }
            
            if (variant == BlockFamily.Variant.CRACKED) {
                smeltingResultFromBase(output, block, entry);
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
    
    protected static EnterBlockTrigger.TriggerInstance insideOf(Block block) {
        return new EnterBlockTrigger.TriggerInstance(ContextAwarePredicate.ANY, block, StatePropertiesPredicate.ANY);
    }
    
    protected static InventoryChangeTrigger.TriggerInstance has(MinMaxBounds.Ints count, ItemLike item) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(item).withCount(count).build());
    }
    
    protected static InventoryChangeTrigger.TriggerInstance has(ItemLike itemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(itemLike).build());
    }
    
    protected static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> tag) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(tag).build());
    }
    
    protected static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... predicates) {
        return new InventoryChangeTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, predicates);
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