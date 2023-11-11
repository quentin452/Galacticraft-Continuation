package micdoodle8.mods.galacticraft.core.inventory;

import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;

public class ContainerElectricFurnace extends Container
{
    private TileEntityElectricFurnace tileEntity;

    public ContainerElectricFurnace(final InventoryPlayer par1InventoryPlayer, final TileEntityElectricFurnace tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)tileEntity, 0, 8, 49, new Class[] { IItemElectric.class }));
        this.addSlotToContainer(new Slot((IInventory)tileEntity, 1, 56, 25));
        this.addSlotToContainer((Slot)new SlotFurnace(par1InventoryPlayer.player, (IInventory)tileEntity, 2, 109, 25));
        for (int var3 = 0; var3 < 3; ++var3) {
            for (int var4 = 0; var4 < 9; ++var4) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
            }
        }
        for (int var3 = 0; var3 < 9; ++var3) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var3, 8 + var3 * 18, 142));
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

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par1) {
        ItemStack var2 = null;
        final Slot var3 = (Slot) this.inventorySlots.get(par1);
        if (var3 != null && var3.getHasStack()) {
            final ItemStack var4 = var3.getStack();
            var2 = var4.copy();
            if (par1 == 2) {
                if (!this.mergeItemStack(var4, 3, 39, true)) {
                    return null;
                }
                var3.onSlotChange(var4, var2);
            }
            else if (par1 != 1 && par1 != 0) {
                if (var4.getItem() instanceof IItemElectric) {
                    if (!this.mergeItemStack(var4, 0, 1, false)) {
                        return null;
                    }
                }
                else if (FurnaceRecipes.smelting().getSmeltingResult(var4) != null) {
                    if (!this.mergeItemStack(var4, 1, 2, false)) {
                        return null;
                    }
                }
                else if (par1 >= 3 && par1 < 30) {
                    if (!this.mergeItemStack(var4, 30, 39, false)) {
                        return null;
                    }
                }
                else if (par1 >= 30 && par1 < 39 && !this.mergeItemStack(var4, 3, 30, false)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(var4, 3, 39, false)) {
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
