package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.init.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;
import net.minecraftforge.common.util.*;
import net.minecraftforge.fluids.*;

public class TileEntityRefinery extends TileBaseElectricBlockWithInventory implements ISidedInventory, IFluidHandler
{
    private final int tankCapacity = 24000;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public FluidTank oilTank;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public FluidTank fuelTank;
    public static final int PROCESS_TIME_REQUIRED = 2;
    public static final int OUTPUT_PER_SECOND = 1;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int processTicks;
    private ItemStack[] containingItems;
    
    public TileEntityRefinery() {
        this.getClass();
        this.oilTank = new FluidTank(24000);
        this.getClass();
        this.fuelTank = new FluidTank(24000);
        this.processTicks = 0;
        this.containingItems = new ItemStack[3];
        this.storage.setMaxExtract(ConfigManagerCore.hardMode ? 90.0f : 60.0f);
        this.oilTank.setFluid(new FluidStack(GalacticraftCore.fluidOil, 0));
        this.fuelTank.setFluid(new FluidStack(GalacticraftCore.fluidFuel, 0));
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            if (this.containingItems[1] != null) {
                if (this.containingItems[1].getItem() instanceof ItemCanisterGeneric) {
                    if (this.containingItems[1].getItem() == GCItems.oilCanister) {
                        final int originalDamage = this.containingItems[1].getItemDamage();
                        final int used = this.oilTank.fill(new FluidStack(GalacticraftCore.fluidOil, 1001 - originalDamage), true);
                        this.containingItems[1] = new ItemStack(GCItems.oilCanister, 1, originalDamage + used);
                    }
                }
                else {
                    final FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(this.containingItems[1]);
                    if (liquid != null) {
                        final boolean isOil = FluidRegistry.getFluidName(liquid).startsWith("oil");
                        if (isOil && (this.oilTank.getFluid() == null || this.oilTank.getFluid().amount + liquid.amount <= this.oilTank.getCapacity())) {
                            this.oilTank.fill(new FluidStack(GalacticraftCore.fluidOil, liquid.amount), true);
                            if (FluidContainerRegistry.isBucket(this.containingItems[1]) && FluidContainerRegistry.isFilledContainer(this.containingItems[1])) {
                                final int amount = this.containingItems[1].stackSize;
                                if (amount > 1) {
                                    this.oilTank.fill(new FluidStack(GalacticraftCore.fluidOil, (amount - 1) * 1000), true);
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
            this.checkFluidTankTransfer(2, this.fuelTank);
            if (this.canProcess() && this.hasEnoughEnergyToRun) {
                if (this.processTicks == 0) {
                    this.processTicks = 2;
                }
                else if (--this.processTicks <= 0) {
                    this.smeltItem();
                    this.processTicks = (this.canProcess() ? 2 : 0);
                }
            }
            else {
                this.processTicks = 0;
            }
        }
    }
    
    private void checkFluidTankTransfer(final int slot, final FluidTank tank) {
        FluidUtil.tryFillContainerFuel(tank, this.containingItems, slot);
    }
    
    public int getScaledOilLevel(final int i) {
        return this.oilTank.getFluidAmount() * i / this.oilTank.getCapacity();
    }
    
    public int getScaledFuelLevel(final int i) {
        return this.fuelTank.getFluidAmount() * i / this.fuelTank.getCapacity();
    }
    
    public boolean canProcess() {
        return this.oilTank.getFluidAmount() > 0 && this.fuelTank.getFluidAmount() < this.fuelTank.getCapacity() && !this.getDisabled(0);
    }
    
    public void smeltItem() {
        if (this.canProcess()) {
            final int oilAmount = this.oilTank.getFluidAmount();
            final int fuelSpace = this.fuelTank.getCapacity() - this.fuelTank.getFluidAmount();
            final int amountToDrain = Math.min(Math.min(oilAmount, fuelSpace), 1);
            this.oilTank.drain(amountToDrain, true);
            this.fuelTank.fill(FluidRegistry.getFluidStack(ConfigManagerCore.useOldFuelFluidID ? "fuelgc" : "fuel", amountToDrain), true);
        }
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.processTicks = nbt.getInteger("smeltingTicks");
        this.containingItems = this.readStandardItemsFromNBT(nbt);
        if (nbt.hasKey("oilTank")) {
            this.oilTank.readFromNBT(nbt.getCompoundTag("oilTank"));
        }
        if (this.oilTank.getFluid() != null && this.oilTank.getFluid().getFluid() != GalacticraftCore.fluidOil) {
            this.oilTank.setFluid(new FluidStack(GalacticraftCore.fluidOil, this.oilTank.getFluidAmount()));
        }
        if (nbt.hasKey("fuelTank")) {
            this.fuelTank.readFromNBT(nbt.getCompoundTag("fuelTank"));
        }
        if (this.fuelTank.getFluid() != null && this.fuelTank.getFluid().getFluid() != GalacticraftCore.fluidFuel) {
            this.fuelTank.setFluid(new FluidStack(GalacticraftCore.fluidFuel, this.fuelTank.getFluidAmount()));
        }
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("smeltingTicks", this.processTicks);
        this.writeStandardItemsToNBT(nbt);
        if (this.oilTank.getFluid() != null) {
            nbt.setTag("oilTank", (NBTBase)this.oilTank.writeToNBT(new NBTTagCompound()));
        }
        if (this.fuelTank.getFluid() != null) {
            nbt.setTag("fuelTank", (NBTBase)this.fuelTank.writeToNBT(new NBTTagCompound()));
        }
    }
    
    protected ItemStack[] getContainingItems() {
        return this.containingItems;
    }
    
    public String getInventoryName() {
        return GCCoreUtil.translate("container.refinery.name");
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public int[] getAccessibleSlotsFromSide(final int side) {
        return new int[] { 0, 1, 2 };
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack itemstack, final int side) {
        if (itemstack == null || !this.isItemValidForSlot(slotID, itemstack)) {
            return false;
        }
        switch (slotID) {
            case 0: {
                return ItemElectricBase.isElectricItemCharged(itemstack);
            }
            case 1: {
                return FluidUtil.isOilContainerAny(itemstack);
            }
            case 2: {
                return FluidUtil.isEmptyContainer(itemstack, GCItems.fuelCanister);
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean canExtractItem(final int slotID, final ItemStack itemstack, final int side) {
        if (itemstack == null || !this.isItemValidForSlot(slotID, itemstack)) {
            return false;
        }
        switch (slotID) {
            case 0: {
                return ItemElectricBase.isElectricItemEmpty(itemstack) || !this.shouldPullEnergy();
            }
            case 1: {
                return FluidUtil.isEmptyContainer(itemstack);
            }
            case 2: {
                return FluidUtil.isFullContainer(itemstack);
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean isItemValidForSlot(final int slotID, final ItemStack itemstack) {
        switch (slotID) {
            case 0: {
                return itemstack != null && ItemElectricBase.isElectricItem(itemstack.getItem());
            }
            case 1:
            case 2: {
                return FluidUtil.isValidContainer(itemstack);
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean shouldUseEnergy() {
        return this.canProcess();
    }
    
    public ForgeDirection getElectricInputDirection() {
        return ForgeDirection.UP;
    }
    
    public boolean canDrain(final ForgeDirection from, final Fluid fluid) {
        return from.equals((Object)ForgeDirection.getOrientation(this.getBlockMetadata() + 2 ^ 0x1)) && this.fuelTank.getFluid() != null && this.fuelTank.getFluidAmount() > 0;
    }
    
    public FluidStack drain(final ForgeDirection from, final FluidStack resource, final boolean doDrain) {
        if (from.equals((Object)ForgeDirection.getOrientation(this.getBlockMetadata() + 2 ^ 0x1))) {
            return this.fuelTank.drain(resource.amount, doDrain);
        }
        return null;
    }
    
    public FluidStack drain(final ForgeDirection from, final int maxDrain, final boolean doDrain) {
        if (from.equals((Object)ForgeDirection.getOrientation(this.getBlockMetadata() + 2 ^ 0x1))) {
            return this.drain(from, new FluidStack(GalacticraftCore.fluidFuel, maxDrain), doDrain);
        }
        return null;
    }
    
    public boolean canFill(final ForgeDirection from, final Fluid fluid) {
        return from.equals((Object)ForgeDirection.getOrientation(this.getBlockMetadata() + 2)) && (this.oilTank.getFluid() == null || this.oilTank.getFluidAmount() < this.oilTank.getCapacity());
    }
    
    public int fill(final ForgeDirection from, final FluidStack resource, final boolean doFill) {
        int used = 0;
        if (from.equals((Object)ForgeDirection.getOrientation(this.getBlockMetadata() + 2))) {
            final String liquidName = FluidRegistry.getFluidName(resource);
            if (liquidName != null && liquidName.startsWith("oil")) {
                if (liquidName.equals(GalacticraftCore.fluidOil.getName())) {
                    used = this.oilTank.fill(resource, doFill);
                }
                else {
                    used = this.oilTank.fill(new FluidStack(GalacticraftCore.fluidOil, resource.amount), doFill);
                }
            }
        }
        return used;
    }
    
    public FluidTankInfo[] getTankInfo(final ForgeDirection from) {
        FluidTankInfo[] tankInfo = new FluidTankInfo[0];
        if (from == ForgeDirection.getOrientation(this.getBlockMetadata() + 2)) {
            tankInfo = new FluidTankInfo[] { new FluidTankInfo((IFluidTank)this.oilTank) };
        }
        else if (from == ForgeDirection.getOrientation(this.getBlockMetadata() + 2 ^ 0x1)) {
            tankInfo = new FluidTankInfo[] { new FluidTankInfo((IFluidTank)this.fuelTank) };
        }
        return tankInfo;
    }
}
