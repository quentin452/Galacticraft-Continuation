package micdoodle8.mods.galacticraft.core.util;

import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.item.crafting.*;
import net.minecraftforge.oredict.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.recipe.*;
import ic2.api.item.*;

public class RecipeUtil
{
    public static ItemStack findMatchingBuggy(final InventoryBuggyBench benchStacks) {
        for (final INasaWorkbenchRecipe recipe : GalacticraftRegistry.getBuggyBenchRecipes()) {
            if (recipe.matches((IInventory)benchStacks)) {
                return recipe.getRecipeOutput();
            }
        }
        return null;
    }
    
    public static ItemStack findMatchingSpaceshipRecipe(final InventoryRocketBench inventoryRocketBench) {
        for (final INasaWorkbenchRecipe recipe : GalacticraftRegistry.getRocketT1Recipes()) {
            if (recipe.matches((IInventory)inventoryRocketBench)) {
                return recipe.getRecipeOutput();
            }
        }
        return null;
    }
    
    public static void addRecipe(final ItemStack result, final Object[] obj) {
        CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(result, obj));
    }
    
    public static void addBlockRecipe(final ItemStack result, final String oreDictIngot, final ItemStack gcIngot) {
        if (OreDictionary.getOres(oreDictIngot).size() > 1) {
            CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(result, new Object[] { gcIngot, oreDictIngot, oreDictIngot, oreDictIngot, oreDictIngot, oreDictIngot, oreDictIngot, oreDictIngot, oreDictIngot }));
        }
        else {
            addRecipe(result, new Object[] { "XXX", "XXX", "XXX", 'X', gcIngot });
        }
    }
    
    public static void addRocketBenchRecipe(final ItemStack result, final HashMap<Integer, ItemStack> input) {
        GalacticraftRegistry.addT1RocketRecipe((INasaWorkbenchRecipe)new NasaWorkbenchRecipe(result, (HashMap)input));
    }
    
    public static void addBuggyBenchRecipe(final ItemStack result, final HashMap<Integer, ItemStack> input) {
        GalacticraftRegistry.addMoonBuggyRecipe((INasaWorkbenchRecipe)new NasaWorkbenchRecipe(result, (HashMap)input));
    }
    
    public static ItemStack getIndustrialCraftItem(final String indentifier) {
        return IC2Items.getItem(indentifier);
    }
}
