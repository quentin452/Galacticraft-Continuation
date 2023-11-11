package micdoodle8.mods.galacticraft.api.recipe;

import net.minecraft.item.*;
import net.minecraft.block.*;
import net.minecraftforge.oredict.*;
import cpw.mods.fml.common.*;
import net.minecraft.entity.player.*;
import java.util.*;

public class SpaceStationRecipe
{
    private final HashMap<Object, Integer> input;

    public SpaceStationRecipe(final HashMap<Object, Integer> objMap) {
        this.input = new HashMap<Object, Integer>();
        for (final Object obj : objMap.keySet()) {
            final Integer amount = objMap.get(obj);
            if (obj instanceof ItemStack) {
                this.input.put(((ItemStack)obj).copy(), amount);
            }
            else if (obj instanceof Item) {
                this.input.put(new ItemStack((Item)obj), amount);
            }
            else if (obj instanceof Block) {
                this.input.put(new ItemStack((Block)obj), amount);
            }
            else if (obj instanceof String) {
                FMLLog.info("While registering space station recipe, found " + OreDictionary.getOres((String)obj).size() + " type(s) of " + obj, new Object[0]);
                this.input.put(OreDictionary.getOres((String)obj), amount);
            }
            else {
                if (!(obj instanceof ArrayList)) {
                    throw new RuntimeException("INVALID SPACE STATION RECIPE");
                }
                this.input.put(obj, amount);
            }
        }
    }

    public int getRecipeSize() {
        return this.input.size();
    }

    public boolean matches(final EntityPlayer player, final boolean remove) {
        final HashMap<Object, Integer> required = new HashMap<Object, Integer>();
        required.putAll(this.input);
        for (final Object next : this.input.keySet()) {
            final int amountRequired = required.get(next);
            int amountInInv = 0;
            for (int x = 0; x < player.inventory.getSizeInventory(); ++x) {
                final ItemStack slot = player.inventory.getStackInSlot(x);
                if (slot != null) {
                    if (next instanceof ItemStack) {
                        if (checkItemEquals((ItemStack)next, slot)) {
                            amountInInv += slot.stackSize;
                        }
                    }
                    else if (next instanceof ArrayList) {
                        for (final Object item : (ArrayList)next) {
                            if (checkItemEquals((ItemStack) item, slot)) {
                                amountInInv += slot.stackSize;
                            }
                        }
                    }
                }
            }
            if (amountInInv >= amountRequired) {
                required.remove(next);
            }
        }
        if (required.isEmpty() && remove) {
            this.removeItems(player);
        }
        return required.isEmpty();
    }

    public void removeItems(final EntityPlayer player) {
        final HashMap<Object, Integer> required = new HashMap<Object, Integer>(this.input);
        for (final Object next : required.keySet()) {
            final int amountRequired = required.get(next);
            int amountRemoved = 0;
            for (int x = 0; x < player.inventory.getSizeInventory(); ++x) {
                final ItemStack slot = player.inventory.getStackInSlot(x);
                if (slot != null) {
                    final int amountRemaining = amountRequired - amountRemoved;
                    if (next instanceof ItemStack) {
                        if (checkItemEquals((ItemStack)next, slot)) {
                            final int amountToRemove = Math.min(slot.stackSize, amountRemaining);
                            final ItemStack copy;
                            ItemStack newStack = copy = slot.copy();
                            copy.stackSize -= amountToRemove;
                            if (newStack.stackSize <= 0) {
                                newStack = null;
                            }
                            player.inventory.setInventorySlotContents(x, newStack);
                            amountRemoved += amountToRemove;
                        }
                    }
                    else if (next instanceof ArrayList) {
                        for (final Object item : (ArrayList)next) {
                            if (checkItemEquals((ItemStack) item, slot)) {
                                final int amountToRemove2 = Math.min(slot.stackSize, amountRemaining);
                                final ItemStack copy2;
                                ItemStack newStack2 = copy2 = slot.copy();
                                copy2.stackSize -= amountToRemove2;
                                if (newStack2.stackSize <= 0) {
                                    newStack2 = null;
                                }
                                player.inventory.setInventorySlotContents(x, newStack2);
                                amountRemoved += amountToRemove2;
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean checkItemEquals(final ItemStack target, final ItemStack input) {
        return target.getItem() == input.getItem() && (target.getItemDamage() == 32767 || target.getItemDamage() == input.getItemDamage());
    }

    public HashMap<Object, Integer> getInput() {
        return this.input;
    }
}
