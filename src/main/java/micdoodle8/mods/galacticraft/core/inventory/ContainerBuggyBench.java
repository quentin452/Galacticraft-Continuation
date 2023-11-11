package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.world.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.item.*;

public class ContainerBuggyBench extends Container
{
    public InventoryBuggyBench craftMatrix;
    public IInventory craftResult;
    private final World worldObj;

    public ContainerBuggyBench(final InventoryPlayer par1InventoryPlayer, final int x, final int y, final int z) {
        this.craftMatrix = new InventoryBuggyBench(this);
        this.craftResult = (IInventory)new InventoryCraftResult();
        final int change = 27;
        this.worldObj = par1InventoryPlayer.player.worldObj;
        this.addSlotToContainer(new SlotRocketBenchResult(par1InventoryPlayer.player, (IInventory)this.craftMatrix, this.craftResult, 0, 142, 106));
        for (int var6 = 0; var6 < 4; ++var6) {
            for (int var7 = 0; var7 < 3; ++var7) {
                this.addSlotToContainer(new SlotBuggyBench((IInventory)this.craftMatrix, var7 * 4 + var6 + 1, 39 + var7 * 18, 14 + var6 * 18 + 27, x, y, z, par1InventoryPlayer.player));
            }
        }
        for (int var6 = 0; var6 < 2; ++var6) {
            for (int var7 = 0; var7 < 2; ++var7) {
                this.addSlotToContainer(new SlotBuggyBench((IInventory)this.craftMatrix, var7 * 2 + var6 + 13, 21 + var7 * 72, 14 + var6 * 54 + 27, x, y, z, par1InventoryPlayer.player));
            }
        }
        for (int var8 = 0; var8 < 3; ++var8) {
            this.addSlotToContainer(new SlotBuggyBench((IInventory)this.craftMatrix, 17 + var8, 93 + var8 * 26, 12, x, y, z, par1InventoryPlayer.player));
        }
        for (int var6 = 0; var6 < 3; ++var6) {
            for (int var7 = 0; var7 < 9; ++var7) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 111 + var6 * 18 + 27));
            }
        }
        for (int var6 = 0; var6 < 9; ++var6) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var6, 8 + var6 * 18, 196));
        }
        this.onCraftMatrixChanged((IInventory)this.craftMatrix);
    }

    public void onContainerClosed(final EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (!this.worldObj.isRemote) {
            for (int var2 = 1; var2 < this.craftMatrix.getSizeInventory(); ++var2) {
                final ItemStack slot = this.craftMatrix.getStackInSlotOnClosing(var2);
                if (slot != null) {
                    par1EntityPlayer.entityDropItem(slot, 0.0f);
                }
            }
        }
    }

    public void onCraftMatrixChanged(final IInventory par1IInventory) {
        this.craftResult.setInventorySlotContents(0, RecipeUtil.findMatchingBuggy(this.craftMatrix));
    }

    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return true;
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par1) {
        ItemStack var2 = null;
        final Slot slot = (Slot) this.inventorySlots.get(par1);
        final int b = this.inventorySlots.size();
        if (slot != null && slot.getHasStack()) {
            final ItemStack var3 = slot.getStack();
            var2 = var3.copy();
            if (par1 < b - 36) {
                if (!this.mergeItemStack(var3, b - 36, b, true)) {
                    return null;
                }
                if (par1 == 0) {
                    slot.onSlotChange(var3, var2);
                }
            }
            else {
                final Item i = var3.getItem();
                if (i == GCItems.heavyPlatingTier1 || i == GCItems.partBuggy) {
                    for (int j = 1; j < 20; ++j) {
                        if (slot.isItemValid(var3)) {
                            this.mergeOneItem(var3, j, j + 1, false);
                        }
                    }
                }
                else if (par1 < b - 9) {
                    if (!this.mergeItemStack(var3, b - 9, b, false)) {
                        return null;
                    }
                }
                else if (!this.mergeItemStack(var3, b - 36, b - 9, false)) {
                    return null;
                }
            }
            if (var3.stackSize == 0) {
                slot.putStack((ItemStack)null);
            }
            if (var3.stackSize == var2.stackSize) {
                return null;
            }
            slot.onSlotChanged();
            slot.onPickupFromSlot(par1EntityPlayer, var3);
        }
        return var2;
    }

    protected boolean mergeOneItem(final ItemStack par1ItemStack, final int par2, final int par3, final boolean par4) {
        boolean flag1 = false;
        if (par1ItemStack.stackSize > 0) {
            for (int k = par2; k < par3; ++k) {
                final Slot slot = (Slot) this.inventorySlots.get(k);
                final ItemStack slotStack = slot.getStack();
                if (slotStack == null) {
                    final ItemStack stackOneItem = par1ItemStack.copy();
                    stackOneItem.stackSize = 1;
                    --par1ItemStack.stackSize;
                    slot.putStack(stackOneItem);
                    slot.onSlotChanged();
                    flag1 = true;
                    break;
                }
            }
        }
        return flag1;
    }
}
