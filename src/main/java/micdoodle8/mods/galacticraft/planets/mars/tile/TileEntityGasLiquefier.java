package micdoodle8.mods.galacticraft.planets.mars.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.api.tile.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.material.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.*;
import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.world.*;
import java.util.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;
import net.minecraftforge.common.util.*;
import net.minecraft.util.*;
import net.minecraftforge.fluids.*;
import micdoodle8.mods.galacticraft.planets.asteroids.*;
import micdoodle8.mods.galacticraft.api.transmission.*;

public class TileEntityGasLiquefier extends TileBaseElectricBlockWithInventory implements ISidedInventory, IDisableableMachine, IFluidHandler, IOxygenReceiver
{
    private final int tankCapacity = 2000;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public FluidTank gasTank;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public FluidTank liquidTank;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public FluidTank liquidTank2;
    public int processTimeRequired;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int processTicks;
    private ItemStack[] containingItems;
    private int airProducts;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int gasTankType;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int fluidTankType;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int fluidTank2Type;

    public TileEntityGasLiquefier() {
        this.getClass();
        this.gasTank = new FluidTank(2000 * 2);
        this.getClass();
        this.liquidTank = new FluidTank(2000);
        this.getClass();
        this.liquidTank2 = new FluidTank(2000);
        this.processTimeRequired = 3;
        this.processTicks = -10;
        this.containingItems = new ItemStack[4];
        this.airProducts = -1;
        this.gasTankType = -1;
        this.fluidTankType = -1;
        this.fluidTank2Type = -1;
        this.storage.setMaxExtract(ConfigManagerCore.hardMode ? 90.0f : 60.0f);
        this.setTierGC(2);
    }

    public void invalidate() {
        super.invalidate();
    }

    public void updateEntity() {
        super.updateEntity();
        if (this.airProducts == -1) {
            this.airProducts = this.getAirProducts();
        }
        if (!this.worldObj.isRemote) {
            final FluidStack currentgas = this.gasTank.getFluid();
            if (currentgas == null || currentgas.amount <= 0) {
                this.gasTankType = -1;
            }
            else {
                this.gasTankType = this.getIdFromName(currentgas.getFluid().getName());
            }
            if (this.airProducts == 0 && this.gasTankType == TankGases.AIR.index) {
                this.gasTank.drain(this.gasTank.getFluidAmount(), true);
            }
            FluidStack currentLiquid = this.liquidTank.getFluid();
            if (currentLiquid == null || currentLiquid.amount == 0) {
                this.fluidTankType = -1;
            }
            else {
                this.fluidTankType = this.getProductIdFromName(currentLiquid.getFluid().getName());
            }
            currentLiquid = this.liquidTank2.getFluid();
            if (currentLiquid == null || currentLiquid.amount == 0) {
                this.fluidTank2Type = -1;
            }
            else {
                this.fluidTank2Type = this.getProductIdFromName(currentLiquid.getFluid().getName());
            }
            final ItemStack inputCanister = this.containingItems[1];
            if (inputCanister != null) {
                if (inputCanister.getItem() instanceof ItemAtmosphericValve && this.airProducts > 0) {
                    if (this.gasTankType == -1 || (this.gasTankType == TankGases.AIR.index && this.gasTank.getFluid().amount < this.gasTank.getCapacity())) {
                        final Block blockAbove = this.worldObj.getBlock(this.xCoord, this.yCoord + 1, this.zCoord);
                        if (blockAbove != null && blockAbove.getMaterial() == Material.air && blockAbove != GCBlocks.breatheableAir && blockAbove != GCBlocks.brightBreatheableAir) {
                            final FluidStack gcAtmosphere = FluidRegistry.getFluidStack(TankGases.AIR.gas, 4);
                            this.gasTank.fill(gcAtmosphere, true);
                            this.gasTankType = TankGases.AIR.index;
                        }
                    }
                }
                else if (inputCanister.getItem() instanceof ItemCanisterGeneric) {
                    final int amount = 1001 - inputCanister.getItemDamage();
                    if (amount > 0) {
                        final Item canisterType = inputCanister.getItem();
                        FluidStack canisterGas = null;
                        int factor = 1;
                        if (this.gasTankType <= 0 && canisterType == AsteroidsItems.methaneCanister) {
                            this.gasTankType = TankGases.METHANE.index;
                            canisterGas = FluidRegistry.getFluidStack(TankGases.METHANE.gas, amount);
                        }
                        if ((this.gasTankType == TankGases.OXYGEN.index || this.gasTankType == -1) && canisterType == AsteroidsItems.canisterLOX) {
                            this.gasTankType = TankGases.OXYGEN.index;
                            canisterGas = FluidRegistry.getFluidStack(TankGases.OXYGEN.gas, amount * 2);
                            factor = 2;
                        }
                        if ((this.gasTankType == TankGases.NITROGEN.index || this.gasTankType == -1) && canisterType == AsteroidsItems.canisterLN2) {
                            this.gasTankType = TankGases.NITROGEN.index;
                            canisterGas = FluidRegistry.getFluidStack(TankGases.NITROGEN.gas, amount * 2);
                            factor = 2;
                        }
                        if (canisterGas != null) {
                            final int used = this.gasTank.fill(canisterGas, true) / factor;
                            if (used == amount) {
                                this.containingItems[1] = new ItemStack(GCItems.oilCanister, 1, 1001);
                            }
                            else {
                                this.containingItems[1] = new ItemStack(canisterType, 1, 1001 - amount + used);
                            }
                        }
                    }
                }
                else {
                    final FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(inputCanister);
                    if (liquid != null && liquid.amount > 0) {
                        final String inputName = FluidRegistry.getFluidName(liquid);
                        if (this.gasTankType <= 0 && inputName.contains("methane")) {
                            if (currentgas == null || currentgas.amount + liquid.amount <= this.gasTank.getCapacity()) {
                                final FluidStack gcMethane = FluidRegistry.getFluidStack(TankGases.METHANE.gas, liquid.amount);
                                this.gasTank.fill(gcMethane, true);
                                this.gasTankType = 0;
                                this.containingItems[1] = FluidUtil.getUsedContainer(inputCanister);
                            }
                        }
                        else if ((this.gasTankType == TankGases.OXYGEN.index || this.gasTankType == -1) && inputName.contains("oxygen")) {
                            if (currentgas == null || currentgas.amount + liquid.amount * 2 <= this.gasTank.getCapacity()) {
                                final FluidStack gcgas = FluidRegistry.getFluidStack(TankGases.OXYGEN.gas, liquid.amount * (inputName.contains("liquid") ? 2 : 1));
                                this.gasTank.fill(gcgas, true);
                                this.gasTankType = TankGases.OXYGEN.index;
                                this.containingItems[1] = FluidUtil.getUsedContainer(inputCanister);
                            }
                        }
                        else if ((this.gasTankType == TankGases.NITROGEN.index || this.gasTankType == -1) && inputName.contains("nitrogen") && (currentgas == null || currentgas.amount + liquid.amount * 2 <= this.gasTank.getCapacity())) {
                            final FluidStack gcgas = FluidRegistry.getFluidStack(TankGases.NITROGEN.gas, liquid.amount * (inputName.contains("liquid") ? 2 : 1));
                            this.gasTank.fill(gcgas, true);
                            this.gasTankType = TankGases.NITROGEN.index;
                            this.containingItems[1] = FluidUtil.getUsedContainer(inputCanister);
                        }
                    }
                }
            }
            this.checkFluidTankTransfer(2, this.liquidTank);
            this.checkFluidTankTransfer(3, this.liquidTank2);
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
            else if (--this.processTicks <= -10) {
                this.processTicks = -10;
            }
        }
    }

    private void checkFluidTankTransfer(final int slot, final FluidTank tank) {
        if (FluidUtil.isValidContainer(this.containingItems[slot])) {
            final FluidStack liquid = tank.getFluid();
            if (liquid != null && liquid.amount > 0) {
                final String liquidname = liquid.getFluid().getName();
                if (liquidname.startsWith("fuel")) {
                    FluidUtil.tryFillContainerFuel(tank, this.containingItems, slot);
                }
                else if (liquidname.equals(TankGases.OXYGEN.liquid)) {
                    FluidUtil.tryFillContainer(tank, liquid, this.containingItems, slot, AsteroidsItems.canisterLOX);
                }
                else if (liquidname.equals(TankGases.NITROGEN.liquid)) {
                    FluidUtil.tryFillContainer(tank, liquid, this.containingItems, slot, AsteroidsItems.canisterLN2);
                }
            }
        }
        else if (this.containingItems[slot] != null && this.containingItems[slot].getItem() instanceof ItemAtmosphericValve) {
            tank.drain(4, true);
        }
    }

    public int getIdFromName(final String gasname) {
        for (final TankGases type : TankGases.values()) {
            if (type.gas.equals(gasname)) {
                return type.index;
            }
        }
        return -1;
    }

    public int getProductIdFromName(final String gasname) {
        for (final TankGases type : TankGases.values()) {
            if (type.liquid.equals(gasname)) {
                return type.index;
            }
        }
        return -1;
    }

    public int getScaledGasLevel(final int i) {
        return (this.gasTank.getFluid() != null) ? (this.gasTank.getFluid().amount * i / this.gasTank.getCapacity()) : 0;
    }

    public int getScaledFuelLevel(final int i) {
        return (this.liquidTank.getFluid() != null) ? (this.liquidTank.getFluid().amount * i / this.liquidTank.getCapacity()) : 0;
    }

    public int getScaledFuelLevel2(final int i) {
        return (this.liquidTank2.getFluid() != null) ? (this.liquidTank2.getFluid().amount * i / this.liquidTank2.getCapacity()) : 0;
    }

    public boolean canProcess() {
        if (this.gasTank.getFluid() == null || this.gasTank.getFluid().amount <= 0 || this.getDisabled(0)) {
            return false;
        }
        if (this.fluidTankType == -1 || this.fluidTank2Type == -1) {
            return true;
        }
        final boolean tank1HasSpace = this.liquidTank.getFluidAmount() < this.liquidTank.getCapacity();
        final boolean tank2HasSpace = this.liquidTank2.getFluidAmount() < this.liquidTank2.getCapacity();
        if (this.gasTankType == TankGases.AIR.index) {
            int airProducts = this.airProducts;
            do {
                final int thisProduct = (airProducts & 0xF) - 1;
                if ((thisProduct == this.fluidTankType && tank1HasSpace) || (thisProduct == this.fluidTank2Type && tank2HasSpace)) {
                    return true;
                }
                airProducts >>= 4;
            } while (airProducts > 0);
            return false;
        }
        return (this.gasTankType == this.fluidTankType && tank1HasSpace) || (this.gasTankType == this.fluidTank2Type && tank2HasSpace);
    }

    public int getAirProducts() {
        final WorldProvider WP = this.worldObj.provider;
        if (WP instanceof WorldProviderSpace) {
            int result = 0;
            final ArrayList<IAtmosphericGas> atmos = (ArrayList<IAtmosphericGas>)((WorldProviderSpace)WP).getCelestialBody().atmosphere;
            if (atmos.size() > 0) {
                result = this.getIdFromName(atmos.get(0).name().toLowerCase()) + 1;
            }
            if (atmos.size() > 1) {
                result += 16 * (this.getIdFromName(atmos.get(1).name().toLowerCase()) + 1);
            }
            if (atmos.size() > 2) {
                result += 256 * (this.getIdFromName(atmos.get(2).name().toLowerCase()) + 1);
            }
            return result;
        }
        return 35;
    }

    public void doLiquefaction() {
        final int gasAmount = this.gasTank.getFluid().amount;
        if (gasAmount == 0) {
            return;
        }
        if (this.gasTankType == TankGases.AIR.index) {
            int airProducts = this.airProducts;
            int amountToDrain = Math.min(gasAmount / 2, (airProducts > 15) ? 2 : 3);
            if (amountToDrain == 0) {
                amountToDrain = 1;
            }
            do {
                final int thisProduct = (airProducts & 0xF) - 1;
                if (thisProduct >= 0) {
                    this.gasTank.drain(this.placeIntoFluidTanks(thisProduct, amountToDrain) * 2, true);
                }
                airProducts >>= 4;
                amountToDrain >>= 1;
                if (amountToDrain == 0) {
                    amountToDrain = 1;
                }
            } while (airProducts > 0);
        }
        else if (gasAmount == 1) {
            this.gasTank.drain(this.placeIntoFluidTanks(this.gasTankType, 1), true);
        }
        else {
            this.gasTank.drain(this.placeIntoFluidTanks(this.gasTankType, Math.min(gasAmount / 2, 3)) * 2, true);
        }
    }

    private int placeIntoFluidTanks(final int thisProduct, int amountToDrain) {
        final int fuelSpace = this.liquidTank.getCapacity() - this.liquidTank.getFluidAmount();
        final int fuelSpace2 = this.liquidTank2.getCapacity() - this.liquidTank2.getFluidAmount();
        if ((thisProduct == this.fluidTank2Type || this.fluidTank2Type == -1) && fuelSpace2 > 0) {
            if (amountToDrain > fuelSpace2) {
                amountToDrain = fuelSpace2;
            }
            this.liquidTank2.fill(FluidRegistry.getFluidStack(TankGases.values()[thisProduct].liquid, amountToDrain), true);
            this.fluidTank2Type = thisProduct;
        }
        else if ((thisProduct == this.fluidTankType || this.fluidTankType == -1) && fuelSpace > 0) {
            if (amountToDrain > fuelSpace) {
                amountToDrain = fuelSpace;
            }
            this.liquidTank.fill(FluidRegistry.getFluidStack(TankGases.values()[thisProduct].liquid, amountToDrain), true);
            this.fluidTankType = thisProduct;
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
        if (nbt.hasKey("liquidTank")) {
            this.liquidTank.readFromNBT(nbt.getCompoundTag("liquidTank"));
        }
        if (nbt.hasKey("liquidTank2")) {
            this.liquidTank2.readFromNBT(nbt.getCompoundTag("liquidTank2"));
        }
    }

    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("smeltingTicks", this.processTicks);
        this.writeStandardItemsToNBT(nbt);
        if (this.gasTank.getFluid() != null) {
            nbt.setTag("gasTank", (NBTBase)this.gasTank.writeToNBT(new NBTTagCompound()));
        }
        if (this.liquidTank.getFluid() != null) {
            nbt.setTag("liquidTank", (NBTBase)this.liquidTank.writeToNBT(new NBTTagCompound()));
        }
        if (this.liquidTank2.getFluid() != null) {
            nbt.setTag("liquidTank2", (NBTBase)this.liquidTank2.writeToNBT(new NBTTagCompound()));
        }
    }

    protected ItemStack[] getContainingItems() {
        return this.containingItems;
    }

    public boolean hasCustomInventoryName() {
        return true;
    }

    public String getInventoryName() {
        return GCCoreUtil.translate("tile.marsMachine.4.name");
    }

    public int[] getAccessibleSlotsFromSide(final int side) {
        if (side == 0) {
            return new int[] { 0, 1, 2, 3 };
        }
        if (side > 1) {
            return new int[] { 1, 2, 3 };
        }
        return new int[] { 0 };
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
                return FluidUtil.isMethaneContainerAny(itemstack);
            }
            case 2: {
                return FluidUtil.isEmptyContainerFor(itemstack, this.liquidTank.getFluid());
            }
            case 3: {
                return FluidUtil.isEmptyContainerFor(itemstack, this.liquidTank2.getFluid());
            }
            default: {
                return false;
            }
        }
    }

    public boolean canExtractItem(final int slotID, final ItemStack itemstack, final int side) {
        switch (slotID) {
            case 0: {
                return ItemElectricBase.isElectricItemEmpty(itemstack);
            }
            case 1: {
                return FluidUtil.isEmptyContainer(itemstack);
            }
            case 2: {
                return FluidUtil.isFullContainer(itemstack);
            }
            case 3: {
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
            case 1:
            case 2:
            case 3: {
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
        return from.equals((Object)ForgeDirection.getOrientation(this.getBlockMetadata() + 2)) && fluid != null && this.getIdFromName(fluid.getName()) > -1;
    }

    public int fill(final ForgeDirection from, final FluidStack resource, final boolean doFill) {
        int used = 0;
        if (resource != null && this.canFill(from, resource.getFluid())) {
            final int type = this.getIdFromName(FluidRegistry.getFluidName(resource));
            if (this.gasTankType == -1 || (this.gasTankType == type && this.gasTank.getFluidAmount() < this.gasTank.getCapacity())) {
                if (type > 0) {
                    final float conversion = 0.18518518f;
                    final FluidStack fluidToFill = new FluidStack(resource.getFluid(), (int)(resource.amount * conversion));
                    used = MathHelper.ceiling_float_int(this.gasTank.fill(fluidToFill, doFill) / conversion);
                }
                else {
                    used = this.gasTank.fill(resource, doFill);
                }
            }
        }
        return used;
    }

    public FluidTankInfo[] getTankInfo(final ForgeDirection from) {
        FluidTankInfo[] tankInfo = new FluidTankInfo[0];
        final int metaside = this.getBlockMetadata() + 2;
        final int side = from.ordinal();
        if (metaside == side) {
            tankInfo = new FluidTankInfo[] { new FluidTankInfo((IFluidTank)this.gasTank) };
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

    public boolean shouldPullOxygen() {
        return this.gasTankType == -1 || (this.gasTankType == 1 && this.gasTank.getFluidAmount() < this.gasTank.getCapacity());
    }

    public float receiveOxygen(final ForgeDirection from, final float receive, final boolean doReceive) {
        if (from.ordinal() == this.getBlockMetadata() + 2 && this.shouldPullOxygen()) {
            final float conversion = 0.18518518f;
            final FluidStack fluidToFill = new FluidStack(AsteroidsModule.fluidOxygenGas, (int)(receive * conversion));
            final int used = MathHelper.ceiling_float_int(this.gasTank.fill(fluidToFill, doReceive) / conversion);
            return (float)used;
        }
        return 0.0f;
    }

    public float provideOxygen(final ForgeDirection from, final float request, final boolean doProvide) {
        return 0.0f;
    }

    public float getOxygenRequest(final ForgeDirection direction) {
        return this.receiveOxygen(direction, 1000000.0f, false);
    }

    public float getOxygenProvide(final ForgeDirection direction) {
        return 0.0f;
    }

    public boolean canConnect(final ForgeDirection direction, final NetworkType type) {
        if (direction == null || direction.equals((Object)ForgeDirection.UNKNOWN)) {
            return false;
        }
        if (type == NetworkType.OXYGEN) {
            return direction.ordinal() == this.getBlockMetadata() + 2;
        }
        return type == NetworkType.POWER && direction == ForgeDirection.DOWN;
    }

    public enum TankGases
    {
        METHANE(0, "methane", ConfigManagerCore.useOldFuelFluidID ? "fuelgc" : "fuel"),
        OXYGEN(1, "oxygen", "liquidoxygen"),
        NITROGEN(2, "nitrogen", "liquidnitrogen"),
        ARGON(3, "argon", "liquidargon"),
        AIR(4, "atmosphericgases", "xxyyzz");

        int index;
        String gas;
        String liquid;

        private TankGases(final int id, final String fluidname, final String outputname) {
            this.index = id;
            this.gas = new String(fluidname);
            this.liquid = new String(outputname);
        }
    }
}
