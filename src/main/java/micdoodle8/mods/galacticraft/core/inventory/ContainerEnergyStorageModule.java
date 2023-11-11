package micdoodle8.mods.galacticraft.core.inventory;

import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;

public class ContainerEnergyStorageModule extends Container
{
    private TileEntityEnergyStorageModule tileEntity;

    public ContainerEnergyStorageModule(final InventoryPlayer par1InventoryPlayer, final TileEntityEnergyStorageModule batteryBox) {
        this.tileEntity = batteryBox;
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)batteryBox, 0, 33, 24, new Class[] { IItemElectric.class }));
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)batteryBox, 1, 33, 48, new Class[] { IItemElectric.class }));
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
        return this.tileEntity.isUseableByPlayer(par1EntityPlayer);
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int slotID) {
        ItemStack returnStack = null;
        final Slot slot = (Slot) this.inventorySlots.get(slotID);
        final int b = this.inventorySlots.size();
        if (slot != null && slot.getHasStack()) {
            final ItemStack itemStack = slot.getStack();
            returnStack = itemStack.copy();
            if (slotID != 0 && slotID != 1) {
                if (itemStack.getItem() instanceof IItemElectric) {
                    if (((IItemElectric)itemStack.getItem()).getElectricityStored(itemStack) > 0.0f) {
                        if (!this.mergeItemStack(itemStack, 1, 2, false) && ((IItemElectric)itemStack.getItem()).getElectricityStored(itemStack) < ((IItemElectric)itemStack.getItem()).getMaxElectricityStored(itemStack) && !this.mergeItemStack(itemStack, 0, 1, false)) {
                            return null;
                        }
                    }
                    else if (!this.mergeItemStack(itemStack, 0, 1, false)) {
                        return null;
                    }
                }
                else if (slotID < b - 9) {
                    if (!this.mergeItemStack(itemStack, b - 9, b, false)) {
                        return null;
                    }
                }
                else if (!this.mergeItemStack(itemStack, b - 36, b - 9, false)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemStack, 2, 38, false)) {
                return null;
            }
            if (itemStack.stackSize == 0) {
                slot.putStack((ItemStack)null);
            }
            else {
                slot.onSlotChanged();
            }
            if (itemStack.stackSize == returnStack.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(par1EntityPlayer, itemStack);
        }
        return returnStack;
    }
}
