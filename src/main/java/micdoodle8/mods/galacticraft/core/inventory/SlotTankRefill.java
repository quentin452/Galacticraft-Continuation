package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.inventory.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class SlotTankRefill extends Slot
{
    public SlotTankRefill(final IInventory par3IInventory, final int par4, final int par5, final int par6) {
        super(par3IInventory, par4, par5, par6);
    }
    
    public boolean isItemValid(final ItemStack par1ItemStack) {
        if (this.slotNumber == 49) {
            return par1ItemStack.getItem() instanceof ItemParaChute;
        }
        return OxygenUtil.isItemValidForPlayerTankInv(this.slotNumber - 45, par1ItemStack);
    }
    
    public int getSlotStackLimit() {
        return 1;
    }
}
