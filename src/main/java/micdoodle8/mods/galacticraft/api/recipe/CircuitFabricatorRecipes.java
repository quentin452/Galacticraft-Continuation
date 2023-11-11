package micdoodle8.mods.galacticraft.api.recipe;

import net.minecraft.item.*;
import java.util.*;

public class CircuitFabricatorRecipes
{
    private static HashMap<ItemStack[], ItemStack> recipes;
    public static ArrayList<ArrayList<ItemStack>> slotValidItems;
    
    public static void addRecipe(final ItemStack output, final ItemStack[] inputList) {
        if (inputList.length != 5) {
            throw new RuntimeException("Invalid circuit fabricator recipe!");
        }
        CircuitFabricatorRecipes.recipes.put(inputList, output);
        if (CircuitFabricatorRecipes.slotValidItems.size() == 0) {
            for (int i = 0; i < 5; ++i) {
                final ArrayList<ItemStack> entry = new ArrayList<ItemStack>();
                CircuitFabricatorRecipes.slotValidItems.add(entry);
            }
        }
        for (int i = 0; i < 5; ++i) {
            final ItemStack inputStack = inputList[i];
            if (inputStack != null) {
                final ArrayList<ItemStack> validItems = CircuitFabricatorRecipes.slotValidItems.get(i);
                boolean found = false;
                for (int j = 0; j < validItems.size(); ++j) {
                    if (inputStack.isItemEqual((ItemStack)validItems.get(j))) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    validItems.add(inputStack.copy());
                }
            }
        }
    }
    
    public static ItemStack getOutputForInput(final ItemStack[] inputList) {
        if (inputList.length != 5) {
            return null;
        }
        for (final Map.Entry<ItemStack[], ItemStack> recipe : CircuitFabricatorRecipes.recipes.entrySet()) {
            boolean found = true;
            for (int i = 0; i < 5; ++i) {
                final ItemStack recipeStack = recipe.getKey()[i];
                final ItemStack inputStack = inputList[i];
                if (recipeStack == null || inputStack == null) {
                    if (recipeStack != null || inputStack != null) {
                        found = false;
                        break;
                    }
                }
                else if (recipeStack.getItem() != inputStack.getItem() || recipeStack.getItemDamage() != inputStack.getItemDamage()) {
                    found = false;
                    break;
                }
            }
            if (!found) {
                continue;
            }
            return recipe.getValue();
        }
        return CircuitFabricatorRecipes.recipes.get(inputList);
    }
    
    public static void removeRecipe(final ItemStack match) {
        final Iterator<Map.Entry<ItemStack[], ItemStack>> it = CircuitFabricatorRecipes.recipes.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<ItemStack[], ItemStack> recipe = it.next();
            if (ItemStack.areItemStacksEqual(match, (ItemStack)recipe.getValue())) {
                it.remove();
            }
        }
    }
    
    static {
        CircuitFabricatorRecipes.recipes = new HashMap<ItemStack[], ItemStack>();
        CircuitFabricatorRecipes.slotValidItems = new ArrayList<ArrayList<ItemStack>>(5);
    }
}
