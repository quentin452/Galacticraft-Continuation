package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.player.*;

public abstract class InventoryEntity extends NetworkedEntity implements IInventory
{
    public ItemStack[] containedItems;
    
    public InventoryEntity(final World par1World) {
        super(par1World);
        this.containedItems = new ItemStack[0];
    }
    
    protected void readEntityFromNBT(final NBTTagCompound nbt) {
        final NBTTagList itemList = nbt.getTagList("Items", 10);
        this.containedItems = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < itemList.tagCount(); ++i) {
            final NBTTagCompound itemTag = itemList.getCompoundTagAt(i);
            final int slotID = itemTag.getByte("Slot") & 0xFF;
            if (slotID >= 0 && slotID < this.containedItems.length) {
                this.containedItems[slotID] = ItemStack.loadItemStackFromNBT(itemTag);
            }
        }
    }
    
    protected void writeEntityToNBT(final NBTTagCompound nbt) {
        final NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < this.containedItems.length; ++i) {
            if (this.containedItems[i] != null) {
                final NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte)i);
                this.containedItems[i].writeToNBT(itemTag);
                itemList.appendTag((NBTBase)itemTag);
            }
        }
        nbt.setTag("Items", (NBTBase)itemList);
    }
    
    public ItemStack getStackInSlot(final int var1) {
        return this.containedItems[var1];
    }
    
    public ItemStack decrStackSize(final int slotIndex, final int amount) {
        if (this.containedItems[slotIndex] == null) {
            return null;
        }
        if (this.containedItems[slotIndex].stackSize <= amount) {
            final ItemStack var3 = this.containedItems[slotIndex];
            this.containedItems[slotIndex] = null;
            return var3;
        }
        final ItemStack var3 = this.containedItems[slotIndex].splitStack(amount);
        if (this.containedItems[slotIndex].stackSize == 0) {
            this.containedItems[slotIndex] = null;
        }
        return var3;
    }
    
    public ItemStack getStackInSlotOnClosing(final int slotIndex) {
        if (this.containedItems[slotIndex] != null) {
            final ItemStack stack = this.containedItems[slotIndex];
            this.containedItems[slotIndex] = null;
            return stack;
        }
        return null;
    }
    
    public void setInventorySlotContents(final int slotIndex, final ItemStack stack) {
        this.containedItems[slotIndex] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
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
}
