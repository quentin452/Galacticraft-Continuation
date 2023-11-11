package micdoodle8.mods.galacticraft.core.tile;

import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraftforge.common.util.*;
import net.minecraft.nbt.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.planets.asteroids.*;
import net.minecraftforge.fluids.*;

public class TileEntityOxygenStorageModule extends TileEntityOxygen implements ISidedInventory, IFluidHandler
{
    public final Set<EntityPlayer> playersUsing;
    public int scaledOxygenLevel;
    private int lastScaledOxygenLevel;
    public static final int OUTPUT_PER_TICK = 500;
    public static final int OXYGEN_CAPACITY = 60000;
    private ItemStack[] containingItems;
    
    public TileEntityOxygenStorageModule() {
        super(60000.0f, 40.0f);
        this.playersUsing = new HashSet<EntityPlayer>();
        this.containingItems = new ItemStack[1];
        this.storage.setCapacity(0.0f);
        this.storage.setMaxExtract(0.0f);
    }
    
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            final ItemStack oxygenItemStack = this.getStackInSlot(0);
            if (oxygenItemStack != null && oxygenItemStack.getItem() instanceof IItemOxygenSupply) {
                final IItemOxygenSupply oxygenItem = (IItemOxygenSupply)oxygenItemStack.getItem();
                final float oxygenDraw = Math.min(this.oxygenPerTick * 2.5f, this.maxOxygen - this.storedOxygen);
                this.storedOxygen += oxygenItem.discharge(oxygenItemStack, oxygenDraw);
                if (this.storedOxygen > this.maxOxygen) {
                    this.storedOxygen = this.maxOxygen;
                }
            }
        }
        super.updateEntity();
        this.scaledOxygenLevel = this.getScaledOxygenLevel(16);
        if (this.scaledOxygenLevel != this.lastScaledOxygenLevel) {
            this.worldObj.func_147479_m(this.xCoord, this.yCoord, this.zCoord);
        }
        this.lastScaledOxygenLevel = this.scaledOxygenLevel;
        this.produceOxygen(ForgeDirection.getOrientation(this.getBlockMetadata() - 8 + 2 ^ 0x1));
        this.lastScaledOxygenLevel = this.scaledOxygenLevel;
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
    
    public EnumSet<ForgeDirection> getElectricalInputDirections() {
        return EnumSet.noneOf(ForgeDirection.class);
    }
    
    public EnumSet<ForgeDirection> getElectricalOutputDirections() {
        return EnumSet.noneOf(ForgeDirection.class);
    }
    
    public boolean shouldPullEnergy() {
        return false;
    }
    
    public boolean shouldUseEnergy() {
        return false;
    }
    
    public ForgeDirection getElectricInputDirection() {
        return null;
    }
    
    public ItemStack getBatteryInSlot() {
        return null;
    }
    
    public boolean shouldUseOxygen() {
        return false;
    }
    
    public float getOxygenProvide(final ForgeDirection direction) {
        return (this.getOxygenOutputDirection() == direction) ? Math.min(500.0f, this.getOxygenStored()) : 0.0f;
    }
    
    public EnumSet<ForgeDirection> getOxygenInputDirections() {
        return EnumSet.of(ForgeDirection.getOrientation(this.getBlockMetadata() - 8 + 2));
    }
    
    public EnumSet<ForgeDirection> getOxygenOutputDirections() {
        return EnumSet.of(ForgeDirection.getOrientation(this.getBlockMetadata() - 8 + 2 ^ 0x1));
    }
    
    public ForgeDirection getOxygenOutputDirection() {
        return ForgeDirection.getOrientation(this.getBlockMetadata() - 8 + 2 ^ 0x1);
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
        return GCCoreUtil.translate("tile.machine2.6.name");
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
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public boolean isItemValidForSlot(final int slotID, final ItemStack itemstack) {
        return slotID == 0 && itemstack != null && itemstack.getItem() instanceof IItemOxygenSupply;
    }
    
    public int[] getAccessibleSlotsFromSide(final int side) {
        return new int[] { 0 };
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack itemstack, final int side) {
        return slotID == 0 && this.isItemValidForSlot(slotID, itemstack) && itemstack.getItemDamage() < itemstack.getItem().getMaxDamage();
    }
    
    public boolean canExtractItem(final int slotID, final ItemStack itemstack, final int side) {
        return slotID == 0 && itemstack != null && FluidUtil.isEmptyContainer(itemstack);
    }
    
    public boolean canDrain(final ForgeDirection from, final Fluid fluid) {
        return false;
    }
    
    public FluidStack drain(final ForgeDirection from, final FluidStack resource, final boolean doDrain) {
        return null;
    }
    
    public FluidStack drain(final ForgeDirection from, final int maxDrain, final boolean doDrain) {
        return null;
    }
    
    public boolean canFill(final ForgeDirection from, final Fluid fluid) {
        return from.ordinal() == this.getBlockMetadata() - 8 + 2 && GalacticraftCore.isPlanetsLoaded && fluid != null && fluid.getName().equals(AsteroidsModule.fluidLiquidOxygen.getName());
    }
    
    public int fill(final ForgeDirection from, final FluidStack resource, final boolean doFill) {
        int used = 0;
        if (resource != null && this.canFill(from, resource.getFluid())) {
            used = (int)(this.receiveOxygen(resource.amount / 0.09259259f, doFill) * 0.09259259f);
        }
        return used;
    }
    
    public FluidTankInfo[] getTankInfo(final ForgeDirection from) {
        FluidTankInfo[] tankInfo = new FluidTankInfo[0];
        final int metaside = this.getBlockMetadata() - 8 + 2;
        final int side = from.ordinal();
        if (metaside == side && GalacticraftCore.isPlanetsLoaded) {
            tankInfo = new FluidTankInfo[] { new FluidTankInfo(new FluidStack(AsteroidsModule.fluidLiquidOxygen, (int)(this.getOxygenStored() * 0.09259259f)), 5555) };
        }
        return tankInfo;
    }
}
