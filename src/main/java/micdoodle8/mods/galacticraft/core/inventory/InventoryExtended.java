package micdoodle8.mods.galacticraft.core.inventory;

import micdoodle8.mods.galacticraft.api.inventory.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;

public class InventoryExtended implements IInventoryGC
{
    public ItemStack[] inventoryStacks;
    
    public InventoryExtended() {
        this.inventoryStacks = new ItemStack[10];
    }
    
    public int getSizeInventory() {
        return this.inventoryStacks.length;
    }
    
    public ItemStack getStackInSlot(final int i) {
        return this.inventoryStacks[i];
    }
    
    public ItemStack decrStackSize(final int i, final int j) {
        if (this.inventoryStacks[i] == null) {
            return null;
        }
        if (this.inventoryStacks[i].stackSize <= j) {
            final ItemStack var3 = this.inventoryStacks[i];
            this.inventoryStacks[i] = null;
            return var3;
        }
        final ItemStack var3 = this.inventoryStacks[i].splitStack(j);
        if (this.inventoryStacks[i].stackSize == 0) {
            this.inventoryStacks[i] = null;
        }
        return var3;
    }
    
    public ItemStack getStackInSlotOnClosing(final int i) {
        return null;
    }
    
    public void setInventorySlotContents(final int i, final ItemStack itemstack) {
        this.inventoryStacks[i] = itemstack;
        if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
            itemstack.stackSize = this.getInventoryStackLimit();
        }
    }
    
    public String getInventoryName() {
        return "Galacticraft Player Inventory";
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public void markDirty() {
    }
    
    public boolean isUseableByPlayer(final EntityPlayer entityplayer) {
        return true;
    }
    
    public void openInventory() {
    }
    
    public void closeInventory() {
    }
    
    public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
        return false;
    }
    
    public void dropExtendedItems(final EntityPlayer player) {
        for (int i = 0; i < this.inventoryStacks.length; ++i) {
            final ItemStack stack = this.inventoryStacks[i];
            if (stack != null) {
                player.dropPlayerItemWithRandomChoice(stack, true);
            }
            this.inventoryStacks[i] = null;
        }
    }
    
    public void readFromNBTOld(final NBTTagList par1NBTTagList) {
        this.inventoryStacks = new ItemStack[10];
        for (int i = 0; i < par1NBTTagList.tagCount(); ++i) {
            final NBTTagCompound nbttagcompound = par1NBTTagList.getCompoundTagAt(i);
            final int j = nbttagcompound.getByte("Slot") & 0xFF;
            final ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);
            if (itemstack != null && j >= 200 && j < this.inventoryStacks.length + 200 - 1) {
                this.inventoryStacks[j - 200] = itemstack;
            }
        }
    }
    
    public void readFromNBT(final NBTTagList tagList) {
        this.inventoryStacks = new ItemStack[10];
        for (int i = 0; i < tagList.tagCount(); ++i) {
            final NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            final int j = nbttagcompound.getByte("Slot") & 0xFF;
            final ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);
            if (itemstack != null) {
                this.inventoryStacks[j] = itemstack;
            }
        }
    }
    
    public NBTTagList writeToNBT(final NBTTagList tagList) {
        for (int i = 0; i < this.inventoryStacks.length; ++i) {
            if (this.inventoryStacks[i] != null) {
                final NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                this.inventoryStacks[i].writeToNBT(nbttagcompound);
                tagList.appendTag((NBTBase)nbttagcompound);
            }
        }
        return tagList;
    }
    
    public void copyInventory(final IInventoryGC par1InventoryPlayer) {
        final InventoryExtended toCopy = (InventoryExtended)par1InventoryPlayer;
        for (int i = 0; i < this.inventoryStacks.length; ++i) {
            this.inventoryStacks[i] = ItemStack.copyItemStack(toCopy.inventoryStacks[i]);
        }
    }
}
