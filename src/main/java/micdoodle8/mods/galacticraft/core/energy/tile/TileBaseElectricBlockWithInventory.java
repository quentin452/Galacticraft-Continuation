package micdoodle8.mods.galacticraft.core.energy.tile;

import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.player.*;
import net.minecraftforge.common.util.*;

public abstract class TileBaseElectricBlockWithInventory extends TileBaseElectricBlock implements IInventory
{
    public ItemStack[] readStandardItemsFromNBT(final NBTTagCompound nbt) {
        final NBTTagList var2 = nbt.getTagList("Items", 10);
        final int length = this.getSizeInventory();
        final ItemStack[] result = new ItemStack[length];
        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            final NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            final int var5 = var4.getByte("Slot") & 0xFF;
            if (var5 < length) {
                result[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
        return result;
    }
    
    public void writeStandardItemsToNBT(final NBTTagCompound nbt) {
        final NBTTagList list = new NBTTagList();
        final int length = this.getSizeInventory();
        final ItemStack[] containingItems = this.getContainingItems();
        for (int var3 = 0; var3 < length; ++var3) {
            if (containingItems[var3] != null) {
                final NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                containingItems[var3].writeToNBT(var4);
                list.appendTag((NBTBase)var4);
            }
        }
        nbt.setTag("Items", (NBTBase)list);
    }
    
    public int getSizeInventory() {
        return this.getContainingItems().length;
    }
    
    public ItemStack getStackInSlot(final int par1) {
        return this.getContainingItems()[par1];
    }
    
    public ItemStack decrStackSize(final int par1, final int par2) {
        final ItemStack[] containingItems = this.getContainingItems();
        if (containingItems[par1] == null) {
            return null;
        }
        if (containingItems[par1].stackSize <= par2) {
            final ItemStack var3 = containingItems[par1];
            containingItems[par1] = null;
            this.markDirty();
            return var3;
        }
        final ItemStack var3 = containingItems[par1].splitStack(par2);
        if (containingItems[par1].stackSize == 0) {
            containingItems[par1] = null;
        }
        this.markDirty();
        return var3;
    }
    
    public ItemStack getStackInSlotOnClosing(final int par1) {
        final ItemStack[] containingItems = this.getContainingItems();
        if (containingItems[par1] != null) {
            final ItemStack var2 = containingItems[par1];
            containingItems[par1] = null;
            this.markDirty();
            return var2;
        }
        return null;
    }
    
    public void setInventorySlotContents(final int par1, final ItemStack par2ItemStack) {
        final ItemStack[] containingItems = this.getContainingItems();
        containingItems[par1] = par2ItemStack;
        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUseableByPlayer(final EntityPlayer par1EntityPlayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && par1EntityPlayer.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }
    
    public void openInventory() {
    }
    
    public void closeInventory() {
    }
    
    public ForgeDirection getElectricInputDirection() {
        return ForgeDirection.getOrientation((this.getBlockMetadata() & 0x3) + 2);
    }
    
    public ItemStack getBatteryInSlot() {
        return this.getStackInSlot(0);
    }
    
    protected abstract ItemStack[] getContainingItems();
}
