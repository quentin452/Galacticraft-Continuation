package micdoodle8.mods.galacticraft.core.inventory;

import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;

public class ContainerIngotCompressor extends Container
{
    private TileEntityIngotCompressor tileEntity;

    public ContainerIngotCompressor(final InventoryPlayer par1InventoryPlayer, final TileEntityIngotCompressor tileEntity) {
        this.tileEntity = tileEntity;
        tileEntity.compressingCraftMatrix.eventHandler = this;
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                this.addSlotToContainer(new Slot((IInventory)tileEntity.compressingCraftMatrix, y + x * 3, 19 + y * 18, 18 + x * 18));
            }
        }
        this.addSlotToContainer(new Slot((IInventory)tileEntity, 0, 55, 75));
        this.addSlotToContainer((Slot)new SlotFurnace(par1InventoryPlayer.player, (IInventory)tileEntity, 1, 138, 38));
        for (int var3 = 0; var3 < 3; ++var3) {
            for (int var4 = 0; var4 < 9; ++var4) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 110 + var3 * 18));
            }
        }
        for (int var3 = 0; var3 < 9; ++var3) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var3, 8 + var3 * 18, 168));
        }
        tileEntity.playersUsing.add(par1InventoryPlayer.player);
    }

    public void onContainerClosed(final EntityPlayer entityplayer) {
        super.onContainerClosed(entityplayer);
        this.tileEntity.playersUsing.remove(entityplayer);
    }

    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return this.tileEntity.isUseableByPlayer(par1EntityPlayer);
    }

    public void onCraftMatrixChanged(final IInventory par1IInventory) {
        super.onCraftMatrixChanged(par1IInventory);
        this.tileEntity.updateInput();
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par1) {
        ItemStack var2 = null;
        final Slot slot = (Slot) this.inventorySlots.get(par1);
        if (slot != null && slot.getHasStack()) {
            final ItemStack var3 = slot.getStack();
            var2 = var3.copy();
            if (par1 <= 10) {
                if (!this.mergeItemStack(var3, 11, 47, true)) {
                    return null;
                }
                if (par1 == 1) {
                    slot.onSlotChange(var3, var2);
                }
            }
            else if (TileEntityFurnace.getItemBurnTime(var3) > 0) {
                if (!this.mergeItemStack(var3, 9, 10, false)) {
                    return null;
                }
            }
            else if (par1 < 38) {
                if (!this.mergeItemStack(var3, 0, 9, false) && !this.mergeItemStack(var3, 38, 47, false)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(var3, 0, 9, false) && !this.mergeItemStack(var3, 11, 38, false)) {
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

    public boolean canDragIntoSlot(final Slot par1Slot) {
        return par1Slot.slotNumber < 9;
    }
}
