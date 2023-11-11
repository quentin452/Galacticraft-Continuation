package micdoodle8.mods.galacticraft.planets.mars.inventory;

import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.planets.mars.util.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.block.*;

public class ContainerSchematicCargoRocket extends Container
{
    public InventorySchematicCargoRocket craftMatrix;
    public IInventory craftResult;
    private final World worldObj;

    public ContainerSchematicCargoRocket(final InventoryPlayer par1InventoryPlayer, final int x, final int y, final int z) {
        this.craftMatrix = new InventorySchematicCargoRocket(this);
        this.craftResult = (IInventory)new InventoryCraftResult();
        int change = 27;
        this.worldObj = par1InventoryPlayer.player.worldObj;
        this.addSlotToContainer((Slot)new SlotRocketBenchResult(par1InventoryPlayer.player, (IInventory)this.craftMatrix, this.craftResult, 0, 142, 69 + change));
        this.addSlotToContainer((Slot)new SlotSchematicCargoRocket((IInventory)this.craftMatrix, 1, 48, -9 + change, x, y, z, par1InventoryPlayer.player));
        this.addSlotToContainer((Slot)new SlotSchematicCargoRocket((IInventory)this.craftMatrix, 2, 48, 9 + change, x, y, z, par1InventoryPlayer.player));
        for (int var6 = 0; var6 < 3; ++var6) {
            this.addSlotToContainer((Slot)new SlotSchematicCargoRocket((IInventory)this.craftMatrix, 3 + var6, 39, -7 + var6 * 18 + 16 + 18 + change, x, y, z, par1InventoryPlayer.player));
        }
        for (int var6 = 0; var6 < 3; ++var6) {
            this.addSlotToContainer((Slot)new SlotSchematicCargoRocket((IInventory)this.craftMatrix, 6 + var6, 57, -7 + var6 * 18 + 16 + 18 + change, x, y, z, par1InventoryPlayer.player));
        }
        this.addSlotToContainer((Slot)new SlotSchematicCargoRocket((IInventory)this.craftMatrix, 9, 21, 63 + change, x, y, z, par1InventoryPlayer.player));
        this.addSlotToContainer((Slot)new SlotSchematicCargoRocket((IInventory)this.craftMatrix, 10, 21, 81 + change, x, y, z, par1InventoryPlayer.player));
        this.addSlotToContainer((Slot)new SlotSchematicCargoRocket((IInventory)this.craftMatrix, 11, 48, 81 + change, x, y, z, par1InventoryPlayer.player));
        this.addSlotToContainer((Slot)new SlotSchematicCargoRocket((IInventory)this.craftMatrix, 12, 75, 63 + change, x, y, z, par1InventoryPlayer.player));
        this.addSlotToContainer((Slot)new SlotSchematicCargoRocket((IInventory)this.craftMatrix, 13, 75, 81 + change, x, y, z, par1InventoryPlayer.player));
        for (int var7 = 0; var7 < 3; ++var7) {
            this.addSlotToContainer((Slot)new SlotSchematicCargoRocket((IInventory)this.craftMatrix, 14 + var7, 93 + var7 * 26, -15 + change, x, y, z, par1InventoryPlayer.player));
        }
        change = 9;
        for (int var6 = 0; var6 < 3; ++var6) {
            for (int var8 = 0; var8 < 9; ++var8) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var8 + var6 * 9 + 9, 8 + var8 * 18, 129 + var6 * 18 + change));
            }
        }
        for (int var6 = 0; var6 < 9; ++var6) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var6, 8 + var6 * 18, 187 + change));
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
        this.craftResult.setInventorySlotContents(0, RecipeUtilMars.findMatchingCargoRocketRecipe(this.craftMatrix));
    }

    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return true;
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par1) {
        ItemStack var2 = null;
        final Slot var3 = (Slot) this.inventorySlots.get(par1);
        if (var3 != null && var3.getHasStack()) {
            final ItemStack var4 = var3.getStack();
            var2 = var4.copy();
            boolean done = false;
            if (par1 <= 16) {
                if (!this.mergeItemStack(var4, 17, 53, false)) {
                    return null;
                }
                if (par1 == 0) {
                    var3.onSlotChange(var4, var2);
                }
            }
            else {
                int i = 1;
                while (i < 14) {
                    final Slot testSlot = (Slot) this.inventorySlots.get(i);
                    if (!testSlot.getHasStack() && testSlot.isItemValid(var2)) {
                        if (!this.mergeOneItem(var4, i, i + 1, false)) {
                            return null;
                        }
                        done = true;
                        break;
                    }
                    else {
                        ++i;
                    }
                }
                if (!done) {
                    if (var2.getItem() == Item.getItemFromBlock((Block)Blocks.chest)) {
                        Slot slot14 = (Slot) this.inventorySlots.get(14);
                        if (slot14 != null && !slot14.getHasStack()) {
                            if (!this.mergeOneItem(var4, 14, 15, false)) {
                                return null;
                            }
                        } else {
                            Slot slot15 = (Slot) this.inventorySlots.get(15);
                            if (slot15 != null && !slot15.getHasStack()) {
                                if (!this.mergeOneItem(var4, 15, 16, false)) {
                                    return null;
                                }
                            } else {
                                Slot slot16 = (Slot) this.inventorySlots.get(16);
                                if (slot16 != null && !slot16.getHasStack()) {
                                    if (!this.mergeOneItem(var4, 16, 17, false)) {
                                        return null;
                                    }
                                }
                            }
                        }
                    }
                    else if (par1 >= 17 && par1 < 44) {
                        if (!this.mergeItemStack(var4, 44, 53, false)) {
                            return null;
                        }
                    }
                    else if (par1 >= 44 && par1 < 53) {
                        if (!this.mergeItemStack(var4, 17, 44, false)) {
                            return null;
                        }
                    }
                    else if (!this.mergeItemStack(var4, 17, 53, false)) {
                        return null;
                    }
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
