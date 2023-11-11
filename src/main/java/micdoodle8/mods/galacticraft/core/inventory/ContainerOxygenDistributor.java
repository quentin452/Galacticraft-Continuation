package micdoodle8.mods.galacticraft.core.inventory;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;

public class ContainerOxygenDistributor extends Container
{
    private TileBaseElectricBlock tileEntity;

    public ContainerOxygenDistributor(final InventoryPlayer par1InventoryPlayer, final TileEntityOxygenDistributor distributor) {
        this.tileEntity = distributor;
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)distributor, 0, 47, 27, new Class[] { IItemElectric.class }));
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)distributor, 1, 17, 27, new Class[] { IItemOxygenSupply.class }));
        for (int var6 = 0; var6 < 3; ++var6) {
            for (int var7 = 0; var7 < 9; ++var7) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 104 + var6 * 18));
            }
        }
        for (int var6 = 0; var6 < 9; ++var6) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var6, 8 + var6 * 18, 162));
        }
    }

    public boolean canInteractWith(final EntityPlayer var1) {
        return this.tileEntity.isUseableByPlayer(var1);
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par1) {
        ItemStack var2 = null;
        final Slot slot = (Slot)  this.inventorySlots.get(par1);
        final int b = this.inventorySlots.size();
        if (slot != null && slot.getHasStack()) {
            final ItemStack stack = slot.getStack();
            var2 = stack.copy();
            if (par1 < 2) {
                if (!this.mergeItemStack(stack, b - 36, b, true)) {
                    return null;
                }
            }
            else if (stack.getItem() instanceof IItemElectric) {
                if (!this.mergeItemStack(stack, 0, 1, false)) {
                    return null;
                }
            }
            else if (stack.getItem() instanceof IItemOxygenSupply) {
                if (!this.mergeItemStack(stack, 1, 2, false)) {
                    return null;
                }
            }
            else if (par1 < b - 9) {
                if (!this.mergeItemStack(stack, b - 9, b, false)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(stack, b - 36, b - 9, false)) {
                return null;
            }
            if (stack.stackSize == 0) {
                slot.putStack((ItemStack)null);
            }
            else {
                slot.onSlotChanged();
            }
            if (stack.stackSize == var2.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(par1EntityPlayer, stack);
        }
        return var2;
    }
}
