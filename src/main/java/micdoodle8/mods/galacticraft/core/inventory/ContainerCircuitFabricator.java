package micdoodle8.mods.galacticraft.core.inventory;

import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.item.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.init.*;
import net.minecraft.item.*;

public class ContainerCircuitFabricator extends Container
{
    private TileEntityCircuitFabricator tileEntity;

    public ContainerCircuitFabricator(final InventoryPlayer playerInv, final TileEntityCircuitFabricator tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)tileEntity, 0, 6, 69, new Class[] { IItemElectric.class }));
        ArrayList<ItemStack> slotContentsList = CircuitFabricatorRecipes.slotValidItems.get(0);
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)tileEntity, 1, 15, 17, (ItemStack[])slotContentsList.toArray(new ItemStack[slotContentsList.size()])));
        slotContentsList = CircuitFabricatorRecipes.slotValidItems.get(1);
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)tileEntity, 2, 74, 46, (ItemStack[])slotContentsList.toArray(new ItemStack[slotContentsList.size()])));
        slotContentsList = CircuitFabricatorRecipes.slotValidItems.get(2);
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)tileEntity, 3, 74, 64, (ItemStack[])slotContentsList.toArray(new ItemStack[slotContentsList.size()])));
        slotContentsList = CircuitFabricatorRecipes.slotValidItems.get(3);
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)tileEntity, 4, 122, 46, (ItemStack[])slotContentsList.toArray(new ItemStack[slotContentsList.size()])));
        slotContentsList = CircuitFabricatorRecipes.slotValidItems.get(4);
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)tileEntity, 5, 145, 20, (ItemStack[])slotContentsList.toArray(new ItemStack[slotContentsList.size()])));
        this.addSlotToContainer((Slot)new SlotFurnace(playerInv.player, (IInventory)tileEntity, 6, 152, 86));
        for (int slot = 0; slot < 3; ++slot) {
            for (int var4 = 0; var4 < 9; ++var4) {
                this.addSlotToContainer(new Slot((IInventory)playerInv, var4 + slot * 9 + 9, 8 + var4 * 18, 110 + slot * 18));
            }
        }
        for (int slot = 0; slot < 9; ++slot) {
            this.addSlotToContainer(new Slot((IInventory)playerInv, slot, 8 + slot * 18, 168));
        }
    }

    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return this.tileEntity.isUseableByPlayer(par1EntityPlayer);
    }

    public void onCraftMatrixChanged(final IInventory par1IInventory) {
        super.onCraftMatrixChanged(par1IInventory);
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
                if (par1 == 6) {
                    slot.onSlotChange(var3, var2);
                }
            }
            else {
                final Item i = var3.getItem();
                if (i instanceof IItemElectric) {
                    if (!this.mergeItemStack(var3, 0, 1, false)) {
                        return null;
                    }
                }
                else if (i == Items.diamond) {
                    if (!this.mergeItemStack(var3, 1, 2, false)) {
                        return null;
                    }
                }
                else if (i == GCItems.basicItem && i.getDamage(var3) == 2) {
                    if (!this.mergeItemStack(var3, 2, 4, false)) {
                        return null;
                    }
                }
                else if (i == Items.redstone) {
                    if (!this.mergeItemStack(var3, 4, 5, false)) {
                        return null;
                    }
                }
                else if (i == Items.repeater || i == new ItemStack(Blocks.redstone_torch).getItem() || (i == Items.dye && i.getDamage(var3) == 4)) {
                    if (!this.mergeItemStack(var3, 5, 6, false)) {
                        return null;
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
