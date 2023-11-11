package micdoodle8.mods.galacticraft.planets.mars.inventory;

import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import net.minecraft.inventory.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;

public class InventorySlimeling implements IInventory
{
    private ItemStack[] stackList;
    private EntitySlimeling slimeling;
    public Container currentContainer;
    
    public InventorySlimeling(final EntitySlimeling slimeling) {
        this.stackList = new ItemStack[30];
        this.slimeling = slimeling;
    }
    
    public int getSizeInventory() {
        return this.stackList.length;
    }
    
    public ItemStack getStackInSlot(final int par1) {
        return (par1 >= this.getSizeInventory()) ? null : this.stackList[par1];
    }
    
    public String getInventoryName() {
        return "Slimeling Inventory";
    }
    
    public ItemStack getStackInSlotOnClosing(final int par1) {
        if (this.stackList[par1] != null) {
            final ItemStack var2 = this.stackList[par1];
            this.stackList[par1] = null;
            return var2;
        }
        return null;
    }
    
    private void removeInventoryBagContents() {
        if (this.currentContainer instanceof ContainerSlimeling) {
            ContainerSlimeling.removeSlots((ContainerSlimeling)this.currentContainer);
        }
        for (int i = 2; i < this.stackList.length; ++i) {
            if (this.stackList[i] != null) {
                if (!this.slimeling.worldObj.isRemote) {
                    this.slimeling.entityDropItem(this.stackList[i], 0.5f);
                }
                this.stackList[i] = null;
            }
        }
    }
    
    public ItemStack decrStackSize(final int par1, final int par2) {
        if (this.stackList[par1] == null) {
            return null;
        }
        if (par1 == 1 && this.stackList[par1].stackSize <= par2) {
            this.removeInventoryBagContents();
            final ItemStack var3 = this.stackList[par1];
            this.stackList[par1] = null;
            return var3;
        }
        final ItemStack var3 = this.stackList[par1].splitStack(par2);
        if (this.stackList[par1].stackSize == 0) {
            if (par1 == 1) {
                this.removeInventoryBagContents();
            }
            this.stackList[par1] = null;
        }
        return var3;
    }
    
    public void setInventorySlotContents(final int par1, final ItemStack par2ItemStack) {
        if (par1 == 1 && ((par2ItemStack == null && this.stackList[par1] != null) || !ItemStack.areItemStacksEqual(par2ItemStack, this.stackList[par1]))) {
            ContainerSlimeling.addAdditionalSlots((ContainerSlimeling)this.currentContainer, this.slimeling, par2ItemStack);
        }
        this.stackList[par1] = par2ItemStack;
    }
    
    public void readFromNBT(final NBTTagList tagList) {
        if (tagList == null || tagList.tagCount() <= 0) {
            return;
        }
        this.stackList = new ItemStack[this.stackList.length];
        for (int i = 0; i < tagList.tagCount(); ++i) {
            final NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            final int j = nbttagcompound.getByte("Slot") & 0xFF;
            final ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);
            if (itemstack != null) {
                this.stackList[j] = itemstack;
            }
        }
    }
    
    public NBTTagList writeToNBT(final NBTTagList tagList) {
        for (int i = 0; i < this.stackList.length; ++i) {
            if (this.stackList[i] != null) {
                final NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                this.stackList[i].writeToNBT(nbttagcompound);
                tagList.appendTag((NBTBase)nbttagcompound);
            }
        }
        return tagList;
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public void markDirty() {
    }
    
    public boolean isUseableByPlayer(final EntityPlayer par1EntityPlayer) {
        return !this.slimeling.isDead && par1EntityPlayer.getDistanceSqToEntity((Entity)this.slimeling) <= 64.0;
    }
    
    public void openInventory() {
    }
    
    public void closeInventory() {
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
        return false;
    }
}
