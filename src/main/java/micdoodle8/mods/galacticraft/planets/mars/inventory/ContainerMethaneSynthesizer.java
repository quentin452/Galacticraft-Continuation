package micdoodle8.mods.galacticraft.planets.mars.inventory;

import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import micdoodle8.mods.galacticraft.api.item.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class ContainerMethaneSynthesizer extends Container
{
    private final TileEntityMethaneSynthesizer tileEntity;

    public ContainerMethaneSynthesizer(final InventoryPlayer par1InventoryPlayer, final TileEntityMethaneSynthesizer tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)tileEntity, 0, 53, 53, new Class[] { IItemElectric.class }));
        this.addSlotToContainer(new Slot((IInventory)tileEntity, 1, 7, 7));
        this.addSlotToContainer(new Slot((IInventory)tileEntity, 2, 28, 7));
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)tileEntity, 3, 28, 53, new ItemStack[] { new ItemStack(MarsItems.carbonFragments, 1, 0) }));
        this.addSlotToContainer(new Slot((IInventory)tileEntity, 4, 153, 7));
        for (int var3 = 0; var3 < 3; ++var3) {
            for (int var4 = 0; var4 < 9; ++var4) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 104 + var3 * 18 - 18));
            }
        }
        for (int var3 = 0; var3 < 9; ++var3) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var3, 8 + var3 * 18, 144));
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
        final Slot slot = (Slot) this.inventorySlots.get(par1);
        if (slot != null && slot.getHasStack()) {
            final ItemStack var3 = slot.getStack();
            var2 = var3.copy();
            if (par1 < 5) {
                if (!this.mergeItemStack(var3, 5, 41, true)) {
                    return null;
                }
                if (par1 == 2) {
                    slot.onSlotChange(var3, var2);
                }
            }
            else if (var3.getItem() instanceof IItemElectric) {
                if (!this.mergeItemStack(var3, 0, 1, false)) {
                    return null;
                }
            }
            else if (var3.getItem() instanceof ItemAtmosphericValve) {
                if (!this.mergeItemStack(var3, 2, 3, false)) {
                    return null;
                }
            }
            else if (var3.getItem() == MarsItems.carbonFragments) {
                if (!this.mergeItemStack(var3, 3, 4, false)) {
                    return null;
                }
            }
            else if (FluidUtil.isEmptyContainer(var3, AsteroidsItems.methaneCanister)) {
                if (!this.mergeItemStack(var3, 4, 5, false)) {
                    return null;
                }
            }
            else if (par1 < 32) {
                if (!this.mergeItemStack(var3, 32, 41, false)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(var3, 5, 32, false)) {
                return null;
            }
            if (var3.stackSize == 0) {
                slot.putStack((ItemStack)null);
            }
            else {
                slot.onSlotChanged();
            }
            if (var3.stackSize == var2.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(par1EntityPlayer, var3);
        }
        return var2;
    }
}
