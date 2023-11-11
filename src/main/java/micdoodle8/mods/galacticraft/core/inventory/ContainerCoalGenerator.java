package micdoodle8.mods.galacticraft.core.inventory;

import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;

public class ContainerCoalGenerator extends Container
{
    private TileEntityCoalGenerator tileEntity;

    public ContainerCoalGenerator(final InventoryPlayer par1InventoryPlayer, final TileEntityCoalGenerator tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)tileEntity, 0, 33, 34, new ItemStack[] { new ItemStack(Items.coal), new ItemStack(Item.getItemFromBlock(Blocks.coal_block)) }));
        for (int var3 = 0; var3 < 3; ++var3) {
            for (int var4 = 0; var4 < 9; ++var4) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
            }
        }
        for (int var3 = 0; var3 < 9; ++var3) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var3, 8 + var3 * 18, 142));
        }
    }

    public void onContainerClosed(final EntityPlayer entityplayer) {
        super.onContainerClosed(entityplayer);
    }

    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return this.tileEntity.isUseableByPlayer(par1EntityPlayer);
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par1) {
        ItemStack var2 = null;
        final Slot var3 = (Slot) this.inventorySlots.get(par1);
        if (var3 != null && var3.getHasStack()) {
            final ItemStack var4 = var3.getStack();
            var2 = var4.copy();
            if (par1 != 0) {
                if (var4.getItem() == Items.coal) {
                    if (!this.mergeItemStack(var4, 0, 1, false)) {
                        return null;
                    }
                }
                else if (par1 >= 28) {
                    if (!this.mergeItemStack(var4, 1, 28, false)) {
                        return null;
                    }
                }
                else if (!this.mergeItemStack(var4, 28, 37, false)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(var4, 1, 37, false)) {
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
