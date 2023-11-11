package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.api.tile.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.init.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.tileentity.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;
import net.minecraftforge.fluids.*;
import net.minecraft.world.*;

public class TileEntityFuelLoader extends TileBaseElectricBlockWithInventory implements ISidedInventory, IFluidHandler, ILandingPadAttachable
{
    private final int tankCapacity = 12000;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public FluidTank fuelTank;
    private ItemStack[] containingItems;
    public IFuelable attachedFuelable;
    private boolean loadedFuelLastTick;
    
    public TileEntityFuelLoader() {
        this.getClass();
        this.fuelTank = new FluidTank(12000);
        this.containingItems = new ItemStack[2];
        this.loadedFuelLastTick = false;
        this.storage.setMaxExtract(30.0f);
    }
    
    public int getScaledFuelLevel(final int i) {
        final double fuelLevel = (this.fuelTank.getFluid() == null) ? 0.0 : this.fuelTank.getFluid().amount;
        final double n = fuelLevel * i;
        this.getClass();
        return (int)(n / 12000.0);
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            this.loadedFuelLastTick = false;
            if (this.containingItems[1] != null) {
                if (this.containingItems[1].getItem() instanceof ItemCanisterGeneric) {
                    if (this.containingItems[1].getItem() == GCItems.fuelCanister) {
                        final int originalDamage = this.containingItems[1].getItemDamage();
                        final int used = this.fuelTank.fill(new FluidStack(GalacticraftCore.fluidFuel, 1001 - originalDamage), true);
                        if (originalDamage + used == 1001) {
                            this.containingItems[1] = new ItemStack(GCItems.oilCanister, 1, 1001);
                        }
                        else {
                            this.containingItems[1] = new ItemStack(GCItems.fuelCanister, 1, originalDamage + used);
                        }
                    }
                }
                else {
                    final FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(this.containingItems[1]);
                    if (liquid != null) {
                        final boolean isFuel = FluidUtil.testFuel(FluidRegistry.getFluidName(liquid));
                        if (isFuel && (this.fuelTank.getFluid() == null || this.fuelTank.getFluid().amount + liquid.amount <= this.fuelTank.getCapacity())) {
                            this.fuelTank.fill(new FluidStack(GalacticraftCore.fluidFuel, liquid.amount), true);
                            if (FluidContainerRegistry.isBucket(this.containingItems[1]) && FluidContainerRegistry.isFilledContainer(this.containingItems[1])) {
                                final int amount = this.containingItems[1].stackSize;
                                if (amount > 1) {
                                    this.fuelTank.fill(new FluidStack(GalacticraftCore.fluidFuel, (amount - 1) * 1000), true);
                                }
                                this.containingItems[1] = new ItemStack(Items.bucket, amount);
                            }
                            else {
                                final ItemStack itemStack = this.containingItems[1];
                                --itemStack.stackSize;
                                if (this.containingItems[1].stackSize == 0) {
                                    this.containingItems[1] = null;
                                }
                            }
                        }
                    }
                }
            }
            if (this.ticks % 100 == 0) {
                this.attachedFuelable = null;
                for (final ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                    final TileEntity pad = new BlockVec3((TileEntity)this).getTileEntityOnSide(this.worldObj, dir);
                    if (pad instanceof TileEntityMulti) {
                        final TileEntity mainTile = ((TileEntityMulti)pad).getMainBlockTile();
                        if (mainTile instanceof IFuelable) {
                            this.attachedFuelable = (IFuelable)mainTile;
                            break;
                        }
                    }
                    else if (pad instanceof IFuelable) {
                        this.attachedFuelable = (IFuelable)pad;
                        break;
                    }
                }
            }
            if (this.fuelTank != null && this.fuelTank.getFluid() != null && this.fuelTank.getFluid().amount > 0) {
                final FluidStack liquid = new FluidStack(GalacticraftCore.fluidFuel, 2);
                if (this.attachedFuelable != null && this.hasEnoughEnergyToRun && !this.disabled) {
                    final int filled = this.attachedFuelable.addFuel(liquid, true);
                    this.loadedFuelLastTick = (filled > 0);
                    this.fuelTank.drain(filled, true);
                }
            }
        }
    }
    
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        this.containingItems = this.readStandardItemsFromNBT(par1NBTTagCompound);
        if (par1NBTTagCompound.hasKey("fuelTank")) {
            this.fuelTank.readFromNBT(par1NBTTagCompound.getCompoundTag("fuelTank"));
        }
    }
    
    public void writeToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        this.writeStandardItemsToNBT(par1NBTTagCompound);
        if (this.fuelTank.getFluid() != null) {
            par1NBTTagCompound.setTag("fuelTank", (NBTBase)this.fuelTank.writeToNBT(new NBTTagCompound()));
        }
    }
    
    protected ItemStack[] getContainingItems() {
        return this.containingItems;
    }
    
    public String getInventoryName() {
        return GCCoreUtil.translate("container.fuelloader.name");
    }
    
    public int getInventoryStackLimit() {
        return 1;
    }
    
    public int[] getAccessibleSlotsFromSide(final int side) {
        return new int[] { 0, 1 };
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack itemstack, final int side) {
        return this.isItemValidForSlot(slotID, itemstack);
    }
    
    public boolean canExtractItem(final int slotID, final ItemStack itemstack, final int side) {
        return slotID == 1 && itemstack != null && FluidUtil.isEmptyContainer(itemstack);
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public boolean isItemValidForSlot(final int slotID, final ItemStack itemstack) {
        return (slotID == 1 && itemstack != null && itemstack.getItem() == GCItems.fuelCanister) || (slotID == 0 && ItemElectricBase.isElectricItem(itemstack.getItem()));
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
        return this.fuelTank.getFluid() == null || this.fuelTank.getFluidAmount() < this.fuelTank.getCapacity();
    }
    
    public int fill(final ForgeDirection from, final FluidStack resource, final boolean doFill) {
        int used = 0;
        if (from.equals((Object)ForgeDirection.getOrientation(this.getBlockMetadata() + 2).getOpposite()) && FluidUtil.testFuel(FluidRegistry.getFluidName(resource))) {
            used = this.fuelTank.fill(resource, doFill);
        }
        return used;
    }
    
    public FluidTankInfo[] getTankInfo(final ForgeDirection from) {
        return new FluidTankInfo[] { new FluidTankInfo((IFluidTank)this.fuelTank) };
    }
    
    public boolean shouldUseEnergy() {
        return this.fuelTank.getFluid() != null && this.fuelTank.getFluid().amount > 0 && !this.getDisabled(0) && this.loadedFuelLastTick;
    }
    
    public boolean canAttachToLandingPad(final IBlockAccess world, final int x, final int y, final int z) {
        return true;
    }
}
