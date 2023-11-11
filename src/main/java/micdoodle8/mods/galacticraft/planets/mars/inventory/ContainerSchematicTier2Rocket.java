package micdoodle8.mods.galacticraft.planets.mars.inventory;

import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.planets.mars.util.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.block.*;

public class ContainerSchematicTier2Rocket extends Container
{
    public InventorySchematicTier2Rocket craftMatrix;
    public IInventory craftResult;
    private final World worldObj;

    public ContainerSchematicTier2Rocket(final InventoryPlayer par1InventoryPlayer, final int x, final int y, final int z) {
        this.craftMatrix = new InventorySchematicTier2Rocket(this);
        this.craftResult = (IInventory)new InventoryCraftResult();
        final int change = 27;
        this.worldObj = par1InventoryPlayer.player.worldObj;
        this.addSlotToContainer((Slot)new SlotRocketBenchResult(par1InventoryPlayer.player, (IInventory)this.craftMatrix, this.craftResult, 0, 142, 114));
        this.addSlotToContainer((Slot)new SlotSchematicTier2Rocket((IInventory)this.craftMatrix, 1, 48, 19, x, y, z, par1InventoryPlayer.player));
        for (int var6 = 0; var6 < 5; ++var6) {
            this.addSlotToContainer((Slot)new SlotSchematicTier2Rocket((IInventory)this.craftMatrix, 2 + var6, 39, -6 + var6 * 18 + 16 + 27, x, y, z, par1InventoryPlayer.player));
        }
        for (int var6 = 0; var6 < 5; ++var6) {
            this.addSlotToContainer((Slot)new SlotSchematicTier2Rocket((IInventory)this.craftMatrix, 7 + var6, 57, -6 + var6 * 18 + 16 + 27, x, y, z, par1InventoryPlayer.player));
        }
        this.addSlotToContainer((Slot)new SlotSchematicTier2Rocket((IInventory)this.craftMatrix, 12, 21, 91, x, y, z, par1InventoryPlayer.player));
        this.addSlotToContainer((Slot)new SlotSchematicTier2Rocket((IInventory)this.craftMatrix, 13, 21, 109, x, y, z, par1InventoryPlayer.player));
        this.addSlotToContainer((Slot)new SlotSchematicTier2Rocket((IInventory)this.craftMatrix, 14, 21, 127, x, y, z, par1InventoryPlayer.player));
        this.addSlotToContainer((Slot)new SlotSchematicTier2Rocket((IInventory)this.craftMatrix, 15, 48, 127, x, y, z, par1InventoryPlayer.player));
        this.addSlotToContainer((Slot)new SlotSchematicTier2Rocket((IInventory)this.craftMatrix, 16, 75, 91, x, y, z, par1InventoryPlayer.player));
        this.addSlotToContainer((Slot)new SlotSchematicTier2Rocket((IInventory)this.craftMatrix, 17, 75, 109, x, y, z, par1InventoryPlayer.player));
        this.addSlotToContainer((Slot)new SlotSchematicTier2Rocket((IInventory)this.craftMatrix, 18, 75, 127, x, y, z, par1InventoryPlayer.player));
        for (int var7 = 0; var7 < 3; ++var7) {
            this.addSlotToContainer((Slot)new SlotSchematicTier2Rocket((IInventory)this.craftMatrix, 19 + var7, 93 + var7 * 26, 12, x, y, z, par1InventoryPlayer.player));
        }
        for (int var6 = 0; var6 < 3; ++var6) {
            for (int var8 = 0; var8 < 9; ++var8) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var8 + var6 * 9 + 9, 8 + var8 * 18, 129 + var6 * 18 + 27));
            }
        }
        for (int var6 = 0; var6 < 9; ++var6) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var6, 8 + var6 * 18, 214));
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
        this.craftResult.setInventorySlotContents(0, RecipeUtilMars.findMatchingSpaceshipT2Recipe(this.craftMatrix));
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
            if (par1 <= 21) {
                if (!this.mergeItemStack(var4, 22, 58, false)) {
                    return null;
                }
                if (par1 == 0) {
                    var3.onSlotChange(var4, var2);
                }
            }
            else {
                int i = 1;
                while (i < 19) {
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
                    Slot slot = (Slot) this.inventorySlots.get(19);
                    if (slot != null && !slot.getHasStack()) {
                        if (!this.mergeOneItem(var4, 19, 20, false)) {
                            return null;
                        }
                    } else {
                        Slot slot1 = (Slot) this.inventorySlots.get(20);
                        if (slot1 != null && !slot1.getHasStack()) {
                            if (!this.mergeOneItem(var4, 20, 21, false)) {
                                return null;
                            }
                        } else {
                            Slot slot2 = (Slot) this.inventorySlots.get(21);
                            if (slot2 != null && !slot2.getHasStack()) {
                                if (!this.mergeOneItem(var4, 21, 22, false)) {
                                    return null;
                                }
                            } else {
                                if (par1 >= 22 && par1 < 49) {
                                    if (!this.mergeItemStack(var4, 49, 58, false)) {
                                        return null;
                                    }
                                } else if (par1 >= 49 && par1 < 58) {
                                    if (!this.mergeItemStack(var4, 22, 49, false)) {
                                        return null;
                                    }
                                } else {
                                    if (!this.mergeItemStack(var4, 22, 58, false)) {
                                        return null;
                                    }
                                }
                            }
                        }
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
