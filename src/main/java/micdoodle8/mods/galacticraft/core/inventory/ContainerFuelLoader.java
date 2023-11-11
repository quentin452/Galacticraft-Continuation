package micdoodle8.mods.galacticraft.core.inventory;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class ContainerFuelLoader extends Container
{
    private TileBaseElectricBlock tileEntity;

    public ContainerFuelLoader(final InventoryPlayer par1InventoryPlayer, final TileEntityFuelLoader fuelLoader) {
        this.tileEntity = (TileBaseElectricBlock)fuelLoader;
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)fuelLoader, 0, 51, 55, new Class[] { IItemElectric.class }));
        this.addSlotToContainer(new Slot((IInventory)fuelLoader, 1, 7, 12));
        for (int var6 = 0; var6 < 3; ++var6) {
            for (int var7 = 0; var7 < 9; ++var7) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 89 + var6 * 18));
            }
        }
        for (int var6 = 0; var6 < 9; ++var6) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var6, 8 + var6 * 18, 147));
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
            if (par2 < 2) {
                if (!this.mergeItemStack(var4, 2, 38, true)) {
                    return null;
                }
            }
            else if (var4.getItem() instanceof IItemElectric) {
                if (!this.mergeItemStack(var4, 0, 1, false)) {
                    return null;
                }
            }
            else if (FluidUtil.isFuelContainerAny(var4)) {
                if (!this.mergeItemStack(var4, 1, 2, false)) {
                    return null;
                }
            }
            else if (par2 < 29) {
                if (!this.mergeItemStack(var4, 29, 38, false)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(var4, 2, 29, false)) {
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
