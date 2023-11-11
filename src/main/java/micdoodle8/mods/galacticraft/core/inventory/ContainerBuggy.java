package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;

public class ContainerBuggy extends Container
{
    private final IInventory lowerChestInventory;
    private final IInventory spaceshipInv;

    public ContainerBuggy(final IInventory par1IInventory, final IInventory par2IInventory, final int type) {
        this.lowerChestInventory = par1IInventory;
        (this.spaceshipInv = par2IInventory).openInventory();
        if (type != 0) {
            for (int var4 = 0; var4 < type * 2; ++var4) {
                for (int var5 = 0; var5 < 9; ++var5) {
                    this.addSlotToContainer(new Slot(this.spaceshipInv, var5 + var4 * 9, 8 + var5 * 18, 50 + var4 * 18));
                }
            }
        }
        for (int var4 = 0; var4 < 3; ++var4) {
            for (int var5 = 0; var5 < 9; ++var5) {
                this.addSlotToContainer(new Slot(this.lowerChestInventory, var5 + var4 * 9 + 9, 8 + var5 * 18, 49 + var4 * 18 + 14 + type * 36));
            }
        }
        for (int var4 = 0; var4 < 9; ++var4) {
            this.addSlotToContainer(new Slot(this.lowerChestInventory, var4, 8 + var4 * 18, 121 + type * 36));
        }
    }

    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return this.spaceshipInv.isUseableByPlayer(par1EntityPlayer);
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par2) {
        ItemStack var3 = null;
        final Slot var4 = (Slot) this.inventorySlots.get(par2);
        final int b = this.inventorySlots.size() - 36;
        if (var4 != null && var4.getHasStack()) {
            final ItemStack var5 = var4.getStack();
            var3 = var5.copy();
            if (par2 < b) {
                if (!this.mergeItemStack(var5, b, b + 36, true)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(var5, 0, b, false)) {
                return null;
            }
            if (var5.stackSize == 0) {
                var4.putStack((ItemStack)null);
            }
            else {
                var4.onSlotChanged();
            }
        }
        return var3;
    }

    public void onContainerClosed(final EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        this.lowerChestInventory.closeInventory();
    }

    public IInventory getLowerChestInventory() {
        return this.lowerChestInventory;
    }
}
