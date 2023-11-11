package micdoodle8.mods.galacticraft.core.recipe;

import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.item.*;
import net.minecraft.inventory.*;
import java.util.*;

public class NasaWorkbenchRecipe implements INasaWorkbenchRecipe
{
    private ItemStack output;
    private HashMap<Integer, ItemStack> input;
    
    public NasaWorkbenchRecipe(final ItemStack output, final HashMap<Integer, ItemStack> input) {
        this.output = output;
        this.input = input;
    }
    
    public boolean matches(final IInventory inventory) {
        for (final Map.Entry<Integer, ItemStack> entry : this.input.entrySet()) {
            final ItemStack stackAt = inventory.getStackInSlot((int)entry.getKey());
            if (!this.checkItemEquals(stackAt, entry.getValue())) {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkItemEquals(final ItemStack target, final ItemStack input) {
        return (input != null || target == null) && (input == null || target != null) && ((target == null && input == null) || (target.getItem() == input.getItem() && (target.getItemDamage() == 32767 || target.getItemDamage() == input.getItemDamage())));
    }
    
    public int getRecipeSize() {
        return this.input.size();
    }
    
    public ItemStack getRecipeOutput() {
        return this.output.copy();
    }
    
    public HashMap<Integer, ItemStack> getRecipeInput() {
        return this.input;
    }
}
