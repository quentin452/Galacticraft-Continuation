package micdoodle8.mods.galacticraft.planets.mars.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.api.tile.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraftforge.fluids.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import micdoodle8.mods.galacticraft.planets.asteroids.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.oxygen.*;
import mekanism.api.gas.*;
import micdoodle8.mods.galacticraft.api.transmission.grid.*;
import micdoodle8.mods.galacticraft.api.transmission.*;

public class TileEntityElectrolyzer extends TileBaseElectricBlockWithInventory implements ISidedInventory, IDisableableMachine, IFluidHandler, IOxygenStorage, IOxygenReceiver
{
    private final int tankCapacity = 4000;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public FluidTank waterTank;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public FluidTank liquidTank;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public FluidTank liquidTank2;
    public int processTimeRequired;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int processTicks;
    private ItemStack[] containingItems;

    public TileEntityElectrolyzer() {
        this.getClass();
        this.waterTank = new FluidTank(4000);
        this.getClass();
        this.liquidTank = new FluidTank(4000);
        this.getClass();
        this.liquidTank2 = new FluidTank(4000);
        this.processTimeRequired = 3;
        this.processTicks = 0;
        this.containingItems = new ItemStack[4];
        this.storage.setMaxExtract(ConfigManagerCore.hardMode ? 150.0f : 120.0f);
        this.setTierGC(2);
    }

    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            if (this.containingItems[1] != null) {
                final FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(this.containingItems[1]);
                if (liquid != null && liquid.getFluid().getName().equals(FluidRegistry.WATER.getName()) && (this.waterTank.getFluid() == null || this.waterTank.getFluid().amount + liquid.amount <= this.waterTank.getCapacity())) {
                    this.waterTank.fill(liquid, true);
                    this.containingItems[1] = FluidUtil.getUsedContainer(this.containingItems[1]);
                }
            }
            this.checkFluidTankTransfer(2, this.liquidTank);
            this.checkFluidTankTransfer(3, this.liquidTank2);
            if (this.hasEnoughEnergyToRun && this.canProcess()) {
                if (this.tierGC == 2) {
                    this.processTimeRequired = ((this.poweredByTierGC == 2) ? 2 : 3);
                }
                if (this.processTicks == 0) {
                    this.processTicks = this.processTimeRequired;
                }
                else if (--this.processTicks <= 0) {
                    this.doElectrolysis();
                    this.processTicks = (this.canProcess() ? this.processTimeRequired : 0);
                }
            }
            else {
                this.processTicks = 0;
            }
            this.produceOxygen(ForgeDirection.getOrientation(this.getOxygenOutputDirection()));
            this.produceHydrogen(ForgeDirection.getOrientation(this.getHydrogenOutputDirection()));
        }
    }

    private void doElectrolysis() {
        final int waterAmount = this.waterTank.getFluid().amount;
        if (waterAmount == 0) {
            return;
        }
        this.placeIntoFluidTanks(2);
        this.waterTank.drain(1, true);
    }

    private int placeIntoFluidTanks(int amountToDrain) {
        final int fuelSpace = this.liquidTank.getCapacity() - this.liquidTank.getFluidAmount();
        final int fuelSpace2 = this.liquidTank2.getCapacity() - this.liquidTank2.getFluidAmount();
        int amountToDrain2 = amountToDrain * 2;
        if (amountToDrain > fuelSpace) {
            amountToDrain = fuelSpace;
        }
        this.liquidTank.fill(FluidRegistry.getFluidStack("oxygen", amountToDrain), true);
        if (amountToDrain2 > fuelSpace2) {
            amountToDrain2 = fuelSpace2;
        }
        this.liquidTank2.fill(FluidRegistry.getFluidStack("hydrogen", amountToDrain2), true);
        return amountToDrain;
    }

    private void checkFluidTankTransfer(final int slot, final FluidTank tank) {
        if (this.containingItems[slot] != null && this.containingItems[slot].getItem() instanceof ItemAtmosphericValve) {
            tank.drain(4, true);
        }
    }

    public int getScaledGasLevel(final int i) {
        return (this.waterTank.getFluid() != null) ? (this.waterTank.getFluid().amount * i / this.waterTank.getCapacity()) : 0;
    }

    public int getScaledFuelLevel(final int i) {
        return (this.liquidTank.getFluid() != null) ? (this.liquidTank.getFluid().amount * i / this.liquidTank.getCapacity()) : 0;
    }

    public int getScaledFuelLevel2(final int i) {
        return (this.liquidTank2.getFluid() != null) ? (this.liquidTank2.getFluid().amount * i / this.liquidTank2.getCapacity()) : 0;
    }

    public boolean canProcess() {
        if (this.waterTank.getFluid() == null || this.waterTank.getFluid().amount <= 0 || this.getDisabled(0)) {
            return false;
        }
        final boolean tank1HasSpace = this.liquidTank.getFluidAmount() < this.liquidTank.getCapacity();
        final boolean tank2HasSpace = this.liquidTank2.getFluidAmount() < this.liquidTank2.getCapacity();
        return tank1HasSpace || tank2HasSpace;
    }

    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.processTicks = nbt.getInteger("processTicks");
        this.containingItems = this.readStandardItemsFromNBT(nbt);
        if (nbt.hasKey("waterTank")) {
            this.waterTank.readFromNBT(nbt.getCompoundTag("waterTank"));
        }
        if (nbt.hasKey("gasTank")) {
            this.liquidTank.readFromNBT(nbt.getCompoundTag("gasTank"));
        }
        if (nbt.hasKey("gasTank2")) {
            this.liquidTank2.readFromNBT(nbt.getCompoundTag("gasTank2"));
        }
    }

    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("processTicks", this.processTicks);
        this.writeStandardItemsToNBT(nbt);
        if (this.waterTank.getFluid() != null) {
            nbt.setTag("waterTank", (NBTBase)this.waterTank.writeToNBT(new NBTTagCompound()));
        }
        if (this.liquidTank.getFluid() != null) {
            nbt.setTag("gasTank", (NBTBase)this.liquidTank.writeToNBT(new NBTTagCompound()));
        }
        if (this.liquidTank2.getFluid() != null) {
            nbt.setTag("gasTank2", (NBTBase)this.liquidTank2.writeToNBT(new NBTTagCompound()));
        }
    }

    protected ItemStack[] getContainingItems() {
        return this.containingItems;
    }

    public boolean hasCustomInventoryName() {
        return true;
    }

    public String getInventoryName() {
        return GCCoreUtil.translate("tile.marsMachine.6.name");
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
                return ItemElectricBase.isElectricItemCharged(itemstack);
            }
            case 1: {
                return itemstack.getItem() == Items.water_bucket;
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
                return ItemElectricBase.isElectricItemEmpty(itemstack);
            }
            case 1: {
                return itemstack.getItem() == Items.bucket;
            }
            default: {
                return false;
            }
        }
    }

    public boolean isItemValidForSlot(final int slotID, final ItemStack itemstack) {
        final Item item = itemstack.getItem();
        switch (slotID) {
            case 0: {
                return ItemElectricBase.isElectricItem(item);
            }
            case 1: {
                return item == Items.bucket || item == Items.water_bucket;
            }
            default: {
                return false;
            }
        }
    }

    public boolean shouldUseEnergy() {
        return this.canProcess();
    }

    public double getPacketRange() {
        return 320.0;
    }

    public ForgeDirection getElectricInputDirection() {
        return ForgeDirection.DOWN;
    }

    public boolean canDrain(final ForgeDirection from, final Fluid fluid) {
        final int metaside = this.getBlockMetadata() + 2;
        final int side = from.ordinal();
        if (side == (metaside ^ 0x1)) {
            return this.liquidTank2.getFluid() != null && this.liquidTank2.getFluidAmount() > 0;
        }
        return 7 - (metaside ^ ((metaside <= 3) ? 1 : 0)) == (side ^ 0x1) && this.liquidTank.getFluid() != null && this.liquidTank.getFluidAmount() > 0;
    }

    public FluidStack drain(final ForgeDirection from, final FluidStack resource, final boolean doDrain) {
        final int metaside = this.getBlockMetadata() + 2;
        final int side = from.ordinal();
        if (side == (metaside ^ 0x1) && resource != null && resource.isFluidEqual(this.liquidTank2.getFluid())) {
            return this.liquidTank2.drain(resource.amount, doDrain);
        }
        boolean b = false;
        if (7 - ((b ^ ((b = (metaside != 0)) ? 1 : 0) <= 3) ? 1 : 0) == (side ^ 0x1) && resource != null && resource.isFluidEqual(this.liquidTank.getFluid())) {
            return this.liquidTank.drain(resource.amount, doDrain);
        }
        return null;
    }

    public FluidStack drain(final ForgeDirection from, final int maxDrain, final boolean doDrain) {
        final int metaside = this.getBlockMetadata() + 2;
        final int side = from.ordinal();
        if (side == (metaside ^ 0x1)) {
            return this.liquidTank2.drain(maxDrain, doDrain);
        }
        if (7 - (metaside ^ ((metaside <= 3) ? 1 : 0)) == (side ^ 0x1)) {
            return this.liquidTank.drain(maxDrain, doDrain);
        }
        return null;
    }

    public boolean canFill(final ForgeDirection from, final Fluid fluid) {
        return from.ordinal() == this.getBlockMetadata() + 2 && fluid != null && fluid.getName().equals(FluidRegistry.WATER.getName());
    }

    public int fill(final ForgeDirection from, final FluidStack resource, final boolean doFill) {
        int used = 0;
        if (resource != null && this.canFill(from, resource.getFluid())) {
            used = this.waterTank.fill(resource, doFill);
        }
        return used;
    }

    public FluidTankInfo[] getTankInfo(final ForgeDirection from) {
        FluidTankInfo[] tankInfo = new FluidTankInfo[0];
        final int metaside = this.getBlockMetadata() + 2;
        final int side = from.ordinal();
        if (metaside == side) {
            tankInfo = new FluidTankInfo[] { new FluidTankInfo((IFluidTank)this.waterTank) };
        }
        else if (metaside == (side ^ 0x1)) {
            tankInfo = new FluidTankInfo[] { new FluidTankInfo((IFluidTank)this.liquidTank2) };
        }
        else if (7 - (metaside ^ ((metaside <= 3) ? 1 : 0)) == (side ^ 0x1)) {
            tankInfo = new FluidTankInfo[] { new FluidTankInfo((IFluidTank)this.liquidTank) };
        }
        return tankInfo;
    }

    public int getBlockMetadata() {
        if (this.blockMetadata == -1) {
            this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        }
        return this.blockMetadata & 0x3;
    }

    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public int receiveGas(final ForgeDirection side, final GasStack stack, final boolean doTransfer) {
        return 0;
    }

    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public int receiveGas(final ForgeDirection side, final GasStack stack) {
        return 0;
    }

    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public GasStack drawGas(final ForgeDirection from, final int amount, final boolean doTransfer) {
        final int metaside = this.getBlockMetadata() + 2;
        final int side = from.ordinal();
        if (metaside == (side ^ 0x1) && this.liquidTank2.getFluid() != null) {
            int amountH = Math.min(8, this.liquidTank2.getFluidAmount());
            amountH = this.liquidTank2.drain(amountH, doTransfer).amount;
            return new GasStack((Gas)EnergyConfigHandler.gasHydrogen, amountH);
        }
        boolean b = false;
        if (7 - ((b ^ ((b = (metaside != 0)) ? 1 : 0) <= 3) ? 1 : 0) == (side ^ 0x1) && this.liquidTank.getFluid() != null) {
            int amountO = Math.min(8, this.liquidTank.getFluidAmount());
            amountO = this.liquidTank.drain(amountO, doTransfer).amount;
            return new GasStack((Gas)EnergyConfigHandler.gasOxygen, amountO);
        }
        return null;
    }

    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public GasStack drawGas(final ForgeDirection from, final int amount) {
        return this.drawGas(from, amount, true);
    }

    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public boolean canReceiveGas(final ForgeDirection side, final Gas type) {
        return false;
    }

    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public boolean canDrawGas(final ForgeDirection from, final Gas type) {
        final int metaside = this.getBlockMetadata() + 2;
        final int side = from.ordinal();
        if (metaside == (side ^ 0x1)) {
            return type.getName().equals("hydrogen");
        }
        return 7 - (metaside ^ ((metaside <= 3) ? 1 : 0)) == (side ^ 0x1) && type.getName().equals("oxygen");
    }

    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.ITubeConnection", modID = "Mekanism")
    public boolean canTubeConnect(final ForgeDirection from) {
        final int metaside = this.getBlockMetadata() + 2;
        final int side = from.ordinal();
        return metaside == (side ^ 0x1) || 7 - (metaside ^ ((metaside <= 3) ? 1 : 0)) == (side ^ 0x1);
    }

    public void setOxygenStored(final float amount) {
        this.liquidTank.setFluid(new FluidStack(AsteroidsModule.fluidOxygenGas, (int)amount));
    }

    public float getOxygenStored() {
        return (float)this.liquidTank.getFluidAmount();
    }

    public int getHydrogenStored() {
        return this.liquidTank2.getFluidAmount();
    }

    public float getMaxOxygenStored() {
        return (float)this.liquidTank.getCapacity();
    }

    private int getOxygenOutputDirection() {
        final int metaside = this.getBlockMetadata() + 2;
        return 7 - (metaside ^ ((metaside <= 3) ? 1 : 0)) ^ 0x1;
    }

    private int getHydrogenOutputDirection() {
        final int metaside = this.getBlockMetadata() + 2;
        return metaside ^ 0x1;
    }

    private boolean produceOxygen(final ForgeDirection outputDirection) {
        final float provide = this.getOxygenProvide(outputDirection);
        if (provide > 0.0f) {
            final TileEntity outputTile = new BlockVec3((TileEntity)this).modifyPositionFromSide(outputDirection).getTileEntity((IBlockAccess)this.worldObj);
            final IOxygenNetwork outputNetwork = NetworkHelper.getOxygenNetworkFromTileEntity(outputTile, outputDirection);
            if (outputNetwork != null) {
                final float powerRequest = outputNetwork.getRequest(new TileEntity[] { (TileEntity)this });
                if (powerRequest > 0.0f) {
                    final float toSend = Math.min(this.getOxygenStored(), provide);
                    final float rejectedPower = outputNetwork.produce(toSend, new TileEntity[] { (TileEntity)this });
                    this.provideOxygen(Math.max(toSend - rejectedPower, 0.0f), true);
                    return true;
                }
            }
            else if (outputTile instanceof IOxygenReceiver) {
                final float requestedOxygen = ((IOxygenReceiver)outputTile).getOxygenRequest(outputDirection.getOpposite());
                if (requestedOxygen > 0.0f) {
                    final float toSend = Math.min(this.getOxygenStored(), provide);
                    final float acceptedOxygen = ((IOxygenReceiver)outputTile).receiveOxygen(outputDirection.getOpposite(), toSend, true);
                    this.provideOxygen(acceptedOxygen, true);
                    return true;
                }
            }
            else if (EnergyConfigHandler.isMekanismLoaded() && outputTile instanceof IGasHandler && ((IGasHandler)outputTile).canReceiveGas(outputDirection.getOpposite(), (Gas)EnergyConfigHandler.gasOxygen)) {
                final GasStack toSend2 = new GasStack((Gas)EnergyConfigHandler.gasOxygen, (int)Math.floor(Math.min(this.getOxygenStored(), provide)));
                int acceptedOxygen2 = 0;
                try {
                    acceptedOxygen2 = ((IGasHandler)outputTile).receiveGas(outputDirection.getOpposite(), toSend2);
                }
                catch (Exception ex) {}
                this.provideOxygen((float)acceptedOxygen2, true);
                return true;
            }
        }
        return false;
    }

    private boolean produceHydrogen(final ForgeDirection outputDirection) {
        final float provide = this.getHydrogenProvide(outputDirection);
        if (provide > 0.0f) {
            final TileEntity outputTile = new BlockVec3((TileEntity)this).modifyPositionFromSide(outputDirection).getTileEntity((IBlockAccess)this.worldObj);
            final IHydrogenNetwork outputNetwork = NetworkHelper.getHydrogenNetworkFromTileEntity(outputTile, outputDirection);
            if (outputNetwork != null) {
                final float powerRequest = outputNetwork.getRequest(new TileEntity[] { (TileEntity)this });
                if (powerRequest > 0.0f) {
                    final float toSend = Math.min((float)this.getHydrogenStored(), provide);
                    final float rejectedPower = outputNetwork.produce(toSend, new TileEntity[] { (TileEntity)this });
                    this.provideHydrogen((int)Math.max(toSend - rejectedPower, 0.0f), true);
                    return true;
                }
            }
            else if (outputTile instanceof TileEntityMethaneSynthesizer) {
                final float requestedHydrogen = ((TileEntityMethaneSynthesizer)outputTile).getHydrogenRequest(outputDirection.getOpposite());
                if (requestedHydrogen > 0.0f) {
                    final float toSend = Math.min((float)this.getHydrogenStored(), provide);
                    final float acceptedHydrogen = ((TileEntityMethaneSynthesizer)outputTile).receiveHydrogen(outputDirection.getOpposite(), toSend, true);
                    this.provideHydrogen((int)acceptedHydrogen, true);
                    return true;
                }
            }
            else if (EnergyConfigHandler.isMekanismLoaded() && outputTile instanceof IGasHandler && ((IGasHandler)outputTile).canReceiveGas(outputDirection.getOpposite(), (Gas)EnergyConfigHandler.gasHydrogen)) {
                final GasStack toSend2 = new GasStack((Gas)EnergyConfigHandler.gasHydrogen, (int)Math.floor(Math.min((float)this.getHydrogenStored(), provide)));
                int acceptedHydrogen2 = 0;
                try {
                    acceptedHydrogen2 = ((IGasHandler)outputTile).receiveGas(outputDirection.getOpposite(), toSend2);
                }
                catch (Exception ex) {}
                this.provideHydrogen(acceptedHydrogen2, true);
                return true;
            }
        }
        return false;
    }

    public float provideOxygen(final ForgeDirection from, final float request, final boolean doProvide) {
        if (this.getOxygenOutputDirection() == from.ordinal()) {
            return this.provideOxygen(request, doProvide);
        }
        return 0.0f;
    }

    public float provideOxygen(final float request, final boolean doProvide) {
        if (request > 0.0f) {
            final float requestedOxygen = Math.min(request, (float)this.liquidTank.getFluidAmount());
            if (doProvide) {
                this.setOxygenStored(this.liquidTank.getFluidAmount() - requestedOxygen);
            }
            return requestedOxygen;
        }
        return 0.0f;
    }

    public int provideHydrogen(final int request, final boolean doProvide) {
        if (request > 0) {
            final int currentHydrogen = this.liquidTank2.getFluidAmount();
            final int requestedHydrogen = Math.min(request, currentHydrogen);
            if (doProvide) {
                this.liquidTank2.setFluid(new FluidStack(FluidRegistry.getFluid("hydrogen"), currentHydrogen - requestedHydrogen));
            }
            return requestedHydrogen;
        }
        return 0;
    }

    public float getOxygenProvide(final ForgeDirection direction) {
        return (this.getOxygenOutputDirection() == direction.ordinal()) ? Math.min(500.0f, this.getOxygenStored()) : 0.0f;
    }

    public float getHydrogenProvide(final ForgeDirection direction) {
        return (this.getHydrogenOutputDirection() == direction.ordinal()) ? ((float)Math.min(500, this.getHydrogenStored())) : 0.0f;
    }

    public boolean shouldPullOxygen() {
        return false;
    }

    public float receiveOxygen(final ForgeDirection from, final float receive, final boolean doReceive) {
        return 0.0f;
    }

    public float getOxygenRequest(final ForgeDirection direction) {
        return 0.0f;
    }

    public boolean canConnect(final ForgeDirection direction, final NetworkType type) {
        if (direction == null || direction.equals((Object)ForgeDirection.UNKNOWN)) {
            return false;
        }
        if (type == NetworkType.OXYGEN) {
            return this.getOxygenOutputDirection() == direction.ordinal();
        }
        if (type == NetworkType.HYDROGEN) {
            return this.getHydrogenOutputDirection() == direction.ordinal();
        }
        return type == NetworkType.POWER && direction == this.getElectricInputDirection();
    }
}
