package micdoodle8.mods.galacticraft.core.util;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe;
import micdoodle8.mods.galacticraft.core.inventory.InventoryBuggyBench;
import micdoodle8.mods.galacticraft.core.inventory.InventoryRocketBench;
import micdoodle8.mods.galacticraft.core.recipe.NasaWorkbenchRecipe;

public class RecipeUtil {

    public static ItemStack findMatchingBuggy(InventoryBuggyBench benchStacks) {
        for (final INasaWorkbenchRecipe recipe : GalacticraftRegistry.getBuggyBenchRecipes()) {
            if (recipe.matches(benchStacks)) {
                return recipe.getRecipeOutput();
            }
        }

        return null;
    }

    public static ItemStack findMatchingSpaceshipRecipe(InventoryRocketBench inventoryRocketBench) {
        for (final INasaWorkbenchRecipe recipe : GalacticraftRegistry.getRocketT1Recipes()) {
            if (recipe.matches(inventoryRocketBench)) {
                return recipe.getRecipeOutput();
            }
        }

        return null;
    }

    public static void addRecipe(ItemStack result, Object[] obj) {
        CraftingManager.getInstance()
            .getRecipeList()
            .add(new ShapedOreRecipe(result, obj));
    }

    public static void addBlockRecipe(ItemStack result, String oreDictIngot, ItemStack gcIngot) {
        if (OreDictionary.getOres(oreDictIngot)
            .size() > 1) {
            CraftingManager.getInstance()
                .getRecipeList()
                .add(
                    new ShapelessOreRecipe(
                        result,
                        gcIngot,
                        oreDictIngot,
                        oreDictIngot,
                        oreDictIngot,
                        oreDictIngot,
                        oreDictIngot,
                        oreDictIngot,
                        oreDictIngot,
                        oreDictIngot));
        } else {
            RecipeUtil.addRecipe(result, new Object[] { "XXX", "XXX", "XXX", 'X', gcIngot });
        }
    }

    public static void addRocketBenchRecipe(ItemStack result, HashMap<Integer, ItemStack> input) {
        GalacticraftRegistry.addT1RocketRecipe(new NasaWorkbenchRecipe(result, input));
    }

    public static void addBuggyBenchRecipe(ItemStack result, HashMap<Integer, ItemStack> input) {
        GalacticraftRegistry.addMoonBuggyRecipe(new NasaWorkbenchRecipe(result, input));
    }

    public static Block getChestBlock() {
        Block block = GameRegistry.findBlock("IronChest", "BlockIronChest");
        if (block == null) {
            block = Blocks.chest;
        }
        return block;
    }

    public static ItemStack getChestItemStack(int size, int meta) {
        final Block block = getChestBlock();
        return new ItemStack(block, size, meta);
    }
}
