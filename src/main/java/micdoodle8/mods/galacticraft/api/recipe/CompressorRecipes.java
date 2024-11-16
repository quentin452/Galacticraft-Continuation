package micdoodle8.mods.galacticraft.api.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import micdoodle8.mods.galacticraft.api.GalacticraftConfigAccess;

public class CompressorRecipes {

    private static final List<IRecipe> recipes = new ArrayList<>();
    private static final List<IRecipe> recipesAdventure = new ArrayList<>();
    private static boolean adventureOnly = false;

    public static ShapedRecipes addRecipe(ItemStack output, Object... inputList) {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (inputList[i] instanceof String[]) {
            final String[] astring = (String[]) inputList[i];
            i++;

            for (final String s1 : astring) {
                ++k;
                j = s1.length();
                s = s + s1;
            }
        } else {
            while (inputList[i] instanceof String) {
                final String s2 = (String) inputList[i];
                i++;
                ++k;
                j = s2.length();
                s = s + s2;
            }
        }

        HashMap<Character, ItemStack> hashmap;

        for (hashmap = new HashMap<>(); i < inputList.length; i += 2) {
            final Character character = (Character) inputList[i];
            ItemStack itemstack1 = null;

            if (inputList[i + 1] instanceof Item) {
                itemstack1 = new ItemStack((Item) inputList[i + 1]);
            } else if (inputList[i + 1] instanceof Block) {
                itemstack1 = new ItemStack((Block) inputList[i + 1], 1, 32767);
            } else if (inputList[i + 1] instanceof ItemStack) {
                itemstack1 = (ItemStack) inputList[i + 1];
            }

            hashmap.put(character, itemstack1);
        }

        final ItemStack[] aitemstack = new ItemStack[j * k];

        for (int i1 = 0; i1 < j * k; ++i1) {
            final char c0 = s.charAt(i1);

            if (hashmap.containsKey(Character.valueOf(c0))) {
                aitemstack[i1] = hashmap.get(Character.valueOf(c0))
                    .copy();
            } else {
                aitemstack[i1] = null;
            }
        }

        final ShapedRecipes shapedrecipes = new ShapedRecipes(j, k, aitemstack, output);
        if (!adventureOnly) {
            CompressorRecipes.recipes.add(shapedrecipes);
        }
        CompressorRecipes.recipesAdventure.add(shapedrecipes);
        return shapedrecipes;
    }

    public static void addShapelessRecipe(ItemStack par1ItemStack, Object... par2ArrayOfObj) {
        final List<Object> arraylist = new ArrayList<>();
        final int i = par2ArrayOfObj.length;

        for (final Object object1 : par2ArrayOfObj) {
            if (object1 instanceof ItemStack) {
                ItemStack stack = (ItemStack) object1;
                arraylist.add(stack.copy());
            } else if (object1 instanceof Item) {
                Item item = (Item) object1;
                arraylist.add(new ItemStack(item));
            } else if (object1 instanceof String) {
                arraylist.add(object1);
            } else {
                if (!(object1 instanceof Block)) {
                    throw new RuntimeException("Invalid shapeless compressor recipe!");
                }

                Block block = (Block) object1;
                arraylist.add(new ItemStack(block));
            }
        }

        final IRecipe toAdd = new ShapelessOreRecipe(par1ItemStack, arraylist.toArray());
        if (!adventureOnly) {
            CompressorRecipes.recipes.add(toAdd);
        }
        CompressorRecipes.recipesAdventure.add(toAdd);
    }

    public static ShapedRecipes addRecipeAdventure(ItemStack output, Object... inputList) {
        adventureOnly = true;
        final ShapedRecipes returnValue = CompressorRecipes.addRecipe(output, inputList);
        adventureOnly = false;
        return returnValue;
    }

    public static void addShapelessAdventure(ItemStack par1ItemStack, Object... par2ArrayOfObj) {
        adventureOnly = true;
        CompressorRecipes.addShapelessRecipe(par1ItemStack, par2ArrayOfObj);
        adventureOnly = false;
    }

    public static ItemStack findMatchingRecipe(IInventory inventory, World par2World) {
        int i = 0;
        ItemStack itemstack = null;
        ItemStack itemstack1 = null;
        int j;

        for (j = 0; j < inventory.getSizeInventory(); ++j) {
            final ItemStack itemstack2 = inventory.getStackInSlot(j);

            if (itemstack2 != null) {
                if (i == 0) {
                    itemstack = itemstack2;
                }

                if (i == 1) {
                    itemstack1 = itemstack2;
                }

                ++i;
            }
        }

        if (i == 2 && itemstack.getItem() == itemstack1.getItem()
            && itemstack.stackSize == 1
            && itemstack1.stackSize == 1
            && itemstack.getItem()
                .isRepairable()) {
            final int k = itemstack.getItem()
                .getMaxDamage() - itemstack.getItemDamageForDisplay();
            final int l = itemstack.getItem()
                .getMaxDamage() - itemstack1.getItemDamageForDisplay();
            final int i1 = k + l
                + itemstack.getItem()
                    .getMaxDamage() * 5
                    / 100;
            int j1 = itemstack.getItem()
                .getMaxDamage() - i1;

            if (j1 < 0) {
                j1 = 0;
            }

            return new ItemStack(itemstack.getItem(), 1, j1);
        }
        final List<IRecipe> theRecipes = CompressorRecipes.getRecipeList();

        for (j = 0; j < theRecipes.size(); ++j) {
            final IRecipe irecipe = theRecipes.get(j);

            if (irecipe instanceof ShapedRecipes && CompressorRecipes.matches((ShapedRecipes) irecipe, inventory)
                || irecipe instanceof ShapelessOreRecipe
                    && CompressorRecipes.matchesShapeless((ShapelessOreRecipe) irecipe, inventory)) {
                return irecipe.getRecipeOutput()
                    .copy();
            }
        }

        return null;
    }

    private static boolean matches(ShapedRecipes recipe, IInventory inventory) {
        for (int i = 0; i <= 3 - recipe.recipeWidth; ++i) {
            for (int j = 0; j <= 3 - recipe.recipeHeight; ++j) {
                if (CompressorRecipes.checkMatch(recipe, inventory, i, j, true)
                    || CompressorRecipes.checkMatch(recipe, inventory, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean checkMatch(ShapedRecipes recipe, IInventory inventory, int par2, int par3, boolean par4) {
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 3; ++l) {
                final int i1 = k - par2;
                final int j1 = l - par3;
                ItemStack itemstack = null;

                if (i1 >= 0 && j1 >= 0 && i1 < recipe.recipeWidth && j1 < recipe.recipeHeight) {
                    if (par4) {
                        itemstack = recipe.recipeItems[recipe.recipeWidth - i1 - 1 + j1 * recipe.recipeWidth];
                    } else {
                        itemstack = recipe.recipeItems[i1 + j1 * recipe.recipeWidth];
                    }
                }

                ItemStack itemstack1 = null;

                if (k >= 0 && l < 3) {
                    final int k2 = k + l * 3;
                    itemstack1 = inventory.getStackInSlot(k2);
                }

                if (itemstack1 != null || itemstack != null) {
                    if ((itemstack1 == null == (itemstack != null)) || itemstack.getItem() != itemstack1.getItem()
                        || itemstack.getItemDamage() != 32767
                            && itemstack.getItemDamage() != itemstack1.getItemDamage()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static boolean matchesShapeless(ShapelessOreRecipe recipe, IInventory var1) {
        final ArrayList<Object> required = new ArrayList<>(recipe.getInput());

        for (int x = 0; x < var1.getSizeInventory(); x++) {
            final ItemStack slot = var1.getStackInSlot(x);

            if (slot != null) {
                boolean inRecipe = false;
                for (Object next : required) {
                    boolean match = false;

                    if (next instanceof ItemStack) {
                        match = OreDictionary.itemMatches((ItemStack) next, slot, false);
                    } else if (next instanceof ArrayList) {
                        @SuppressWarnings("unchecked")
                        final Iterator<ItemStack> itr = ((ArrayList<ItemStack>) next).iterator();
                        while (itr.hasNext() && !match) {
                            match = OreDictionary.itemMatches(itr.next(), slot, false);
                        }
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
        return GalacticraftConfigAccess.getChallengeRecipes() ? CompressorRecipes.recipesAdventure
            : CompressorRecipes.recipes;
    }

    public static void removeRecipe(ItemStack match) {
        for (final Iterator<IRecipe> it = CompressorRecipes.getRecipeList()
            .iterator(); it.hasNext();) {
            final IRecipe irecipe = it.next();
            if (ItemStack.areItemStacksEqual(match, irecipe.getRecipeOutput())) {
                it.remove();
            }
        }
    }
}
