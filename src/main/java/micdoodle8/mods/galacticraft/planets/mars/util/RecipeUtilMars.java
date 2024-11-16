package micdoodle8.mods.galacticraft.planets.mars.util;

import net.minecraft.item.ItemStack;

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe;
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.InventorySchematicAstroMiner;
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.InventorySchematicTier3Rocket;
import micdoodle8.mods.galacticraft.planets.mars.inventory.InventorySchematicCargoRocket;
import micdoodle8.mods.galacticraft.planets.mars.inventory.InventorySchematicTier2Rocket;

public class RecipeUtilMars {

    public static ItemStack findMatchingSpaceshipT2Recipe(InventorySchematicTier2Rocket inventoryRocketBench) {
        for (final INasaWorkbenchRecipe recipe : GalacticraftRegistry.getRocketT2Recipes()) {
            if (recipe.matches(inventoryRocketBench)) {
                return recipe.getRecipeOutput();
            }
        }

        return null;
    }

    public static ItemStack findMatchingCargoRocketRecipe(InventorySchematicCargoRocket inventoryRocketBench) {
        for (final INasaWorkbenchRecipe recipe : GalacticraftRegistry.getCargoRocketRecipes()) {
            if (recipe.matches(inventoryRocketBench)) {
                return recipe.getRecipeOutput();
            }
        }

        return null;
    }

    public static ItemStack findMatchingSpaceshipT3Recipe(InventorySchematicTier3Rocket inventoryRocketBench) {
        for (final INasaWorkbenchRecipe recipe : GalacticraftRegistry.getRocketT3Recipes()) {
            if (recipe.matches(inventoryRocketBench)) {
                return recipe.getRecipeOutput();
            }
        }

        return null;
    }

    public static ItemStack findMatchingAstroMinerRecipe(InventorySchematicAstroMiner craftMatrix) {
        for (final INasaWorkbenchRecipe recipe : GalacticraftRegistry.getAstroMinerRecipes()) {
            if (recipe.matches(craftMatrix)) {
                return recipe.getRecipeOutput();
            }
        }

        return null;
    }
}
