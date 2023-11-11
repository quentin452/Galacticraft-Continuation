package micdoodle8.mods.galacticraft.core.inventory;

import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;

public class ContainerRocketInventory extends Container
{
    private final IInventory lowerChestInventory;
    private final IInventory spaceshipInv;
    private final IRocketType.EnumRocketType rocketType;

    public ContainerRocketInventory(final IInventory par1IInventory, final IInventory par2IInventory, final IRocketType.EnumRocketType rocketType) {
        this.lowerChestInventory = par1IInventory;
        this.spaceshipInv = par2IInventory;
        this.rocketType = rocketType;
        par2IInventory.openInventory();
        switch (rocketType.getInventorySpace() - 2) {
            case 0: {
                this.addSlotsNoInventory();
                break;
            }
            case 18: {
                this.addSlotsWithInventory(rocketType.getInventorySpace());
                break;
            }
            case 36: {
                this.addSlotsWithInventory(rocketType.getInventorySpace());
                break;
            }
            case 54: {
                this.addSlotsWithInventory(rocketType.getInventorySpace());
                break;
            }
        }
    }

    private void addSlotsNoInventory() {
        for (int var4 = 0; var4 < 3; ++var4) {
            for (int var5 = 0; var5 < 9; ++var5) {
                this.addSlotToContainer(new Slot(this.lowerChestInventory, var5 + (var4 + 1) * 9, 8 + var5 * 18, 84 + var4 * 18 - 34));
            }
        }
        for (int var4 = 0; var4 < 9; ++var4) {
            this.addSlotToContainer(new Slot(this.lowerChestInventory, var4, 8 + var4 * 18, 108));
        }
    }

    private void addSlotsWithInventory(final int slotCount) {
        final int lastRow = slotCount / 9;
        final int ySize = 145 + (this.rocketType.getInventorySpace() - 2) * 2;
        for (int var4 = 0; var4 < lastRow; ++var4) {
            for (int var5 = 0; var5 < 9; ++var5) {
                this.addSlotToContainer(new Slot(this.spaceshipInv, var5 + var4 * 9, 8 + var5 * 18, 50 + var4 * 18));
            }
        }
        for (int var4 = 0; var4 < 3; ++var4) {
            for (int var5 = 0; var5 < 9; ++var5) {
                this.addSlotToContainer(new Slot(this.lowerChestInventory, var5 + var4 * 9 + 9, 8 + var5 * 18, ySize - 82 + var4 * 18));
            }
        }
        for (int var4 = 0; var4 < 9; ++var4) {
            this.addSlotToContainer(new Slot(this.lowerChestInventory, var4, 8 + var4 * 18, ySize - 24));
        }
    }

    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return this.spaceshipInv.isUseableByPlayer(par1EntityPlayer);
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par2) {
        ItemStack var3 = null;
        final Slot var4 = (Slot)  this.inventorySlots.get(par2);
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
