package micdoodle8.mods.galacticraft.planets.asteroids.recipe;

import net.minecraft.item.crafting.*;
import java.util.*;
import net.minecraft.inventory.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.item.*;

public class CanisterRecipes extends ShapelessRecipes
{
    public CanisterRecipes(final ItemStack stack, final List list) {
        super(stack, list);
    }
    
    public boolean matches(final InventoryCrafting p_77569_1_, final World p_77569_2_) {
        ItemStack itemCanister = null;
        ItemStack itemTank = null;
        for (int i = 0; i < p_77569_1_.getSizeInventory(); ++i) {
            final ItemStack itemstack1 = p_77569_1_.getStackInSlot(i);
            if (itemstack1 != null) {
                final Item testItem = itemstack1.getItem();
                if (testItem instanceof ItemCanisterLiquidOxygen || testItem == GCItems.oxygenCanisterInfinite) {
                    if (itemCanister != null) {
                        return false;
                    }
                    itemCanister = itemstack1;
                }
                else {
                    if (!(testItem instanceof ItemOxygenTank) || itemTank != null) {
                        return false;
                    }
                    itemTank = itemstack1;
                }
            }
        }
        return itemCanister != null && itemTank != null && itemCanister.getItemDamage() < itemCanister.getMaxDamage() && itemTank.getItemDamage() > 0;
    }
    
    public ItemStack getCraftingResult(final InventoryCrafting inv) {
        ItemStack itemTank = null;
        ItemStack itemCanister = null;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            final ItemStack itemstack1 = inv.getStackInSlot(i);
            if (itemstack1 != null) {
                final Item testItem = itemstack1.getItem();
                if (testItem instanceof ItemCanisterLiquidOxygen || testItem == GCItems.oxygenCanisterInfinite) {
                    if (itemCanister != null) {
                        return null;
                    }
                    itemCanister = itemstack1;
                }
                else {
                    if (!(testItem instanceof ItemOxygenTank) || itemTank != null) {
                        return null;
                    }
                    itemTank = itemstack1;
                }
            }
        }
        if (itemCanister == null || itemTank == null) {
            return null;
        }
        if (itemCanister.getItemDamage() >= itemCanister.getMaxDamage()) {
            return null;
        }
        if (itemTank.getItemDamage() <= 0) {
            return null;
        }
        final int oxygenAvail = itemCanister.getMaxDamage() - itemCanister.getItemDamage();
        final int oxygenToFill = itemTank.getItemDamage() * 5 / 54;
        if (oxygenAvail >= oxygenToFill) {
            final ItemStack result = itemTank.copy();
            result.setItemDamage(0);
            if (itemCanister.getItem() instanceof ItemCanisterLiquidOxygen) {
                ItemCanisterLiquidOxygen.saveDamage(itemCanister, itemCanister.getItemDamage() + oxygenToFill);
            }
            return result;
        }
        final int tankDamageNew = (oxygenToFill - oxygenAvail) * 54 / 5;
        final ItemStack result2 = itemTank.copy();
        result2.setItemDamage(tankDamageNew);
        if (itemCanister.getItem() instanceof ItemCanisterLiquidOxygen) {
            ItemCanisterLiquidOxygen.saveDamage(itemCanister, itemCanister.getMaxDamage());
        }
        return result2;
    }
    
    public int getRecipeSize() {
        return 2;
    }
}
