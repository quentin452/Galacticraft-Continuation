package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.world.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.block.*;

public class ContainerSchematicTier1Rocket extends Container
{
    public InventoryRocketBench craftMatrix;
    public IInventory craftResult;
    private final World worldObj;

    public ContainerSchematicTier1Rocket(final InventoryPlayer par1InventoryPlayer, final int x, final int y, final int z) {
        this.craftMatrix = new InventoryRocketBench(this);
        this.craftResult = (IInventory)new InventoryCraftResult();
        final int change = 27;
        this.worldObj = par1InventoryPlayer.player.worldObj;
        this.addSlotToContainer((Slot)new SlotRocketBenchResult(par1InventoryPlayer.player, (IInventory)this.craftMatrix, this.craftResult, 0, 142, 96));
        this.addSlotToContainer((Slot)new SlotRocketBench((IInventory)this.craftMatrix, 1, 48, 19, x, y, z, par1InventoryPlayer.player));
        for (int var6 = 0; var6 < 4; ++var6) {
            this.addSlotToContainer((Slot)new SlotRocketBench((IInventory)this.craftMatrix, 2 + var6, 39, -6 + var6 * 18 + 16 + 27, x, y, z, par1InventoryPlayer.player));
        }
        for (int var6 = 0; var6 < 4; ++var6) {
            this.addSlotToContainer((Slot)new SlotRocketBench((IInventory)this.craftMatrix, 6 + var6, 57, -6 + var6 * 18 + 16 + 27, x, y, z, par1InventoryPlayer.player));
        }
        this.addSlotToContainer((Slot)new SlotRocketBench((IInventory)this.craftMatrix, 10, 21, 91, x, y, z, par1InventoryPlayer.player));
        this.addSlotToContainer((Slot)new SlotRocketBench((IInventory)this.craftMatrix, 11, 21, 109, x, y, z, par1InventoryPlayer.player));
        this.addSlotToContainer((Slot)new SlotRocketBench((IInventory)this.craftMatrix, 12, 48, 109, x, y, z, par1InventoryPlayer.player));
        this.addSlotToContainer((Slot)new SlotRocketBench((IInventory)this.craftMatrix, 13, 75, 91, x, y, z, par1InventoryPlayer.player));
        this.addSlotToContainer((Slot)new SlotRocketBench((IInventory)this.craftMatrix, 14, 75, 109, x, y, z, par1InventoryPlayer.player));
        for (int var7 = 0; var7 < 3; ++var7) {
            this.addSlotToContainer((Slot)new SlotRocketBench((IInventory)this.craftMatrix, 15 + var7, 93 + var7 * 26, 12, x, y, z, par1InventoryPlayer.player));
        }
        for (int var6 = 0; var6 < 3; ++var6) {
            for (int var8 = 0; var8 < 9; ++var8) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var8 + var6 * 9 + 9, 8 + var8 * 18, 111 + var6 * 18 + 27));
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
            for (int var2 = 1; var2 < 18; ++var2) {
                final ItemStack var3 = this.craftMatrix.getStackInSlotOnClosing(var2);
                if (var3 != null) {
                    par1EntityPlayer.entityDropItem(var3, 0.0f);
                }
            }
        }
    }

    public void onCraftMatrixChanged(final IInventory par1IInventory) {
        this.craftResult.setInventorySlotContents(0, RecipeUtil.findMatchingSpaceshipRecipe(this.craftMatrix));
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
            if (par1 <= 17) {
                if (!this.mergeItemStack(var4, 18, 54, false)) {
                    return null;
                }
                if (par1 == 0) {
                    var3.onSlotChange(var4, var2);
                }
            }
            else if (var2.getItem() == GCItems.partNoseCone) {
                if (!this.mergeOneItem(var4, 1, 2, false)) {
                    return null;
                }
            }
            else if (var2.getItem() == GCItems.heavyPlatingTier1) {
                if (!this.mergeOneItem(var4, 2, 10, false)) {
                    return null;
                }
            }
            else if (var2.getItem() == GCItems.partFins) {
                if (!this.mergeOneItem(var4, 10, 12, false) && !this.mergeOneItem(var4, 13, 15, false)) {
                    return null;
                }
            }
            else if (var2.getItem() == GCItems.rocketEngine) {
                if (!this.mergeOneItem(var4, 12, 13, false)) {
                    return null;
                }
            }
            else if (var2.getItem() == Item.getItemFromBlock((Block)Blocks.chest)) {
                if (!this.mergeOneItem(var4, 15, 18, false)) {
                    return null;
                }
            }
            else if (par1 >= 18 && par1 < 45) {
                if (!this.mergeItemStack(var4, 45, 54, false)) {
                    return null;
                }
            }
            else if (par1 >= 45 && par1 < 54 && !this.mergeItemStack(var4, 18, 45, false)) {
                return null;
            }
            if (var4.stackSize == 0) {
                if (par1 == 0) {
                    var3.onPickupFromSlot(par1EntityPlayer, var4);
                }
                var3.putStack((ItemStack)null);
                return var2;
            }
            if (var4.stackSize == var2.stackSize) {
                return null;
            }
            var3.onPickupFromSlot(par1EntityPlayer, var4);
            if (par1 == 0) {
                var3.onSlotChanged();
            }
        }
        return var2;
    }

    protected boolean mergeOneItem(final ItemStack par1ItemStack, final int par2, final int par3, final boolean par4) {
        boolean flag1 = false;
        if (par1ItemStack.stackSize > 0) {
            for (int k = par2; k < par3; ++k) {
                final Slot slot = (Slot)  this.inventorySlots.get(k);
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
