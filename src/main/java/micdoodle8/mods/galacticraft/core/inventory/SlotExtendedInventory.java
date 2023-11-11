package micdoodle8.mods.galacticraft.core.inventory;

import micdoodle8.mods.galacticraft.core.items.GCItems;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.api.item.*;

public class SlotExtendedInventory extends Slot
{
    public SlotExtendedInventory(final IInventory par2IInventory, final int par3, final int par4, final int par5) {
        super(par2IInventory, par3, par4, par5);
    }

    public int getSlotStackLimit() {
        return 1;
    }

    public boolean isItemValid(final ItemStack itemstack) {
        switch (this.getSlotIndex()) {
            case 0: {
                return itemstack.getItem() instanceof ItemOxygenMask;
            }
            case 1: {
                return itemstack.getItem() == GCItems.oxygenGear;
            }
            case 2: {
                return itemstack.getItem() instanceof ItemOxygenTank;
            }
            case 3: {
                return itemstack.getItem() instanceof ItemOxygenTank;
            }
            case 4: {
                return itemstack.getItem() instanceof ItemParaChute;
            }
            case 5: {
                return itemstack.getItem() == GCItems.basicItem && itemstack.getItemDamage() == 19;
            }
            case 6: {
                return this.thermalArmorSlotValid(itemstack, 0);
            }
            case 7: {
                return this.thermalArmorSlotValid(itemstack, 1);
            }
            case 8: {
                return this.thermalArmorSlotValid(itemstack, 2);
            }
            case 9: {
                return this.thermalArmorSlotValid(itemstack, 3);
            }
            default: {
                return super.isItemValid(itemstack);
            }
        }
    }

    public boolean thermalArmorSlotValid(final ItemStack stack, final int slotIndex) {
        return stack.getItem() instanceof IItemThermal && ((IItemThermal)stack.getItem()).isValidForSlot(stack, slotIndex);
    }
}
