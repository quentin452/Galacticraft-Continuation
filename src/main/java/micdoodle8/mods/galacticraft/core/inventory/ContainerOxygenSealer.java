package micdoodle8.mods.galacticraft.core.inventory;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.entity.player.*;

public class ContainerOxygenSealer extends Container
{
    private TileBaseElectricBlock tileEntity;

    public ContainerOxygenSealer(final InventoryPlayer par1InventoryPlayer, final TileEntityOxygenSealer sealer) {
        this.tileEntity = sealer;
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)sealer, 0, 33, 27, new Class[] { IItemElectric.class }));
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)sealer, 1, 10, 27, new Class[] { IItemOxygenSupply.class }));
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)sealer, 2, 56, 27, new ItemStack[] { new ItemStack(GCItems.basicItem, 1, 20) }));
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

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par1) {
        ItemStack var2 = null;
        final Slot slot = (Slot)  this.inventorySlots.get(par1);
        final int b = this.inventorySlots.size();
        if (slot != null && slot.getHasStack()) {
            final ItemStack stack = slot.getStack();
            var2 = stack.copy();
            if (par1 <= 2) {
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
            else if (stack.getItem() == GCItems.basicItem && stack.getItemDamage() == 20) {
                if (!this.mergeItemStack(stack, 2, 3, false)) {
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
