package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.items.*;

public class ContainerExtendedInventory extends Container
{
    public InventoryPlayer inventoryPlayer;
    public InventoryExtended extendedInventory;

    public ContainerExtendedInventory(final EntityPlayer thePlayer, final InventoryExtended extendedInventory) {
        this.inventoryPlayer = thePlayer.inventory;
        this.extendedInventory = extendedInventory;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot((IInventory)thePlayer.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot((IInventory)thePlayer.inventory, i, 8 + i * 18, 142));
        }
        for (int i = 0; i < 4; ++i) {
            this.addSlotToContainer((Slot)new SlotArmorGC(thePlayer, (IInventory)thePlayer.inventory, 39 - i, 61, 8 + i * 18, i));
        }
        this.addSlotToContainer((Slot)new SlotExtendedInventory((IInventory)extendedInventory, 0, 125, 17));
        this.addSlotToContainer((Slot)new SlotExtendedInventory((IInventory)extendedInventory, 1, 125, 35));
        this.addSlotToContainer((Slot)new SlotExtendedInventory((IInventory)extendedInventory, 2, 116, 53));
        this.addSlotToContainer((Slot)new SlotExtendedInventory((IInventory)extendedInventory, 3, 134, 53));
        this.addSlotToContainer((Slot)new SlotExtendedInventory((IInventory)extendedInventory, 4, 143, 17));
        this.addSlotToContainer((Slot)new SlotExtendedInventory((IInventory)extendedInventory, 5, 107, 17));
        for (int i = 0; i < 4; ++i) {
            this.addSlotToContainer((Slot)new SlotExtendedInventory((IInventory)extendedInventory, 6 + i, 79, 8 + i * 18));
        }
    }

    public boolean canInteractWith(final EntityPlayer var1) {
        return true;
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par1) {
        ItemStack var2 = null;
        final Slot slot = (Slot) this.inventorySlots.get(par1);
        if (slot != null && slot.getHasStack()) {
            final ItemStack stack = slot.getStack();
            var2 = stack.copy();
            if (par1 >= 36) {
                if (!this.mergeItemStack(stack, 0, 36, true)) {
                    return null;
                }
            }
            else {
                boolean flag = false;
                int j = 36;
                while (j < 40) {
                    if (slot.isItemValid(stack)) {
                        if (!this.mergeOneItem(stack, j, j + 1, false)) {
                            return null;
                        }
                        flag = true;
                        break;
                    }
                    else {
                        ++j;
                    }
                }
                if (!flag) {
                    if (stack.getItem() instanceof ItemOxygenTank) {
                        if (!this.mergeOneItem(stack, 42, 44, false)) {
                            return null;
                        }
                        flag = true;
                    }
                    else {
                        j = 40;
                        while (j < 50) {
                            if (slot.isItemValid(stack)) {
                                if (!this.mergeOneItem(stack, j, j + 1, false)) {
                                    return null;
                                }
                                flag = true;
                                break;
                            }
                            else {
                                ++j;
                            }
                        }
                    }
                }
                if (!flag) {
                    if (par1 < 27) {
                        if (!this.mergeItemStack(stack, 27, 36, false)) {
                            return null;
                        }
                    }
                    else if (!this.mergeItemStack(stack, 0, 27, false)) {
                        return null;
                    }
                }
            }
            if (stack.stackSize == 0) {
                slot.putStack((ItemStack)null);
            }
            else {
                slot.onSlotChanged();
            }
            if (stack.stackSize == var2.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(par1EntityPlayer, stack);
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
