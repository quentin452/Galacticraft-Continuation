package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.inventory.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.items.*;
import java.lang.reflect.*;

public class SlotRefinery extends Slot
{
    public SlotRefinery(final IInventory par1iInventory, final int par2, final int par3, final int par4) {
        super(par1iInventory, par2, par3, par4);
    }
    
    public boolean isItemValid(final ItemStack par1ItemStack) {
        Class<?> buildCraftClass = null;
        try {
            if ((buildCraftClass = Class.forName("buildcraft.BuildCraftEnergy")) != null) {
                for (final Field f : buildCraftClass.getFields()) {
                    if (f.getName().equals("bucketOil")) {
                        final Item item = (Item)f.get(null);
                        if (par1ItemStack.getItem() == item) {
                            return true;
                        }
                    }
                }
            }
        }
        catch (Throwable t) {}
        return par1ItemStack.getItem() instanceof ItemOilCanister && par1ItemStack.getItemDamage() > 0;
    }
}
