package micdoodle8.mods.galacticraft.api.recipe;

import java.lang.reflect.*;
import net.minecraft.item.crafting.*;
import net.minecraft.item.*;
import net.minecraft.block.*;
import net.minecraft.inventory.*;
import net.minecraft.world.*;
import net.minecraftforge.oredict.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.*;

public class CompressorRecipes
{
    private static List<IRecipe> recipes;
    private static List<IRecipe> recipesAdventure;
    private static boolean adventureOnly;
    private static Field adventureFlag;
    private static boolean flagNotCached;
    
    public static ShapedRecipes addRecipe(final ItemStack output, final Object... inputList) {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;
        if (inputList[i] instanceof String[]) {
            final String[] array;
            final String[] astring = array = (String[])inputList[i++];
            for (final String s2 : array) {
                ++k;
                j = s2.length();
                s += s2;
            }
        }
        else {
            while (inputList[i] instanceof String) {
                final String s3 = (String)inputList[i++];
                ++k;
                j = s3.length();
                s += s3;
            }
        }
        final HashMap<Character, ItemStack> hashmap = new HashMap<Character, ItemStack>();
        while (i < inputList.length) {
            final Character character = (Character)inputList[i];
            ItemStack itemstack1 = null;
            if (inputList[i + 1] instanceof Item) {
                itemstack1 = new ItemStack((Item)inputList[i + 1]);
            }
            else if (inputList[i + 1] instanceof Block) {
                itemstack1 = new ItemStack((Block)inputList[i + 1], 1, 32767);
            }
            else if (inputList[i + 1] instanceof ItemStack) {
                itemstack1 = (ItemStack)inputList[i + 1];
            }
            hashmap.put(character, itemstack1);
            i += 2;
        }
        final ItemStack[] aitemstack = new ItemStack[j * k];
        for (int i2 = 0; i2 < j * k; ++i2) {
            final char c0 = s.charAt(i2);
            if (hashmap.containsKey(c0)) {
                aitemstack[i2] = hashmap.get(c0).copy();
            }
            else {
                aitemstack[i2] = null;
            }
        }
        final ShapedRecipes shapedrecipes = new ShapedRecipes(j, k, aitemstack, output);
        if (!CompressorRecipes.adventureOnly) {
            CompressorRecipes.recipes.add((IRecipe)shapedrecipes);
        }
        CompressorRecipes.recipesAdventure.add((IRecipe)shapedrecipes);
        return shapedrecipes;
    }
    
    public static void addShapelessRecipe(final ItemStack par1ItemStack, final Object... par2ArrayOfObj) {
        final ArrayList arraylist = new ArrayList();
        for (final Object object1 : par2ArrayOfObj) {
            if (object1 instanceof ItemStack) {
                arraylist.add(((ItemStack)object1).copy());
            }
            else if (object1 instanceof Item) {
                arraylist.add(new ItemStack((Item)object1));
            }
            else if (object1 instanceof String) {
                arraylist.add(object1);
            }
            else {
                if (!(object1 instanceof Block)) {
                    throw new RuntimeException("Invalid shapeless compressor recipe!");
                }
                arraylist.add(new ItemStack((Block)object1));
            }
        }
        final IRecipe toAdd = (IRecipe)new ShapelessOreRecipe(par1ItemStack, arraylist.toArray());
        if (!CompressorRecipes.adventureOnly) {
            CompressorRecipes.recipes.add(toAdd);
        }
        CompressorRecipes.recipesAdventure.add(toAdd);
    }
    
    public static ShapedRecipes addRecipeAdventure(final ItemStack output, final Object... inputList) {
        CompressorRecipes.adventureOnly = true;
        final ShapedRecipes returnValue = addRecipe(output, inputList);
        CompressorRecipes.adventureOnly = false;
        return returnValue;
    }
    
    public static void addShapelessAdventure(final ItemStack par1ItemStack, final Object... par2ArrayOfObj) {
        CompressorRecipes.adventureOnly = true;
        addShapelessRecipe(par1ItemStack, par2ArrayOfObj);
        CompressorRecipes.adventureOnly = false;
    }
    
    public static ItemStack findMatchingRecipe(final IInventory inventory, final World par2World) {
        int i = 0;
        ItemStack itemstack = null;
        ItemStack itemstack2 = null;
        for (int j = 0; j < inventory.getSizeInventory(); ++j) {
            final ItemStack itemstack3 = inventory.getStackInSlot(j);
            if (itemstack3 != null) {
                if (i == 0) {
                    itemstack = itemstack3;
                }
                if (i == 1) {
                    itemstack2 = itemstack3;
                }
                ++i;
            }
        }
        if (i == 2 && itemstack.getItem() == itemstack2.getItem() && itemstack.stackSize == 1 && itemstack2.stackSize == 1 && itemstack.getItem().isRepairable()) {
            final int k = itemstack.getItem().getMaxDamage() - itemstack.getItemDamageForDisplay();
            final int l = itemstack.getItem().getMaxDamage() - itemstack2.getItemDamageForDisplay();
            final int i2 = k + l + itemstack.getItem().getMaxDamage() * 5 / 100;
            int j2 = itemstack.getItem().getMaxDamage() - i2;
            if (j2 < 0) {
                j2 = 0;
            }
            return new ItemStack(itemstack.getItem(), 1, j2);
        }
        final List<IRecipe> theRecipes = getRecipeList();
        for (int j = 0; j < theRecipes.size(); ++j) {
            final IRecipe irecipe = theRecipes.get(j);
            if (irecipe instanceof ShapedRecipes && matches((ShapedRecipes)irecipe, inventory, par2World)) {
                return irecipe.getRecipeOutput().copy();
            }
            if (irecipe instanceof ShapelessOreRecipe && matchesShapeless((ShapelessOreRecipe)irecipe, inventory, par2World)) {
                return irecipe.getRecipeOutput().copy();
            }
        }
        return null;
    }
    
    private static boolean matches(final ShapedRecipes recipe, final IInventory inventory, final World par2World) {
        for (int i = 0; i <= 3 - recipe.recipeWidth; ++i) {
            for (int j = 0; j <= 3 - recipe.recipeHeight; ++j) {
                if (checkMatch(recipe, inventory, i, j, true)) {
                    return true;
                }
                if (checkMatch(recipe, inventory, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean checkMatch(final ShapedRecipes recipe, final IInventory inventory, final int par2, final int par3, final boolean par4) {
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 3; ++l) {
                final int i1 = k - par2;
                final int j1 = l - par3;
                ItemStack itemstack = null;
                if (i1 >= 0 && j1 >= 0 && i1 < recipe.recipeWidth && j1 < recipe.recipeHeight) {
                    if (par4) {
                        itemstack = recipe.recipeItems[recipe.recipeWidth - i1 - 1 + j1 * recipe.recipeWidth];
                    }
                    else {
                        itemstack = recipe.recipeItems[i1 + j1 * recipe.recipeWidth];
                    }
                }
                ItemStack itemstack2 = null;
                if (k >= 0 && l < 3) {
                    final int k2 = k + l * 3;
                    itemstack2 = inventory.getStackInSlot(k2);
                }
                if (itemstack2 != null || itemstack != null) {
                    if ((itemstack2 == null && itemstack != null) || (itemstack2 != null && itemstack == null)) {
                        return false;
                    }
                    if (itemstack.getItem() != itemstack2.getItem()) {
                        return false;
                    }
                    if (itemstack.getItemDamage() != 32767 && itemstack.getItemDamage() != itemstack2.getItemDamage()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private static boolean matchesShapeless(final ShapelessOreRecipe recipe, final IInventory var1, final World par2World) {
        final ArrayList<Object> required = new ArrayList<Object>(recipe.getInput());
        for (int x = 0; x < var1.getSizeInventory(); ++x) {
            final ItemStack slot = var1.getStackInSlot(x);
            if (slot != null) {
                boolean inRecipe = false;
                final Iterator<Object> req = required.iterator();
                while (req.hasNext()) {
                    boolean match = false;
                    final Object next = req.next();
                    if (next instanceof ItemStack) {
                        match = OreDictionary.itemMatches((ItemStack)next, slot, false);
                    }
                    else if (next instanceof ArrayList) {
                        for (Iterator<ItemStack> itr = ((ArrayList)next).iterator(); itr.hasNext() && !match; match = OreDictionary.itemMatches((ItemStack)itr.next(), slot, false)) {}
                    }
                    if (match) {
                        inRecipe = true;
                        required.remove(next);
                        break;
                    }
                }
                if (!inRecipe) {
                    return false;
                }
            }
        }
        return required.isEmpty();
    }
    
    public static List<IRecipe> getRecipeList() {
        return GalacticraftConfigAccess.getChallengeRecipes() ? CompressorRecipes.recipesAdventure : CompressorRecipes.recipes;
    }
    
    public static void removeRecipe(final ItemStack match) {
        final Iterator<IRecipe> it = getRecipeList().iterator();
        while (it.hasNext()) {
            final IRecipe irecipe = it.next();
            if (ItemStack.areItemStacksEqual(match, irecipe.getRecipeOutput())) {
                it.remove();
            }
        }
    }
    
    static {
        CompressorRecipes.recipes = new ArrayList<IRecipe>();
        CompressorRecipes.recipesAdventure = new ArrayList<IRecipe>();
        CompressorRecipes.adventureOnly = false;
        CompressorRecipes.flagNotCached = true;
    }
}
