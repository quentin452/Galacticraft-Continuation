package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;

public class ContainerSchematic extends Container
{
    public InventorySchematic craftMatrix;
    public IInventory craftResult;
    private final World worldObj;

    public ContainerSchematic(final InventoryPlayer par1InventoryPlayer, final int x, final int y, final int z) {
        this.craftMatrix = new InventorySchematic(this);
        this.craftResult = (IInventory)new InventoryCraftResult();
        this.worldObj = par1InventoryPlayer.player.worldObj;
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)this.craftMatrix, 0, 80, 1, new Class[] { ISchematicItem.class }));
        for (int var6 = 0; var6 < 3; ++var6) {
            for (int var7 = 0; var7 < 9; ++var7) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 111 + var6 * 18 - 59 + 16));
            }
        }
        for (int var6 = 0; var6 < 9; ++var6) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var6, 8 + var6 * 18, 126));
        }
        this.onCraftMatrixChanged((IInventory)this.craftMatrix);
    }

    public void onContainerClosed(final EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (!this.worldObj.isRemote) {
            for (int var2 = 0; var2 < 1; ++var2) {
                final ItemStack var3 = this.craftMatrix.getStackInSlotOnClosing(var2);
                if (var3 != null) {
                    par1EntityPlayer.entityDropItem(var3, 0.0f);
                }
            }
        }
    }

    public boolean canInteractWith(final EntityPlayer entityplayer) {
        return true;
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par2) {
        ItemStack var3 = null;
        final Slot var4 = (Slot)  this.inventorySlots.get(par2);
        if (var4 != null && var4.getHasStack()) {
            final ItemStack var5 = var4.getStack();
            var3 = var5.copy();
            if (par2 < 1) {
                if (!this.mergeItemStack(var5, 1, this.inventorySlots.size(), true)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(var5, 0, 1, false)) {
                return null;
            }
            if (var5.stackSize == 0) {
                var4.putStack((ItemStack)null);
            }
            else {
                var4.onSlotChanged();
            }
        }
        return var3;
    }
}
