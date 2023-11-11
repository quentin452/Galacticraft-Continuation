package micdoodle8.mods.galacticraft.core.tile;

import net.minecraft.inventory.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;
import net.minecraftforge.common.util.*;
import java.util.*;

public class TileEntityOxygenDecompressor extends TileEntityOxygen implements IInventory, ISidedInventory
{
    private ItemStack[] containingItems;
    public static final int OUTPUT_PER_TICK = 100;
    private boolean usingEnergy;
    
    public TileEntityOxygenDecompressor() {
        super(1200.0f, 0.0f);
        this.containingItems = new ItemStack[2];
        this.usingEnergy = false;
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            this.usingEnergy = false;
            final ItemStack tank1 = this.containingItems[0];
            if (tank1 != null && this.hasEnoughEnergyToRun && this.getOxygenStored() < this.getMaxOxygenStored() && tank1.getItem() instanceof ItemOxygenTank && tank1.getItemDamage() < tank1.getMaxDamage()) {
                tank1.setItemDamage(tank1.getItemDamage() + 1);
                this.receiveOxygen(1.0f, true);
                this.usingEnergy = true;
            }
            this.produceOxygen();
        }
    }
    
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        final NBTTagList var2 = par1NBTTagCompound.getTagList("Items", 10);
        this.containingItems = new ItemStack[this.getSizeInventory()];
        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            final NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            final int var5 = var4.getByte("Slot") & 0xFF;
            if (var5 < this.containingItems.length) {
                this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
    }
    
    public void writeToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        final NBTTagList list = new NBTTagList();
        for (int var3 = 0; var3 < this.containingItems.length; ++var3) {
            if (this.containingItems[var3] != null) {
                final NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.containingItems[var3].writeToNBT(var4);
                list.appendTag((NBTBase)var4);
            }
        }
        par1NBTTagCompound.setTag("Items", (NBTBase)list);
    }
    
    public int getSizeInventory() {
        return this.containingItems.length;
    }
    
    public ItemStack getStackInSlot(final int par1) {
        return this.containingItems[par1];
    }
    
    public ItemStack decrStackSize(final int par1, final int par2) {
        if (this.containingItems[par1] == null) {
            return null;
        }
        if (this.containingItems[par1].stackSize <= par2) {
            final ItemStack var3 = this.containingItems[par1];
            this.containingItems[par1] = null;
            return var3;
        }
        final ItemStack var3 = this.containingItems[par1].splitStack(par2);
        if (this.containingItems[par1].stackSize == 0) {
            this.containingItems[par1] = null;
        }
        return var3;
    }
    
    public ItemStack getStackInSlotOnClosing(final int par1) {
        if (this.containingItems[par1] != null) {
            final ItemStack var2 = this.containingItems[par1];
            this.containingItems[par1] = null;
            return var2;
        }
        return null;
    }
    
    public void setInventorySlotContents(final int par1, final ItemStack par2ItemStack) {
        this.containingItems[par1] = par2ItemStack;
        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }
    
    public String getInventoryName() {
        return GCCoreUtil.translate("container.oxygendecompressor.name");
    }
    
    public int getInventoryStackLimit() {
        return 1;
    }
    
    public boolean isUseableByPlayer(final EntityPlayer par1EntityPlayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && par1EntityPlayer.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }
    
    public void openInventory() {
    }
    
    public void closeInventory() {
    }
    
    public int[] getAccessibleSlotsFromSide(final int side) {
        return new int[] { 0, 1 };
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack itemstack, final int side) {
        if (!this.isItemValidForSlot(slotID, itemstack)) {
            return false;
        }
        switch (slotID) {
            case 0: {
                return itemstack.getItemDamage() < itemstack.getMaxDamage();
            }
            case 1: {
                return ItemElectricBase.isElectricItemCharged(itemstack);
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean canExtractItem(final int slotID, final ItemStack itemstack, final int side) {
        if (!this.isItemValidForSlot(slotID, itemstack)) {
            return false;
        }
        switch (slotID) {
            case 0: {
                return itemstack.getItemDamage() == itemstack.getMaxDamage();
            }
            case 1: {
                return ItemElectricBase.isElectricItemEmpty(itemstack);
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean isItemValidForSlot(final int slotID, final ItemStack itemstack) {
        switch (slotID) {
            case 0: {
                return itemstack.getItem() instanceof ItemOxygenTank;
            }
            case 1: {
                return ItemElectricBase.isElectricItem(itemstack.getItem());
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public boolean shouldUseEnergy() {
        return this.usingEnergy;
    }
    
    public ForgeDirection getElectricInputDirection() {
        return ForgeDirection.getOrientation(this.getBlockMetadata() - 4 + 2);
    }
    
    public ItemStack getBatteryInSlot() {
        return this.getStackInSlot(1);
    }
    
    public ForgeDirection getOxygenOutputDirection() {
        return this.getElectricInputDirection().getOpposite();
    }
    
    public EnumSet<ForgeDirection> getOxygenInputDirections() {
        return EnumSet.noneOf(ForgeDirection.class);
    }
    
    public EnumSet<ForgeDirection> getOxygenOutputDirections() {
        return EnumSet.of(this.getElectricInputDirection().getOpposite());
    }
    
    public boolean shouldPullOxygen() {
        return false;
    }
    
    public boolean shouldUseOxygen() {
        return false;
    }
    
    public float getOxygenProvide(final ForgeDirection direction) {
        return this.getOxygenOutputDirections().contains(direction) ? Math.min(100.0f, this.getOxygenStored()) : 0.0f;
    }
}
