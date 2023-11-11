package micdoodle8.mods.galacticraft.planets.asteroids.inventory;

import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.planets.mars.util.*;

public class ContainerSchematicAstroMiner extends Container
{
    public InventorySchematicAstroMiner craftMatrix;
    public IInventory craftResult;
    private final World worldObj;

    public ContainerSchematicAstroMiner(final InventoryPlayer par1InventoryPlayer, final int x, final int y, final int z) {
        this.craftMatrix = new InventorySchematicAstroMiner(this);
        this.craftResult = (IInventory)new InventoryCraftResult();
        this.worldObj = par1InventoryPlayer.player.worldObj;
        this.addSlotToContainer((Slot)new SlotRocketBenchResult(par1InventoryPlayer.player, (IInventory)this.craftMatrix, this.craftResult, 0, 142, 98));
        int count = 1;
        for (int i = 0; i < 4; ++i) {
            this.addSlotToContainer((Slot)new SlotSchematicAstroMiner((IInventory)this.craftMatrix, count++, 27 + i * 18, 61, x, y, z, par1InventoryPlayer.player));
        }
        for (int i = 0; i < 5; ++i) {
            this.addSlotToContainer((Slot)new SlotSchematicAstroMiner((IInventory)this.craftMatrix, count++, 16 + i * 18, 79, x, y, z, par1InventoryPlayer.player));
        }
        for (int i = 0; i < 3; ++i) {
            this.addSlotToContainer((Slot)new SlotSchematicAstroMiner((IInventory)this.craftMatrix, count++, 44 + i * 18, 97, x, y, z, par1InventoryPlayer.player));
        }
        for (int i = 0; i < 2; ++i) {
            this.addSlotToContainer((Slot)new SlotSchematicAstroMiner((IInventory)this.craftMatrix, count++, 8 + i * 18, 103, x, y, z, par1InventoryPlayer.player));
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, i, 8 + i * 18, 198));
        }
        this.onCraftMatrixChanged((IInventory)this.craftMatrix);
    }

    public void onContainerClosed(final EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (!this.worldObj.isRemote) {
            for (int var2 = 1; var2 < this.craftMatrix.getSizeInventory(); ++var2) {
                final ItemStack var3 = this.craftMatrix.getStackInSlotOnClosing(var2);
                if (var3 != null) {
                    par1EntityPlayer.entityDropItem(var3, 0.0f);
                }
            }
        }
    }

    public void onCraftMatrixChanged(final IInventory par1IInventory) {
        this.craftResult.setInventorySlotContents(0, RecipeUtilMars.findMatchingAstroMinerRecipe(this.craftMatrix));
    }

    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return true;
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par1) {
        ItemStack var2 = null;
        final Slot var3 = (Slot)  this.inventorySlots.get(par1);
        if (var3 != null && var3.getHasStack()) {
            final ItemStack var4 = var3.getStack();
            var2 = var4.copy();
            final boolean done = false;
            if (par1 <= 14) {
                if (!this.mergeItemStack(var4, 15, 51, false)) {
                    return null;
                }
                var3.onSlotChange(var4, var2);
            }
            else {
                boolean valid = false;
                for (int i = 1; i < 15; ++i) {
                    final Slot testSlot = (Slot)  this.inventorySlots.get(i);
                    if (!testSlot.getHasStack() && testSlot.isItemValid(var2)) {
                        valid = true;
                        break;
                    }
                }
                if (valid) {
                    if (!this.mergeOneItemTestValid(var4, 1, 15, false)) {
                        return null;
                    }
                }
                else if (par1 >= 15 && par1 < 42) {
                    if (!this.mergeItemStack(var4, 42, 51, false)) {
                        return null;
                    }
                }
                else if (par1 >= 42 && par1 < 51) {
                    if (!this.mergeItemStack(var4, 15, 42, false)) {
                        return null;
                    }
                }
                else if (!this.mergeItemStack(var4, 15, 51, false)) {
                    return null;
                }
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

    protected boolean mergeOneItemTestValid(final ItemStack par1ItemStack, final int par2, final int par3, final boolean par4) {
        boolean flag1 = false;
        if (par1ItemStack.stackSize > 0) {
            for (int k = par2; k < par3; ++k) {
                final Slot slot = (Slot) this.inventorySlots.get(k);
                final ItemStack slotStack = slot.getStack();
                if (slotStack == null && slot.isItemValid(par1ItemStack)) {
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
