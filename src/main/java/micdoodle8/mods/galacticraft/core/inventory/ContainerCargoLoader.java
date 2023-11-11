package micdoodle8.mods.galacticraft.core.inventory;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;

public class ContainerCargoLoader extends Container
{
    private TileBaseElectricBlock tileEntity;

    public ContainerCargoLoader(final InventoryPlayer par1InventoryPlayer, final IInventory cargoLoader) {
        this.tileEntity = (TileBaseElectricBlock)cargoLoader;
        this.addSlotToContainer((Slot)new SlotSpecific(cargoLoader, 0, 10, 27, new Class[] { IItemElectric.class }));
        for (int var6 = 0; var6 < 2; ++var6) {
            for (int var7 = 0; var7 < 7; ++var7) {
                this.addSlotToContainer(new Slot(cargoLoader, var7 + var6 * 7 + 1, 38 + var7 * 18, 27 + var6 * 18));
            }
        }
        for (int var6 = 0; var6 < 3; ++var6) {
            for (int var7 = 0; var7 < 9; ++var7) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 124 + var6 * 18));
            }
        }
        for (int var6 = 0; var6 < 9; ++var6) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var6, 8 + var6 * 18, 182));
        }
    }

    public boolean canInteractWith(final EntityPlayer var1) {
        return this.tileEntity.isUseableByPlayer(var1);
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par2) {
        ItemStack var3 = null;
        final Slot slot = (Slot) this.inventorySlots.get(par2);
        if (slot != null && slot.getHasStack()) {
            final ItemStack var4 = slot.getStack();
            var3 = var4.copy();
            if (par2 < 15) {
                if (!this.mergeItemStack(var4, 15, 51, true)) {
                    return null;
                }
            }
            else if (var4.getItem() instanceof IItemElectric) {
                if (!this.mergeItemStack(var4, 0, 1, false)) {
                    return null;
                }
            }
            else if (par2 < 42) {
                if (!this.mergeItemStack(var4, 1, 15, false) && !this.mergeItemStack(var4, 42, 51, false)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(var4, 1, 15, false) && !this.mergeItemStack(var4, 15, 42, false)) {
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
