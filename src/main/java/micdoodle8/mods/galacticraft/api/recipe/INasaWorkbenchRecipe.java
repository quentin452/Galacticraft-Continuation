package micdoodle8.mods.galacticraft.api.recipe;

import net.minecraft.inventory.*;
import net.minecraft.item.*;
import java.util.*;

public interface INasaWorkbenchRecipe
{
    boolean matches(final IInventory p0);
    
    int getRecipeSize();
    
    ItemStack getRecipeOutput();
    
    HashMap<Integer, ItemStack> getRecipeInput();
}
