package micdoodle8.mods.galacticraft.planets.mars.inventory;

import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;

public class ContainerSlimeling extends Container
{
    private final InventorySlimeling slimelingInventory;

    public ContainerSlimeling(final InventoryPlayer playerInventory, final EntitySlimeling slimeling) {
        this.slimelingInventory = slimeling.slimelingInventory;
        addSlots((ContainerSlimeling)(this.slimelingInventory.currentContainer = this), playerInventory, slimeling);
        addAdditionalSlots(this, slimeling, slimeling.getCargoSlot());
        this.slimelingInventory.openInventory();
    }

    public static void addSlots(final ContainerSlimeling container, final InventoryPlayer playerInventory, final EntitySlimeling slimeling) {
        Slot slot = (Slot)new SlotSpecific((IInventory)slimeling.slimelingInventory, 1, 9, 30, new ItemStack[] { new ItemStack(MarsItems.marsItemBasic, 1, 4) });
        container.addSlotToContainer(slot);
        for (int var3 = 0; var3 < 3; ++var3) {
            for (int var4 = 0; var4 < 9; ++var4) {
                slot = new Slot((IInventory)playerInventory, var4 + var3 * 9 + 9, 8 + var4 * 18, 129 + var3 * 18);
                container.addSlotToContainer(slot);
            }
        }
        for (int var3 = 0; var3 < 9; ++var3) {
            slot = new Slot((IInventory)playerInventory, var3, 8 + var3 * 18, 187);
            container.addSlotToContainer(slot);
        }
    }

    public static void removeSlots(final ContainerSlimeling container) {
        container.inventoryItemStacks = container.inventoryItemStacks.subList(0, 37);
        container.inventorySlots = container.inventorySlots.subList(0, 37);
    }

    public static void addAdditionalSlots(final ContainerSlimeling container, final EntitySlimeling slimeling, final ItemStack stack) {
        if (stack != null && stack.getItem() == MarsItems.marsItemBasic && stack.getItemDamage() == 4 && container.inventorySlots.size() < 63) {
            for (int var3 = 0; var3 < 3; ++var3) {
                for (int var4 = 0; var4 < 9; ++var4) {
                    final Slot slot = new Slot((IInventory)slimeling.slimelingInventory, var4 + var3 * 9 + 2, 8 + var4 * 18, 54 + var3 * 18);
                    slot.slotNumber = container.inventorySlots.size();
                    container.inventorySlots.add(slot);
                    container.inventoryItemStacks.add(null);
                }
            }
        }
    }

    public void onContainerClosed(final EntityPlayer entityplayer) {
        this.slimelingInventory.closeInventory();
    }

    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return this.slimelingInventory.isUseableByPlayer(par1EntityPlayer);
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par1) {
        ItemStack var2 = null;
        final Slot slot = (Slot) this.inventorySlots.get(par1);
        final int b = this.inventorySlots.size();
        if (slot != null && slot.getHasStack()) {
            final ItemStack var3 = slot.getStack();
            var2 = var3.copy();
            if (b < 39) {
                if (par1 < b - 36) {
                    if (!this.mergeItemStack(var3, b - 36, b, true)) {
                        return null;
                    }
                }
                else if (var3.getItem() == MarsItems.marsItemBasic && var3.getItemDamage() == 4) {
                    if (!this.mergeItemStack(var3, 0, 1, false)) {
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
            else {
                if (par1 == 0) {
                    return null;
                }
                if (par1 > 36) {
                    if (!this.mergeItemStack(var3, 1, 37, true)) {
                        return null;
                    }
                }
                else if (par1 < 28) {
                    if (!this.mergeItemStack(var3, 37, 64, false) && !this.mergeItemStack(var3, 28, 37, false)) {
                        return null;
                    }
                }
                else if (!this.mergeItemStack(var3, 37, 64, false) && !this.mergeItemStack(var3, 1, 28, false)) {
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
