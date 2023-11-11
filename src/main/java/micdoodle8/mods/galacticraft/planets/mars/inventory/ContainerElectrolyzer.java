package micdoodle8.mods.galacticraft.planets.mars.inventory;

import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import micdoodle8.mods.galacticraft.api.item.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class ContainerElectrolyzer extends Container
{
    private final TileEntityElectrolyzer tileEntity;

    public ContainerElectrolyzer(final InventoryPlayer par1InventoryPlayer, final TileEntityElectrolyzer tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)tileEntity, 0, 34, 50, new Class[] { IItemElectric.class }));
        this.addSlotToContainer(new Slot((IInventory)tileEntity, 1, 7, 7));
        this.addSlotToContainer(new Slot((IInventory)tileEntity, 2, 132, 7));
        this.addSlotToContainer(new Slot((IInventory)tileEntity, 3, 153, 7));
        for (int var3 = 0; var3 < 3; ++var3) {
            for (int var4 = 0; var4 < 9; ++var4) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 104 + var3 * 18 - 18));
            }
        }
        for (int var3 = 0; var3 < 9; ++var3) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var3, 8 + var3 * 18, 144));
        }
        tileEntity.openInventory();
    }

    public void onContainerClosed(final EntityPlayer entityplayer) {
        super.onContainerClosed(entityplayer);
        this.tileEntity.closeInventory();
    }

    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return this.tileEntity.isUseableByPlayer(par1EntityPlayer);
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par1) {
        ItemStack var2 = null;
        final Slot slot = (Slot) this.inventorySlots.get(par1);
        if (slot != null && slot.getHasStack()) {
            final ItemStack var3 = slot.getStack();
            var2 = var3.copy();
            if (par1 < 4) {
                if (!this.mergeItemStack(var3, 4, 40, true)) {
                    return null;
                }
                if (par1 == 2) {
                    slot.onSlotChange(var3, var2);
                }
            }
            else if (var3.getItem() instanceof IItemElectric) {
                if (!this.mergeItemStack(var3, 0, 1, false)) {
                    return null;
                }
            }
            else if (FluidUtil.isWaterContainer(var3)) {
                if (!this.mergeItemStack(var3, 1, 2, false)) {
                    return null;
                }
            }
            else if (FluidUtil.isEmptyGasContainer(var3)) {
                if (!this.mergeItemStack(var3, 2, 4, false)) {
                    return null;
                }
            }
            else if (par1 < 31) {
                if (!this.mergeItemStack(var3, 31, 40, false)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(var3, 4, 31, false)) {
                return null;
            }
            if (var3.stackSize == 0) {
                slot.putStack((ItemStack)null);
            }
            else {
                slot.onSlotChanged();
            }
            if (var3.stackSize == var2.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(par1EntityPlayer, var3);
        }
        return var2;
    }
}
