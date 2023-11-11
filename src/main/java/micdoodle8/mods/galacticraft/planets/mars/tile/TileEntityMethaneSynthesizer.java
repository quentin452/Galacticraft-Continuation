package micdoodle8.mods.galacticraft.planets.mars.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.api.tile.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import net.minecraft.block.material.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.*;
import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.world.*;
import java.util.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;
import net.minecraftforge.common.util.*;
import net.minecraftforge.fluids.*;
import mekanism.api.gas.*;
import micdoodle8.mods.galacticraft.api.transmission.*;

public class TileEntityMethaneSynthesizer extends TileBaseElectricBlockWithInventory implements ISidedInventory, IDisableableMachine, IFluidHandler
{
    private final int tankCapacity = 4000;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public FluidTank gasTank;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public FluidTank gasTank2;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public FluidTank liquidTank;
    public int processTimeRequired;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int processTicks;
    private ItemStack[] containingItems;
    private int hasCO2;
    private boolean noCoal;
    private int coalPartial;
    
    public TileEntityMethaneSynthesizer() {
        this.getClass();
        this.gasTank = new FluidTank(4000);
        this.getClass();
        this.gasTank2 = new FluidTank(4000 / 2);
        this.getClass();
        this.liquidTank = new FluidTank(4000 / 2);
        this.processTimeRequired = 3;
        this.processTicks = -8;
        this.containingItems = new ItemStack[5];
        this.hasCO2 = -1;
        this.noCoal = true;
        this.coalPartial = 0;
        this.storage.setMaxExtract(ConfigManagerCore.hardMode ? 60.0f : 30.0f);
        this.setTierGC(2);
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (this.hasCO2 == -1) {
            this.hasCO2 = this.getAirProducts();
        }
        if (!this.worldObj.isRemote) {
            if (this.hasCO2 == 0 && this.gasTank2.getFluidAmount() > 0) {
                this.gasTank2.drain(this.gasTank2.getFluidAmount(), true);
            }
            final ItemStack inputCanister = this.containingItems[2];
            if (inputCanister != null && inputCanister.getItem() instanceof ItemAtmosphericValve && this.hasCO2 > 0 && this.gasTank2.getFluidAmount() < this.gasTank2.getCapacity()) {
                final Block blockAbove = this.worldObj.getBlock(this.xCoord, this.yCoord + 1, this.zCoord);
                if (blockAbove != null && blockAbove.getMaterial() == Material.air && blockAbove != GCBlocks.breatheableAir && blockAbove != GCBlocks.brightBreatheableAir && !OxygenUtil.inOxygenBubble(this.worldObj, this.xCoord + 0.5, this.yCoord + 1.0, this.zCoord + 0.5)) {
                    final FluidStack gcAtmosphere = FluidRegistry.getFluidStack("carbondioxide", 4);
                    this.gasTank2.fill(gcAtmosphere, true);
                }
            }
            this.checkFluidTankTransfer(4, this.liquidTank);
            if (this.hasEnoughEnergyToRun && this.canProcess()) {
                if (this.tierGC == 2) {
                    this.processTimeRequired = ((this.poweredByTierGC == 2) ? 2 : 3);
                }
                if (this.processTicks <= 0) {
                    this.processTicks = this.processTimeRequired;
                }
                else if (--this.processTicks <= 0) {
                    this.doLiquefaction();
                    this.processTicks = (this.canProcess() ? this.processTimeRequired : 0);
                }
            }
            else if (this.processTicks > 0) {
                this.processTicks = 0;
            }
            else if (--this.processTicks <= -8) {
                this.processTicks = -8;
            }
        }
    }
    
    private void checkFluidTankTransfer(final int slot, final FluidTank tank) {
        if (FluidUtil.isValidContainer(this.containingItems[slot])) {
            final FluidStack liquid = tank.getFluid();
            if (liquid != null) {
                FluidUtil.tryFillContainer(tank, liquid, this.containingItems, slot, AsteroidsItems.methaneCanister);
            }
        }
        else if (this.containingItems[slot] != null && this.containingItems[slot].getItem() instanceof ItemAtmosphericValve) {
            tank.drain(4, true);
        }
    }
    
    public int getScaledGasLevel(final int i) {
        return (this.gasTank.getFluid() != null) ? (this.gasTank.getFluid().amount * i / this.gasTank.getCapacity()) : 0;
    }
    
    public int getScaledGasLevel2(final int i) {
        return (this.gasTank2.getFluid() != null) ? (this.gasTank2.getFluid().amount * i / this.gasTank2.getCapacity()) : 0;
    }
    
    public int getScaledFuelLevel(final int i) {
        return (this.liquidTank.getFluid() != null) ? (this.liquidTank.getFluid().amount * i / this.liquidTank.getCapacity()) : 0;
    }
    
    public boolean canProcess() {
        if (this.gasTank.getFluid() == null || this.gasTank.getFluid().amount <= 0 || this.getDisabled(0)) {
            return false;
        }
        this.noCoal = (this.containingItems[3] == null || this.containingItems[3].stackSize == 0 || this.containingItems[3].getItem() != MarsItems.carbonFragments);
        return (!this.noCoal || this.coalPartial != 0 || (this.gasTank2.getFluid() != null && this.gasTank2.getFluidAmount() > 0)) && this.liquidTank.getFluidAmount() < this.liquidTank.getCapacity();
    }
    
    public int getAirProducts() {
        final WorldProvider WP = this.worldObj.provider;
        if (!(WP instanceof WorldProviderSpace)) {
            return 0;
        }
        final ArrayList<IAtmosphericGas> atmos = (ArrayList<IAtmosphericGas>)((WorldProviderSpace)WP).getCelestialBody().atmosphere;
        if (atmos.size() > 0 && atmos.get(0) == IAtmosphericGas.CO2) {
            return 1;
        }
        if (atmos.size() > 1 && atmos.get(1) == IAtmosphericGas.CO2) {
            return 1;
        }
        if (atmos.size() > 2 && atmos.get(2) == IAtmosphericGas.CO2) {
            return 1;
        }
        return 0;
    }
    
    public void doLiquefaction() {
        if (this.noCoal && this.coalPartial == 0) {
            if (this.gasTank2.getFluid() == null || this.gasTank2.drain(1, true).amount < 1) {
                return;
            }
        }
        else {
            if (this.coalPartial == 0) {
                this.decrStackSize(3, 1);
            }
            ++this.coalPartial;
            if (this.coalPartial == 40) {
                this.coalPartial = 0;
            }
        }
        this.gasTank.drain(this.placeIntoFluidTanks(2) * 8, true);
    }
    
    private int placeIntoFluidTanks(int amountToDrain) {
        final int fuelSpace = this.liquidTank.getCapacity() - this.liquidTank.getFluidAmount();
        if (fuelSpace > 0) {
            if (amountToDrain > fuelSpace) {
                amountToDrain = fuelSpace;
            }
            this.liquidTank.fill(FluidRegistry.getFluidStack("methane", amountToDrain), true);
        }
        else {
            amountToDrain = 0;
        }
        return amountToDrain;
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.processTicks = nbt.getInteger("smeltingTicks");
        this.containingItems = this.readStandardItemsFromNBT(nbt);
        if (nbt.hasKey("gasTank")) {
            this.gasTank.readFromNBT(nbt.getCompoundTag("gasTank"));
        }
        if (nbt.hasKey("gasTank2")) {
            this.gasTank2.readFromNBT(nbt.getCompoundTag("gasTank2"));
        }
        if (nbt.hasKey("liquidTank")) {
            this.liquidTank.readFromNBT(nbt.getCompoundTag("liquidTank"));
        }
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("smeltingTicks", this.processTicks);
        this.writeStandardItemsToNBT(nbt);
        if (this.gasTank.getFluid() != null) {
            nbt.setTag("gasTank", (NBTBase)this.gasTank.writeToNBT(new NBTTagCompound()));
        }
        if (this.gasTank2.getFluid() != null) {
            nbt.setTag("gasTank2", (NBTBase)this.gasTank2.writeToNBT(new NBTTagCompound()));
        }
        if (this.liquidTank.getFluid() != null) {
            nbt.setTag("liquidTank", (NBTBase)this.liquidTank.writeToNBT(new NBTTagCompound()));
        }
    }
    
    protected ItemStack[] getContainingItems() {
        return this.containingItems;
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public String getInventoryName() {
        return GCCoreUtil.translate("tile.marsMachine.5.name");
    }
    
    public int[] getAccessibleSlotsFromSide(final int side) {
        return new int[] { 0, 1, 2, 3, 4 };
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack itemstack, final int side) {
        if (!this.isItemValidForSlot(slotID, itemstack)) {
            return false;
        }
        switch (slotID) {
            case 0: {
                return ItemElectricBase.isElectricItemCharged(itemstack);
            }
            case 3: {
                return itemstack.getItem() == MarsItems.carbonFragments;
            }
            case 4: {
                return FluidUtil.isEmptyContainer(itemstack, AsteroidsItems.methaneCanister);
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
                return ItemElectricBase.isElectricItemEmpty(itemstack) || !this.shouldPullEnergy();
            }
            case 4: {
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
                return ItemElectricBase.isElectricItem(itemstack.getItem());
            }
            case 1: {
                return false;
            }
            case 2: {
                return itemstack.getItem() instanceof ItemAtmosphericValve;
            }
            case 3: {
                return itemstack.getItem() == MarsItems.carbonFragments;
            }
            case 4: {
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
    
    public double getPacketRange() {
        return 320.0;
    }
    
    public ForgeDirection getElectricInputDirection() {
        return ForgeDirection.DOWN;
    }
    
    public boolean canDrain(final ForgeDirection from, final Fluid fluid) {
        final int metaside = this.getBlockMetadata() + 2;
        final int side = from.ordinal();
        return side == (metaside ^ 0x1) && this.liquidTank.getFluid() != null && this.liquidTank.getFluidAmount() > 0;
    }
    
    public FluidStack drain(final ForgeDirection from, final FluidStack resource, final boolean doDrain) {
        final int metaside = this.getBlockMetadata() + 2;
        final int side = from.ordinal();
        if (side == (metaside ^ 0x1) && resource != null && resource.isFluidEqual(this.liquidTank.getFluid())) {
            return this.liquidTank.drain(resource.amount, doDrain);
        }
        return null;
    }
    
    public FluidStack drain(final ForgeDirection from, final int maxDrain, final boolean doDrain) {
        final int metaside = this.getBlockMetadata() + 2;
        final int side = from.ordinal();
        if (side == (metaside ^ 0x1)) {
            return this.liquidTank.drain(maxDrain, doDrain);
        }
        return null;
    }
    
    public boolean canFill(final ForgeDirection from, final Fluid fluid) {
        return from.ordinal() == this.getBlockMetadata() + 2 && fluid != null && "hydrogen".equals(fluid.getName());
    }
    
    public int fill(final ForgeDirection from, final FluidStack resource, final boolean doFill) {
        int used = 0;
        if (resource != null && this.canFill(from, resource.getFluid()) && this.gasTank.getFluidAmount() < this.gasTank.getCapacity()) {
            used = this.gasTank.fill(resource, doFill);
        }
        return used;
    }
    
    public FluidTankInfo[] getTankInfo(final ForgeDirection from) {
        FluidTankInfo[] tankInfo = new FluidTankInfo[0];
        if (from == ForgeDirection.getOrientation(this.getBlockMetadata() + 2)) {
            tankInfo = new FluidTankInfo[] { new FluidTankInfo((IFluidTank)this.gasTank) };
        }
        else if (from == ForgeDirection.getOrientation(this.getBlockMetadata() + 2).getOpposite()) {
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
        if (!stack.getGas().getName().equals("hydrogen")) {
            return 0;
        }
        int used = 0;
        if (this.gasTank.getFluidAmount() < this.gasTank.getCapacity()) {
            used = this.gasTank.fill(FluidRegistry.getFluidStack("hydrogen", stack.amount), doTransfer);
        }
        return used;
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public int receiveGas(final ForgeDirection side, final GasStack stack) {
        return this.receiveGas(side, stack, true);
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public GasStack drawGas(final ForgeDirection side, final int amount, final boolean doTransfer) {
        return null;
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public GasStack drawGas(final ForgeDirection side, final int amount) {
        return null;
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public boolean canReceiveGas(final ForgeDirection side, final Gas type) {
        return type.getName().equals("hydrogen") && side.equals((Object)ForgeDirection.getOrientation(this.getBlockMetadata() + 2));
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public boolean canDrawGas(final ForgeDirection side, final Gas type) {
        return false;
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.ITubeConnection", modID = "Mekanism")
    public boolean canTubeConnect(final ForgeDirection side) {
        return side.equals((Object)ForgeDirection.getOrientation(this.getBlockMetadata() + 2));
    }
    
    public boolean canConnect(final ForgeDirection direction, final NetworkType type) {
        if (direction == null || direction.equals((Object)ForgeDirection.UNKNOWN) || type == NetworkType.OXYGEN) {
            return false;
        }
        if (type == NetworkType.POWER) {
            return direction == this.getElectricInputDirection();
        }
        return direction.equals((Object)ForgeDirection.getOrientation(this.getBlockMetadata() + 2));
    }
    
    public Float getHydrogenRequest(final ForgeDirection direction) {
        return this.receiveHydrogen(direction, 1000000.0f, false);
    }
    
    public boolean shouldPullHydrogen() {
        return this.gasTank.getFluidAmount() < this.gasTank.getCapacity();
    }
    
    public float receiveHydrogen(final ForgeDirection from, final float receive, final boolean doReceive) {
        if (from.ordinal() == this.getBlockMetadata() + 2 && this.shouldPullHydrogen()) {
            final FluidStack fluidToFill = FluidRegistry.getFluidStack("hydrogen", (int)receive);
            return (float)this.gasTank.fill(fluidToFill, doReceive);
        }
        return 0.0f;
    }
}
