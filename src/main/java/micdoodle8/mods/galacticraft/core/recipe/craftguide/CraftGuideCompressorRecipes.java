package micdoodle8.mods.galacticraft.core.recipe.craftguide;

import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.item.crafting.*;
import net.minecraftforge.oredict.*;
import java.util.*;
import uristqwerty.CraftGuide.api.*;

public class CraftGuideCompressorRecipes implements RecipeProvider
{
    private final Slot[] slots;
    
    public CraftGuideCompressorRecipes() {
        this.slots = new Slot[11];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.slots[i + j * 3] = (Slot)new ItemSlot(i * 18 + 3, j * 18 + 3, 16, 16).drawOwnBackground();
            }
        }
        this.slots[9] = (Slot)new ItemSlot(59, 21, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT).drawOwnBackground();
        this.slots[10] = (Slot)new ItemSlot(59, 3, 16, 16).setSlotType(SlotType.MACHINE_SLOT);
    }
    
    public void generateRecipes(final RecipeGenerator generator) {
        final ItemStack machine = new ItemStack(GCBlocks.machineBase, 1, 12);
        final RecipeTemplate template = generator.createRecipeTemplate(this.slots, machine);
        for (int i = 0; i < CompressorRecipes.getRecipeList().size(); ++i) {
            final Object[] array = new Object[11];
            final IRecipe rec = CompressorRecipes.getRecipeList().get(i);
            if (rec instanceof ShapedRecipes) {
                final ShapedRecipes recipe = (ShapedRecipes)rec;
                for (int j = 0; j < recipe.recipeItems.length; ++j) {
                    final ItemStack stack = recipe.recipeItems[j];
                    array[j] = stack.copy();
                }
                array[9] = recipe.getRecipeOutput().copy();
            }
            else if (rec instanceof ShapelessOreRecipe) {
                final ShapelessOreRecipe recipe2 = (ShapelessOreRecipe)rec;
                for (int j = 0; j < recipe2.getInput().size(); ++j) {
                    final Object obj = recipe2.getInput().get(j);
                    if (obj instanceof ItemStack) {
                        array[j] = ((ItemStack)obj).copy();
                    }
                    else if (obj instanceof String) {
                        array[j] = OreDictionary.getOres((String)obj).clone();
                    }
                    else if (obj instanceof ArrayList) {
                        array[j] = ((ArrayList)obj).clone();
                    }
                }
                array[9] = recipe2.getRecipeOutput().copy();
            }
            array[10] = machine;
            generator.addRecipe(template, array);
        }
    }
}
