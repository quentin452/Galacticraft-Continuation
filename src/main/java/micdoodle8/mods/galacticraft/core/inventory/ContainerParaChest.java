package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;

public class ContainerParaChest extends Container
{
    private IInventory parachestInventory;
    public int numRows;

    public ContainerParaChest(final IInventory par1IInventory, final IInventory par2IInventory) {
        this.parachestInventory = par2IInventory;
        this.numRows = (par2IInventory.getSizeInventory() - 3) / 9;
        par2IInventory.openInventory();
        final int i = (this.numRows - 4) * 18 + 19;
        for (int j = 0; j < this.numRows; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlotToContainer(new Slot(par2IInventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }
        this.addSlotToContainer(new Slot(par2IInventory, par2IInventory.getSizeInventory() - 3, 125, ((this.numRows == 0) ? 24 : 26) + this.numRows * 18));
        this.addSlotToContainer(new Slot(par2IInventory, par2IInventory.getSizeInventory() - 2, 143, ((this.numRows == 0) ? 24 : 26) + this.numRows * 18));
        this.addSlotToContainer(new Slot(par2IInventory, par2IInventory.getSizeInventory() - 1, 75, ((this.numRows == 0) ? 24 : 26) + this.numRows * 18));
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlotToContainer(new Slot(par1IInventory, k + j * 9 + 9, 8 + k * 18, ((this.numRows == 0) ? 116 : 118) + j * 18 + i));
            }
        }
        for (int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(par1IInventory, j, 8 + j * 18, ((this.numRows == 0) ? 174 : 176) + i));
        }
    }

    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return this.parachestInventory.isUseableByPlayer(par1EntityPlayer);
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par2) {
        ItemStack itemstack = null;
        final Slot slot = (Slot)  this.inventorySlots.get(par2);
        final int b = this.inventorySlots.size();
        if (slot != null && slot.getHasStack()) {
            final ItemStack itemstack2 = slot.getStack();
            itemstack = itemstack2.copy();
            if (par2 < this.parachestInventory.getSizeInventory()) {
                if (!this.mergeItemStack(itemstack2, b - 36, b, true)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack2, 0, this.parachestInventory.getSizeInventory(), false)) {
                return null;
            }
            if (itemstack2.stackSize == 0) {
                slot.putStack((ItemStack)null);
            }
            else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

    public void onContainerClosed(final EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        this.parachestInventory.closeInventory();
    }

    public IInventory getparachestInventory() {
        return this.parachestInventory;
    }
}
