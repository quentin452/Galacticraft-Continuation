package micdoodle8.mods.galacticraft.planets.asteroids.inventory;

import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import micdoodle8.mods.galacticraft.api.item.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;

public class ContainerAstroMinerDock extends Container
{
    private TileEntityMinerBase tileEntity;

    public ContainerAstroMinerDock(final InventoryPlayer par1InventoryPlayer, final IInventory tile) {
        this.tileEntity = (TileEntityMinerBase)tile;
        this.addSlotToContainer((Slot)new SlotSpecific(tile, 0, 230, 108, new Class[] { IItemElectric.class }));
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 12; ++j) {
                this.addSlotToContainer(new Slot(tile, j + i * 12 + 1, 8 + j * 18, 18 + i * 18));
            }
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 139 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, i, 8 + i * 18, 197));
        }
    }

    public boolean canInteractWith(final EntityPlayer var1) {
        return this.tileEntity.isUseableByPlayer(var1);
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par2) {
        ItemStack var3 = null;
        final Slot slot = (Slot) this.inventorySlots.get(par2);
        final int b = 73;
        if (slot != null && slot.getHasStack()) {
            final ItemStack var4 = slot.getStack();
            var3 = var4.copy();
            if (par2 < b) {
                if (!this.mergeItemStack(var4, b, b + 36, true)) {
                    return null;
                }
            }
            else if (var4.getItem() instanceof IItemElectric) {
                if (!this.mergeItemStack(var4, 0, 1, false)) {
                    return null;
                }
            }
            else if (par2 < b + 27) {
                if (!this.mergeItemStack(var4, 1, b, false) && !this.mergeItemStack(var4, b + 27, b + 36, false)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(var4, 1, b, false) && !this.mergeItemStack(var4, b, b + 27, false)) {
                return null;
            }
            if (var4.stackSize == 0) {
                slot.putStack((ItemStack)null);
            }
            else {
                slot.onSlotChanged();
            }
            if (var4.stackSize == var3.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(par1EntityPlayer, var4);
        }
        return var3;
    }
}
