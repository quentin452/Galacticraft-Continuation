package micdoodle8.mods.galacticraft.core.inventory;

import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;

public class ContainerOxygenStorageModule extends Container
{
    private TileEntityOxygenStorageModule tileEntity;

    public ContainerOxygenStorageModule(final InventoryPlayer par1InventoryPlayer, final TileEntityOxygenStorageModule storageModule) {
        this.tileEntity = storageModule;
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)storageModule, 0, 17, 22, new Class[] { IItemOxygenSupply.class }));
        for (int var3 = 0; var3 < 3; ++var3) {
            for (int var4 = 0; var4 < 9; ++var4) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
            }
        }
        for (int var3 = 0; var3 < 9; ++var3) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var3, 8 + var3 * 18, 142));
        }
        this.tileEntity.playersUsing.add(par1InventoryPlayer.player);
    }

    public void onContainerClosed(final EntityPlayer entityplayer) {
        super.onContainerClosed(entityplayer);
        this.tileEntity.playersUsing.remove(entityplayer);
    }

    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return true;
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par1) {
        ItemStack var2 = null;
        final Slot slot = (Slot)  this.inventorySlots.get(par1);
        final int b = this.inventorySlots.size();
        if (slot != null && slot.getHasStack()) {
            final ItemStack stack = slot.getStack();
            var2 = stack.copy();
            if (par1 < 1) {
                if (!this.mergeItemStack(stack, b - 36, b, true)) {
                    return null;
                }
            }
            else if (stack.getItem() instanceof IItemOxygenSupply) {
                if (!this.mergeItemStack(stack, 0, 1, false)) {
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
