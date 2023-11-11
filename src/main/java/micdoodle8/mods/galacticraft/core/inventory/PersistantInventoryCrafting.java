package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.item.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;

public class PersistantInventoryCrafting implements IInventory
{
    private ItemStack[] stackList;
    private int inventoryWidth;
    public Container eventHandler;
    
    public PersistantInventoryCrafting() {
        final int k = 9;
        this.stackList = new ItemStack[k];
        this.inventoryWidth = 3;
    }
    
    public int getSizeInventory() {
        return this.stackList.length;
    }
    
    public ItemStack getStackInSlot(final int par1) {
        return (par1 >= this.getSizeInventory()) ? null : this.stackList[par1];
    }
    
    public ItemStack getStackInRowAndColumn(final int par1, final int par2) {
        if (par1 >= 0 && par1 < this.inventoryWidth) {
            final int k = par1 + par2 * this.inventoryWidth;
            return this.getStackInSlot(k);
        }
        return null;
    }
    
    public String getInventoryName() {
        return "container.crafting";
    }
    
    public boolean hasCustomInventoryName() {
        return false;
    }
    
    public ItemStack getStackInSlotOnClosing(final int par1) {
        if (this.stackList[par1] != null) {
            final ItemStack itemstack = this.stackList[par1];
            this.stackList[par1] = null;
            return itemstack;
        }
        return null;
    }
    
    public ItemStack decrStackSize(final int par1, final int par2) {
        if (this.stackList[par1] == null) {
            return null;
        }
        if (this.stackList[par1].stackSize <= par2) {
            final ItemStack itemstack = this.stackList[par1];
            this.stackList[par1] = null;
            if (this.eventHandler != null) {
                this.eventHandler.onCraftMatrixChanged((IInventory)this);
            }
            return itemstack;
        }
        final ItemStack itemstack = this.stackList[par1].splitStack(par2);
        if (this.stackList[par1].stackSize == 0) {
            this.stackList[par1] = null;
        }
        if (this.eventHandler != null) {
            this.eventHandler.onCraftMatrixChanged((IInventory)this);
        }
        return itemstack;
    }
    
    public void setInventorySlotContents(final int par1, final ItemStack par2ItemStack) {
        this.stackList[par1] = par2ItemStack;
        if (this.eventHandler != null) {
            this.eventHandler.onCraftMatrixChanged((IInventory)this);
        }
    }
    
    public void setInventorySlotContentsNoUpdate(final int par1, final ItemStack par2ItemStack) {
        this.stackList[par1] = par2ItemStack;
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public void markDirty() {
    }
    
    public boolean isUseableByPlayer(final EntityPlayer par1EntityPlayer) {
        return true;
    }
    
    public void openInventory() {
    }
    
    public void closeInventory() {
    }
    
    public boolean isItemValidForSlot(final int par1, final ItemStack par2ItemStack) {
        return true;
    }
}
