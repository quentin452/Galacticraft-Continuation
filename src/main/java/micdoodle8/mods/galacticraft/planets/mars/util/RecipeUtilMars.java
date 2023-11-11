package micdoodle8.mods.galacticraft.planets.mars.util;

import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.inventory.*;
import java.util.*;
import micdoodle8.mods.galacticraft.planets.mars.inventory.*;
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.*;

public class RecipeUtilMars
{
    public static ItemStack findMatchingSpaceshipT2Recipe(final InventorySchematicTier2Rocket inventoryRocketBench) {
        for (final INasaWorkbenchRecipe recipe : GalacticraftRegistry.getRocketT2Recipes()) {
            if (recipe.matches((IInventory)inventoryRocketBench)) {
                return recipe.getRecipeOutput();
            }
        }
        return null;
    }
    
    public static ItemStack findMatchingCargoRocketRecipe(final InventorySchematicCargoRocket inventoryRocketBench) {
        for (final INasaWorkbenchRecipe recipe : GalacticraftRegistry.getCargoRocketRecipes()) {
            if (recipe.matches((IInventory)inventoryRocketBench)) {
                return recipe.getRecipeOutput();
            }
        }
        return null;
    }
    
    public static ItemStack findMatchingSpaceshipT3Recipe(final InventorySchematicTier3Rocket inventoryRocketBench) {
        for (final INasaWorkbenchRecipe recipe : GalacticraftRegistry.getRocketT3Recipes()) {
            if (recipe.matches((IInventory)inventoryRocketBench)) {
                return recipe.getRecipeOutput();
            }
        }
        return null;
    }
    
    public static ItemStack findMatchingAstroMinerRecipe(final InventorySchematicAstroMiner craftMatrix) {
        for (final INasaWorkbenchRecipe recipe : GalacticraftRegistry.getAstroMinerRecipes()) {
            if (recipe.matches((IInventory)craftMatrix)) {
                return recipe.getRecipeOutput();
            }
        }
        return null;
    }
}
