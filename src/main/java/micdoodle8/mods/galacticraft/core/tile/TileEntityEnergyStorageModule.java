package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.block.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;
import micdoodle8.mods.galacticraft.api.item.*;
import java.util.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.api.transmission.*;

public class TileEntityEnergyStorageModule extends TileBaseUniversalElectricalSource implements ISidedInventory, IConnector
{
    private static final float BASE_CAPACITY = 500000.0f;
    private static final float TIER2_CAPACITY = 2500000.0f;
    private ItemStack[] containingItems;
    public final Set<EntityPlayer> playersUsing;
    public int scaledEnergyLevel;
    public int lastScaledEnergyLevel;
    private float lastEnergy;
    private boolean initialised;
    
    public TileEntityEnergyStorageModule() {
        this(1);
    }
    
    public TileEntityEnergyStorageModule(final int tier) {
        this.containingItems = new ItemStack[2];
        this.playersUsing = new HashSet<EntityPlayer>();
        this.lastEnergy = 0.0f;
        this.initialised = false;
        this.initialised = true;
        if (tier == 1) {
            this.storage.setCapacity(500000.0f);
            this.storage.setMaxExtract(300.0f);
            return;
        }
        this.setTier2();
    }
    
    private void setTier2() {
        this.storage.setCapacity(2500000.0f);
        this.storage.setMaxExtract(1800.0f);
        this.setTierGC(2);
    }
    
    public void updateEntity() {
        if (!this.initialised) {
            final int metadata = this.getBlockMetadata();
            final Block b = this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
            if (b == GCBlocks.machineBase) {
                this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, GCBlocks.machineTiered, 0, 2);
            }
            else if (metadata >= 8) {
                this.setTier2();
            }
            this.initialised = true;
        }
        final float energy = this.storage.getEnergyStoredGC();
        if (this.getTierGC() == 1 && !this.worldObj.isRemote && this.lastEnergy - energy > this.storage.getMaxExtract() - 1.0f) {
            this.storage.extractEnergyGC(25.0f, false);
        }
        this.lastEnergy = energy;
        super.updateEntity();
        this.scaledEnergyLevel = (int)Math.floor((this.getEnergyStoredGC() + 49.0f) * 16.0f / this.getMaxEnergyStoredGC());
        if (this.scaledEnergyLevel != this.lastScaledEnergyLevel) {
            this.worldObj.func_147479_m(this.xCoord, this.yCoord, this.zCoord);
        }
        if (!this.worldObj.isRemote) {
            this.recharge(this.containingItems[0]);
            this.discharge(this.containingItems[1]);
        }
        if (!this.worldObj.isRemote) {
            this.produce();
        }
        this.lastScaledEnergyLevel = this.scaledEnergyLevel;
    }
    
    public void openInventory() {
    }
    
    public void closeInventory() {
    }
    
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        if (this.storage.getEnergyStoredGC() > 500000.0f) {
            this.setTier2();
            this.initialised = true;
        }
        else {
            this.initialised = false;
        }
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
        if (this.tierGC == 1 && this.storage.getEnergyStoredGC() > 500000.0f) {
            this.storage.setEnergyStored(500000.0f);
        }
        super.writeToNBT(par1NBTTagCompound);
        final NBTTagList var2 = new NBTTagList();
        for (int var3 = 0; var3 < this.containingItems.length; ++var3) {
            if (this.containingItems[var3] != null) {
                final NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.containingItems[var3].writeToNBT(var4);
                var2.appendTag((NBTBase)var4);
            }
        }
        par1NBTTagCompound.setTag("Items", (NBTBase)var2);
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
        return GCCoreUtil.translate((this.tierGC == 1) ? "tile.machine.1.name" : "tile.machine.8.name");
    }
    
    public int getInventoryStackLimit() {
        return 1;
    }
    
    public boolean isUseableByPlayer(final EntityPlayer par1EntityPlayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && par1EntityPlayer.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public boolean isItemValidForSlot(final int slotID, final ItemStack itemstack) {
        return ItemElectricBase.isElectricItem(itemstack.getItem());
    }
    
    public int[] getAccessibleSlotsFromSide(final int slotID) {
        return new int[] { 0, 1 };
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack itemstack, final int side) {
        if (itemstack.getItem() instanceof IItemElectricBase) {
            if (slotID == 0) {
                return ((IItemElectricBase)itemstack.getItem()).getTransfer(itemstack) > 0.0f;
            }
            if (slotID == 1) {
                return ((IItemElectricBase)itemstack.getItem()).getElectricityStored(itemstack) > 0.0f;
            }
        }
        return false;
    }
    
    public boolean canExtractItem(final int slotID, final ItemStack itemstack, final int side) {
        if (itemstack.getItem() instanceof IItemElectricBase) {
            if (slotID == 0) {
                return ((IItemElectricBase)itemstack.getItem()).getTransfer(itemstack) <= 0.0f;
            }
            if (slotID == 1) {
                return ((IItemElectricBase)itemstack.getItem()).getElectricityStored(itemstack) <= 0.0f || this.getEnergyStoredGC() >= this.getMaxEnergyStoredGC();
            }
        }
        return false;
    }
    
    public EnumSet<ForgeDirection> getElectricalInputDirections() {
        return EnumSet.of(ForgeDirection.getOrientation((this.getBlockMetadata() & 0x3) + 2).getOpposite(), ForgeDirection.UNKNOWN);
    }
    
    public EnumSet<ForgeDirection> getElectricalOutputDirections() {
        return EnumSet.of(ForgeDirection.getOrientation((this.getBlockMetadata() & 0x3) + 2), ForgeDirection.UNKNOWN);
    }
    
    public ForgeDirection getElectricalOutputDirectionMain() {
        return ForgeDirection.getOrientation((this.getBlockMetadata() & 0x3) + 2);
    }
    
    public boolean canConnect(final ForgeDirection direction, final NetworkType type) {
        if (direction == null || direction.equals((Object)ForgeDirection.UNKNOWN) || type != NetworkType.POWER) {
            return false;
        }
        final int metadata = this.getBlockMetadata() & 0x3;
        return direction == ForgeDirection.getOrientation(metadata + 2) || direction == ForgeDirection.getOrientation(metadata + 2 ^ 0x1);
    }
}
