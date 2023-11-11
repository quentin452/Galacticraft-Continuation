package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.inventory.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.items.*;

public class SlotFuelCanister extends Slot
{
    public SlotFuelCanister(final IInventory par1iInventory, final int par2, final int par3, final int par4) {
        super(par1iInventory, par2, par3, par4);
    }
    
    public boolean isItemValid(final ItemStack par1ItemStack) {
        return par1ItemStack.getItem() instanceof ItemFuelCanister && par1ItemStack.getItemDamage() > 0;
    }
}
