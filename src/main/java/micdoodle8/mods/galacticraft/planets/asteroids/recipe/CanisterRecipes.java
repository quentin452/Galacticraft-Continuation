package micdoodle8.mods.galacticraft.planets.asteroids.recipe;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank;
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemCanisterLiquidOxygen;

public class CanisterRecipes extends ShapelessRecipes {

    public CanisterRecipes(ItemStack stack, List<ItemStack> list) {
        super(stack, list);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(InventoryCrafting p_77569_1_, World p_77569_2_) {
        ItemStack itemCanister = null;
        ItemStack itemTank = null;

        for (int i = 0; i < p_77569_1_.getSizeInventory(); ++i) {
            final ItemStack itemstack1 = p_77569_1_.getStackInSlot(i);

            if (itemstack1 != null) {
                final Item testItem = itemstack1.getItem();
                if (testItem instanceof ItemCanisterLiquidOxygen || testItem == GCItems.oxygenCanisterInfinite) {
                    if (itemCanister != null) {
                        // Two canisters
                        return false;
                    }

                    itemCanister = itemstack1;
                } else {
                    if (!(testItem instanceof ItemOxygenTank) || itemTank != null) {
                        // Something other than an oxygen tank
                        return false;
                    }

                    itemTank = itemstack1;
                }
            }
        }

        // Need one canister + one tank
        // Empty canister
        if (itemCanister == null || itemTank == null || itemCanister.getItemDamage() >= itemCanister.getMaxDamage()) {
            return false;
        }

        // Full tank
        return itemTank.getItemDamage() > 0;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack itemTank = null;
        ItemStack itemCanister = null;

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            final ItemStack itemstack1 = inv.getStackInSlot(i);

            if (itemstack1 != null) {
                final Item testItem = itemstack1.getItem();
                if (testItem instanceof ItemCanisterLiquidOxygen || testItem == GCItems.oxygenCanisterInfinite) {
                    if (itemCanister != null) {
                        // Two canisters
                        return null;
                    }

                    itemCanister = itemstack1;
                } else {
                    if (!(testItem instanceof ItemOxygenTank) || itemTank != null) {
                        // Something other than an oxygen tank
                        return null;
                    }

                    itemTank = itemstack1;
                }
            }
        }

        // Need one canister + one tank

        // Empty canister
        // Full tank
        if (itemCanister == null || itemTank == null
            || itemCanister.getItemDamage() >= itemCanister.getMaxDamage()
            || itemTank.getItemDamage() <= 0) {
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
        final ItemStack result = itemTank.copy();
        result.setItemDamage(tankDamageNew);
        if (itemCanister.getItem() instanceof ItemCanisterLiquidOxygen) {
            ItemCanisterLiquidOxygen.saveDamage(itemCanister, itemCanister.getMaxDamage());
        }
        return result;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }
}
