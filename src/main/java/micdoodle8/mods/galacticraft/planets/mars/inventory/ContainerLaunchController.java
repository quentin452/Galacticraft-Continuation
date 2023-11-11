package micdoodle8.mods.galacticraft.planets.mars.inventory;

import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import micdoodle8.mods.galacticraft.api.item.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;

public class ContainerLaunchController extends Container
{
    private final TileEntityLaunchController tileEntity;

    public ContainerLaunchController(final InventoryPlayer par1InventoryPlayer, final TileEntityLaunchController tileEntity) {
        (this.tileEntity = tileEntity).checkDestFrequencyValid();
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)tileEntity, 0, 152, 105, new Class[] { IItemElectric.class }));
        for (int var6 = 0; var6 < 3; ++var6) {
            for (int var7 = 0; var7 < 9; ++var7) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 127 + var6 * 18));
            }
        }
        for (int var6 = 0; var6 < 9; ++var6) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var6, 8 + var6 * 18, 185));
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
        final Slot slot = (Slot)  this.inventorySlots.get(par1);
        final int b = this.inventorySlots.size();
        if (slot != null && slot.getHasStack()) {
            final ItemStack stack = slot.getStack();
            var2 = stack.copy();
            if (par1 == 0) {
                if (!this.mergeItemStack(stack, b - 36, b, true)) {
                    return null;
                }
            }
            else if (stack.getItem() instanceof IItemElectric) {
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
