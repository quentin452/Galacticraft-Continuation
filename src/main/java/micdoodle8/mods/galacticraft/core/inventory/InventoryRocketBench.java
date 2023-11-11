package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.item.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;

public class InventoryRocketBench implements IInventory
{
    private final ItemStack[] stackList;
    private final int inventoryWidth;
    private final Container eventHandler;
    
    public InventoryRocketBench(final Container par1Container) {
        final int var4 = 18;
        this.stackList = new ItemStack[18];
        this.eventHandler = par1Container;
        this.inventoryWidth = 5;
    }
    
    public int getSizeInventory() {
        return this.stackList.length;
    }
    
    public ItemStack getStackInSlot(final int par1) {
        return (par1 >= this.getSizeInventory()) ? null : this.stackList[par1];
    }
    
    public ItemStack getStackInRowAndColumn(final int par1, final int par2) {
        if (par1 < 0 || par1 >= this.inventoryWidth) {
            return null;
        }
        final int var3 = par1 + par2 * this.inventoryWidth;
        if (var3 >= 18) {
            return null;
        }
        return this.getStackInSlot(var3);
    }
    
    public String getInventoryName() {
        return "container.crafting";
    }
    
    public ItemStack getStackInSlotOnClosing(final int par1) {
        if (this.stackList[par1] != null) {
            final ItemStack var2 = this.stackList[par1];
            this.stackList[par1] = null;
            return var2;
        }
        return null;
    }
    
    public ItemStack decrStackSize(final int par1, final int par2) {
        if (this.stackList[par1] == null) {
            return null;
        }
        if (this.stackList[par1].stackSize <= par2) {
            final ItemStack var3 = this.stackList[par1];
            this.stackList[par1] = null;
            this.eventHandler.onCraftMatrixChanged((IInventory)this);
            return var3;
        }
        final ItemStack var3 = this.stackList[par1].splitStack(par2);
        if (this.stackList[par1].stackSize == 0) {
            this.stackList[par1] = null;
        }
        this.eventHandler.onCraftMatrixChanged((IInventory)this);
        return var3;
    }
    
    public void setInventorySlotContents(final int par1, final ItemStack par2ItemStack) {
        this.stackList[par1] = par2ItemStack;
        this.eventHandler.onCraftMatrixChanged((IInventory)this);
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
    
    public boolean hasCustomInventoryName() {
        return false;
    }
    
    public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
        return false;
    }
}
