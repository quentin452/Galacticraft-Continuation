package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.api.power.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.entity.player.*;
import net.minecraftforge.common.util.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.transmission.*;

public class TileEntityCoalGenerator extends TileBaseUniversalElectricalSource implements IInventory, ISidedInventory, IConnector
{
    public static final int MAX_GENERATE_GJ_PER_TICK = 150;
    public static final int MIN_GENERATE_GJ_PER_TICK = 30;
    private static final float BASE_ACCELERATION = 0.3f;
    public float prevGenerateWatts;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public float heatGJperTick;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int itemCookTime;
    private ItemStack[] containingItems;
    
    public TileEntityCoalGenerator() {
        this.prevGenerateWatts = 0.0f;
        this.heatGJperTick = 0.0f;
        this.itemCookTime = 0;
        this.containingItems = new ItemStack[1];
        this.storage.setMaxExtract(120.0f);
    }
    
    public void updateEntity() {
        if (!this.worldObj.isRemote && this.heatGJperTick - 30.0f > 0.0f) {
            this.receiveEnergyGC((EnergySource)null, this.heatGJperTick - 30.0f, false);
        }
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            if (this.itemCookTime > 0) {
                --this.itemCookTime;
                this.heatGJperTick = Math.min(this.heatGJperTick + Math.max(this.heatGJperTick * 0.005f, 0.3f), 150.0f);
            }
            if (this.itemCookTime <= 0 && this.containingItems[0] != null) {
                if (this.containingItems[0].getItem() == Items.coal && this.containingItems[0].stackSize > 0) {
                    this.itemCookTime = 320;
                    this.decrStackSize(0, 1);
                }
                else if (this.containingItems[0].getItem() == Item.getItemFromBlock(Blocks.coal_block) && this.containingItems[0].stackSize > 0) {
                    this.itemCookTime = 3200;
                    this.decrStackSize(0, 1);
                }
            }
            this.produce();
            if (this.itemCookTime <= 0) {
                this.heatGJperTick = Math.max(this.heatGJperTick - 0.3f, 0.0f);
            }
            this.heatGJperTick = Math.min(Math.max(this.heatGJperTick, 0.0f), this.getMaxEnergyStoredGC());
        }
    }
    
    public void openInventory() {
    }
    
    public void closeInventory() {
    }
    
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        this.itemCookTime = par1NBTTagCompound.getInteger("itemCookTime");
        this.heatGJperTick = (float)par1NBTTagCompound.getInteger("generateRateInt");
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
        par1NBTTagCompound.setInteger("itemCookTime", this.itemCookTime);
        par1NBTTagCompound.setFloat("generateRate", this.heatGJperTick);
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
        return GCCoreUtil.translate("tile.machine.0.name");
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUseableByPlayer(final EntityPlayer par1EntityPlayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && par1EntityPlayer.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public boolean isItemValidForSlot(final int slotID, final ItemStack itemstack) {
        return itemstack.getItem() == Items.coal || itemstack.getItem() == Item.getItemFromBlock(Blocks.coal_block);
    }
    
    public int[] getAccessibleSlotsFromSide(final int var1) {
        return new int[] { 0 };
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack itemstack, final int j) {
        return this.isItemValidForSlot(slotID, itemstack);
    }
    
    public boolean canExtractItem(final int slotID, final ItemStack itemstack, final int j) {
        return slotID == 0;
    }
    
    public float receiveElectricity(final ForgeDirection from, final float energy, final int tier, final boolean doReceive) {
        return 0.0f;
    }
    
    public EnumSet<ForgeDirection> getElectricalInputDirections() {
        return EnumSet.noneOf(ForgeDirection.class);
    }
    
    public EnumSet<ForgeDirection> getElectricalOutputDirections() {
        return EnumSet.of(ForgeDirection.getOrientation(this.getBlockMetadata() + 2));
    }
    
    public ForgeDirection getElectricalOutputDirectionMain() {
        return ForgeDirection.getOrientation(this.getBlockMetadata() + 2);
    }
    
    public boolean canConnect(final ForgeDirection direction, final NetworkType type) {
        return direction != null && !direction.equals((Object)ForgeDirection.UNKNOWN) && type == NetworkType.POWER && direction == this.getElectricalOutputDirectionMain();
    }
}
