package micdoodle8.mods.galacticraft.core.inventory;

import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;

public class ContainerElectricIngotCompressor extends Container
{
    private TileEntityElectricIngotCompressor tileEntity;

    public ContainerElectricIngotCompressor(final InventoryPlayer par1InventoryPlayer, final TileEntityElectricIngotCompressor tileEntity) {
        this.tileEntity = tileEntity;
        tileEntity.compressingCraftMatrix.eventHandler = this;
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                this.addSlotToContainer(new Slot((IInventory)tileEntity.compressingCraftMatrix, y + x * 3, 19 + y * 18, 18 + x * 18));
            }
        }
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)tileEntity, 0, 55, 75, new Class[] { IItemElectric.class }));
        this.addSlotToContainer((Slot)new SlotFurnace(par1InventoryPlayer.player, (IInventory)tileEntity, 1, 138, 30));
        this.addSlotToContainer((Slot)new SlotFurnace(par1InventoryPlayer.player, (IInventory)tileEntity, 2, 138, 48));
        for (int var3 = 0; var3 < 3; ++var3) {
            for (int var4 = 0; var4 < 9; ++var4) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 117 + var3 * 18));
            }
        }
        for (int var3 = 0; var3 < 9; ++var3) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var3, 8 + var3 * 18, 175));
        }
    }

    public void onContainerClosed(final EntityPlayer entityplayer) {
        super.onContainerClosed(entityplayer);
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
        final Slot var3 = (Slot) this.inventorySlots.get(par1);
        if (var3 != null && var3.getHasStack()) {
            final ItemStack var4 = var3.getStack();
            var2 = var4.copy();
            if (par1 <= 11) {
                if (!this.mergeItemStack(var4, 12, 48, true)) {
                    return null;
                }
                if (par1 == 1 || par1 == 2) {
                    var3.onSlotChange(var4, var2);
                }
            }
            else if (var4.getItem() instanceof IItemElectric) {
                if (!this.mergeItemStack(var4, 9, 10, false)) {
                    return null;
                }
            }
            else if (par1 < 39) {
                if (!this.mergeItemStack(var4, 0, 9, false) && !this.mergeItemStack(var4, 39, 48, false)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(var4, 0, 9, false) && !this.mergeItemStack(var4, 12, 39, false)) {
                return null;
            }
            if (var4.stackSize == 0) {
                var3.putStack((ItemStack)null);
            }
            else {
                var3.onSlotChanged();
            }
            if (var4.stackSize == var2.stackSize) {
                return null;
            }
            var3.onPickupFromSlot(par1EntityPlayer, var4);
        }
        return var2;
    }
}
