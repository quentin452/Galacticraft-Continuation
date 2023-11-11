package micdoodle8.mods.galacticraft.core.energy.tile;

import java.util.*;
import net.minecraftforge.common.util.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.api.item.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import ic2.api.item.*;
import net.minecraft.item.*;
import micdoodle8.mods.miccore.*;
import cofh.api.energy.*;
import mekanism.api.energy.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraftforge.common.*;
import java.lang.reflect.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import buildcraft.api.power.*;
import net.minecraft.world.*;
import buildcraft.api.mj.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.tile.*;

public abstract class TileBaseUniversalElectrical extends EnergyStorageTile
{
    protected boolean isAddedToEnergyNet;
    protected Object powerHandlerBC;
    private float IC2surplusInGJ;

    public TileBaseUniversalElectrical() {
        this.IC2surplusInGJ = 0.0f;
    }

    public double getPacketRange() {
        return 12.0;
    }

    public int getPacketCooldown() {
        return 3;
    }

    public boolean isNetworkedTile() {
        return true;
    }

    public EnumSet<ForgeDirection> getElectricalInputDirections() {
        return EnumSet.allOf(ForgeDirection.class);
    }

    public EnumSet<ForgeDirection> getElectricalOutputDirections() {
        return EnumSet.noneOf(ForgeDirection.class);
    }

    public float getRequest(final ForgeDirection direction) {
        if (this.getElectricalInputDirections().contains(direction) || direction == ForgeDirection.UNKNOWN) {
            return super.getRequest(direction);
        }
        return 0.0f;
    }

    public float receiveElectricity(final ForgeDirection from, final float receive, final int tier, final boolean doReceive) {
        if (this.getElectricalInputDirections().contains(from) || from == ForgeDirection.UNKNOWN) {
            return super.receiveElectricity(from, receive, tier, doReceive);
        }
        return 0.0f;
    }

    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }

    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
    }

    @Annotations.VersionSpecific(version = "[1.7.2]")
    public void discharge(final ItemStack itemStack) {
        if (itemStack != null) {
            final Item item = itemStack.getItem();
            final float energyToDischarge = this.getRequest(ForgeDirection.UNKNOWN);
            if (item instanceof IItemElectric) {
                this.storage.receiveEnergyGC(ElectricItemHelper.dischargeItem(itemStack, energyToDischarge));
            }
            else if (EnergyConfigHandler.isIndustrialCraft2Loaded()) {
                if (item instanceof IElectricItem) {
                    final IElectricItem electricItem = (IElectricItem)item;
                    if (electricItem.canProvideEnergy(itemStack)) {
                        double result = 0.0;
                        final int energyDischargeIC2 = (int)(energyToDischarge / EnergyConfigHandler.IC2_RATIO);
                        try {
                            final Class<?> clazz = Class.forName("ic2.api.item.IElectricItemManager");
                            final Method dischargeMethod = clazz.getMethod("discharge", ItemStack.class, Integer.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE);
                            result = (int)dischargeMethod.invoke(ElectricItem.manager, itemStack, energyDischargeIC2, 4, false, false);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        final float energyDischarged = (float)result * EnergyConfigHandler.IC2_RATIO;
                        this.storage.receiveEnergyGC(energyDischarged);
                    }
                }
                else if (item instanceof ISpecialElectricItem) {
                    final ISpecialElectricItem electricItem2 = (ISpecialElectricItem)item;
                    if (electricItem2.canProvideEnergy(itemStack)) {
                        double result = 0.0;
                        final int energyDischargeIC2 = (int)(energyToDischarge / EnergyConfigHandler.IC2_RATIO);
                        try {
                            final Class<?> clazz = Class.forName("ic2.api.item.IElectricItemManager");
                            final Method dischargeMethod = clazz.getMethod("discharge", ItemStack.class, Integer.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE);
                            result = (int)dischargeMethod.invoke(electricItem2.getManager(itemStack), itemStack, energyDischargeIC2, 4, false, false);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        final float energyDischarged = (float)result * EnergyConfigHandler.IC2_RATIO;
                        this.storage.receiveEnergyGC(energyDischarged);
                    }
                }
            }
        }
    }

    @Annotations.AltForVersion(version = "[1.7.10]")
    public void dischargeB(final ItemStack itemStack) {
        if (itemStack != null) {
            final Item item = itemStack.getItem();
            final float energyToDischarge = this.getRequest(ForgeDirection.UNKNOWN);
            if (item instanceof IItemElectric) {
                this.storage.receiveEnergyGC(ElectricItemHelper.dischargeItem(itemStack, energyToDischarge));
            }
            else if (EnergyConfigHandler.isRFAPILoaded() && item instanceof IEnergyContainerItem) {
                this.storage.receiveEnergyGC(((IEnergyContainerItem)item).extractEnergy(itemStack, (int)(energyToDischarge / EnergyConfigHandler.RF_RATIO), false) * EnergyConfigHandler.RF_RATIO);
            }
            else if (EnergyConfigHandler.isMekanismLoaded() && item instanceof IEnergizedItem && ((IEnergizedItem)item).canSend(itemStack)) {
                this.storage.receiveEnergyGC((float)EnergizedItemManager.discharge(itemStack, (double)(energyToDischarge / EnergyConfigHandler.MEKANISM_RATIO)) * EnergyConfigHandler.MEKANISM_RATIO);
            }
            else if (EnergyConfigHandler.isIndustrialCraft2Loaded()) {
                if (item instanceof IElectricItem) {
                    final IElectricItem electricItem = (IElectricItem)item;
                    if (electricItem.canProvideEnergy(itemStack)) {
                        double result = 0.0;
                        final double energyDischargeIC2 = energyToDischarge / EnergyConfigHandler.IC2_RATIO;
                        try {
                            final Class<?> clazz = Class.forName("ic2.api.item.IElectricItemManager");
                            final Method dischargeMethod = clazz.getMethod("discharge", ItemStack.class, Double.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE);
                            result = (double)dischargeMethod.invoke(ElectricItem.manager, itemStack, energyDischargeIC2, 4, false, false, false);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        final float energyDischarged = (float)result * EnergyConfigHandler.IC2_RATIO;
                        this.storage.receiveEnergyGC(energyDischarged);
                    }
                }
                else if (item instanceof ISpecialElectricItem) {
                    final ISpecialElectricItem electricItem2 = (ISpecialElectricItem)item;
                    if (electricItem2.canProvideEnergy(itemStack)) {
                        double result = 0.0;
                        final double energyDischargeIC2 = energyToDischarge / EnergyConfigHandler.IC2_RATIO;
                        try {
                            final Class<?> clazz = Class.forName("ic2.api.item.IElectricItemManager");
                            final Method dischargeMethod = clazz.getMethod("discharge", ItemStack.class, Double.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE);
                            result = (double)dischargeMethod.invoke(electricItem2.getManager(itemStack), itemStack, energyDischargeIC2, 4, false, false, false);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        final float energyDischarged = (float)result * EnergyConfigHandler.IC2_RATIO;
                        this.storage.receiveEnergyGC(energyDischarged);
                    }
                }
            }
        }
    }

    public void initiate() {
        super.initiate();
        if (EnergyConfigHandler.isBuildcraftLoaded()) {
            this.initBuildCraft();
        }
    }

    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            if (!this.isAddedToEnergyNet) {
                this.initIC();
            }
            if (EnergyConfigHandler.isIndustrialCraft2Loaded() && this.IC2surplusInGJ >= 0.001f) {
                this.IC2surplusInGJ -= this.storage.receiveEnergyGC(this.IC2surplusInGJ);
                if (this.IC2surplusInGJ < 0.001f) {
                    this.IC2surplusInGJ = 0.0f;
                }
            }
            if (EnergyConfigHandler.isBuildcraftLoaded()) {
                if (this.powerHandlerBC == null) {
                    this.initBuildCraft();
                }
                final PowerHandler handler = (PowerHandler)this.powerHandlerBC;
                double energyBC = handler.getEnergyStored();
                if (energyBC > 0.0) {
                    final float usedBC = this.storage.receiveEnergyGC((float)energyBC * EnergyConfigHandler.BC3_RATIO) / EnergyConfigHandler.BC3_RATIO;
                    energyBC -= usedBC;
                    if (energyBC < 0.0) {
                        energyBC = 0.0;
                    }
                    handler.setEnergy(energyBC);
                }
            }
        }
    }

    public void invalidate() {
        this.unloadTileIC2();
        super.invalidate();
    }

    public void onChunkUnload() {
        this.unloadTileIC2();
        super.onChunkUnload();
    }

    protected void initIC() {
        if (EnergyConfigHandler.isIndustrialCraft2Loaded()) {
            try {
                final Class<?> tileLoadEvent = Class.forName("ic2.api.energy.event.EnergyTileLoadEvent");
                final Class<?> energyTile = Class.forName("ic2.api.energy.tile.IEnergyTile");
                final Constructor<?> constr = tileLoadEvent.getConstructor(energyTile);
                final Object o = constr.newInstance(this);
                if (o != null && o instanceof Event) {
                    MinecraftForge.EVENT_BUS.post((Event)o);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.isAddedToEnergyNet = true;
    }

    private void unloadTileIC2() {
        if (this.isAddedToEnergyNet && this.worldObj != null) {
            if (EnergyConfigHandler.isIndustrialCraft2Loaded() && !this.worldObj.isRemote) {
                try {
                    final Class<?> tileLoadEvent = Class.forName("ic2.api.energy.event.EnergyTileUnloadEvent");
                    final Class<?> energyTile = Class.forName("ic2.api.energy.tile.IEnergyTile");
                    final Constructor<?> constr = tileLoadEvent.getConstructor(energyTile);
                    final Object o = constr.newInstance(this);
                    if (o != null && o instanceof Event) {
                        MinecraftForge.EVENT_BUS.post((Event)o);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.isAddedToEnergyNet = false;
        }
    }

    @Annotations.VersionSpecific(version = "[1.7.10]")
    @Annotations.RuntimeInterface(clazz = "ic2.api.energy.tile.IEnergySink", modID = "IC2")
    public double getDemandedEnergy() {
        if (EnergyConfigHandler.disableIC2Input) {
            return 0.0;
        }
        try {
            if (this.IC2surplusInGJ < 0.001f) {
                this.IC2surplusInGJ = 0.0f;
                return Math.ceil(this.storage.receiveEnergyGC(2.14748365E9f, true) / EnergyConfigHandler.IC2_RATIO);
            }
            final float received = this.storage.receiveEnergyGC(this.IC2surplusInGJ, true);
            if (received == this.IC2surplusInGJ) {
                return Math.ceil((this.storage.receiveEnergyGC(2.14748365E9f, true) - this.IC2surplusInGJ) / EnergyConfigHandler.IC2_RATIO);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @Annotations.VersionSpecific(version = "[1.7.2]")
    @Annotations.RuntimeInterface(clazz = "ic2.api.energy.tile.IEnergySink", modID = "IC2")
    public double demandedEnergyUnits() {
        if (EnergyConfigHandler.disableIC2Input) {
            return 0.0;
        }
        try {
            if (this.IC2surplusInGJ < 0.001f) {
                this.IC2surplusInGJ = 0.0f;
                return Math.ceil(this.storage.receiveEnergyGC(2.14748365E9f, true) / EnergyConfigHandler.IC2_RATIO);
            }
            final float received = this.storage.receiveEnergyGC(this.IC2surplusInGJ, true);
            if (received == this.IC2surplusInGJ) {
                return Math.ceil((this.storage.receiveEnergyGC(2.14748365E9f, true) - this.IC2surplusInGJ) / EnergyConfigHandler.IC2_RATIO);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @Annotations.VersionSpecific(version = "[1.7.10]")
    @Annotations.RuntimeInterface(clazz = "ic2.api.energy.tile.IEnergySink", modID = "IC2")
    public double injectEnergy(final ForgeDirection direction, final double amount, final double voltage) {
        if (!EnergyConfigHandler.disableIC2Input && (direction == ForgeDirection.UNKNOWN || this.getElectricalInputDirections().contains(direction))) {
            final float convertedEnergy = (float)amount * EnergyConfigHandler.IC2_RATIO;
            final int tierFromIC2 = ((int)voltage > 120) ? 2 : 1;
            final float receive = this.receiveElectricity(direction, convertedEnergy, tierFromIC2, true);
            if (convertedEnergy > receive) {
                this.IC2surplusInGJ = convertedEnergy - receive;
            }
            else {
                this.IC2surplusInGJ = 0.0f;
            }
            return 0.0;
        }
        return amount;
    }

    @Annotations.VersionSpecific(version = "[1.7.2]")
    @Annotations.RuntimeInterface(clazz = "ic2.api.energy.tile.IEnergySink", modID = "IC2")
    public double injectEnergyUnits(final ForgeDirection direction, final double amount) {
        if (!EnergyConfigHandler.disableIC2Input && (direction == ForgeDirection.UNKNOWN || this.getElectricalInputDirections().contains(direction))) {
            final float convertedEnergy = (float)amount * EnergyConfigHandler.IC2_RATIO;
            final int tierFromIC2 = (amount >= 128.0) ? 2 : 1;
            final float receive = this.receiveElectricity(direction, convertedEnergy, tierFromIC2, true);
            if (convertedEnergy > receive) {
                this.IC2surplusInGJ = convertedEnergy - receive;
            }
            else {
                this.IC2surplusInGJ = 0.0f;
            }
            return 0.0;
        }
        return amount;
    }

    @Annotations.VersionSpecific(version = "[1.7.10]")
    @Annotations.RuntimeInterface(clazz = "ic2.api.energy.tile.IEnergySink", modID = "IC2")
    public int getSinkTier() {
        return 3;
    }

    @Annotations.VersionSpecific(version = "[1.7.2]")
    @Annotations.RuntimeInterface(clazz = "ic2.api.energy.tile.IEnergySink", modID = "IC2")
    public double getMaxSafeInput() {
        return 2.147483647E9;
    }

    @Annotations.RuntimeInterface(clazz = "ic2.api.energy.tile.IEnergyAcceptor", modID = "IC2")
    public boolean acceptsEnergyFrom(final TileEntity emitter, final ForgeDirection direction) {
        if (emitter instanceof IElectrical || emitter instanceof IConductor) {
            return false;
        }
        try {
            final Class<?> energyTile = Class.forName("ic2.api.energy.tile.IEnergyTile");
            if (!energyTile.isInstance(emitter)) {
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return this.getElectricalInputDirections().contains(direction);
    }

    public void initBuildCraft() {
        if (this.powerHandlerBC == null) {
            this.powerHandlerBC = new PowerHandler((IPowerReceptor)this, PowerHandler.Type.MACHINE);
        }
        float receive = this.storage.receiveEnergyGC(this.storage.getMaxReceive(), true) / EnergyConfigHandler.BC3_RATIO;
        if (receive < 0.1f) {
            receive = 0.0f;
        }
        ((PowerHandler)this.powerHandlerBC).configure(0.0, (double)receive, 0.0, (double)(int)(this.getMaxEnergyStoredGC() / EnergyConfigHandler.BC3_RATIO));
        ((PowerHandler)this.powerHandlerBC).configurePowerPerdition(1, 10);
    }

    @Annotations.RuntimeInterface(clazz = "buildcraft.api.power.IPowerReceptor", modID = "")
    public PowerHandler.PowerReceiver getPowerReceiver(final ForgeDirection side) {
        if (this.getElectricalInputDirections().contains(side)) {
            this.initBuildCraft();
            return ((PowerHandler)this.powerHandlerBC).getPowerReceiver();
        }
        return null;
    }

    @Annotations.RuntimeInterface(clazz = "buildcraft.api.power.IPowerReceptor", modID = "")
    public void doWork(final PowerHandler workProvider) {
        this.initBuildCraft();
    }

    @Annotations.RuntimeInterface(clazz = "buildcraft.api.power.IPowerReceptor", modID = "")
    public World getWorld() {
        return this.getWorldObj();
    }

    @Annotations.RuntimeInterface(clazz = "buildcraft.api.mj.ISidedBatteryProvider", modID = "")
    public IBatteryObject getMjBattery(final String kind, final ForgeDirection direction) {
        if (this.getElectricalInputDirections().contains(direction)) {
            return (IBatteryObject)this;
        }
        return null;
    }

    @Annotations.RuntimeInterface(clazz = "buildcraft.api.mj.IBatteryObject", modID = "")
    public double getEnergyRequested() {
        if (EnergyConfigHandler.disableBuildCraftInput) {
            return 0.0;
        }
        float requested = this.getRequest(ForgeDirection.UNKNOWN) / EnergyConfigHandler.BC3_RATIO;
        if (requested < 0.1f) {
            requested = 0.0f;
        }
        return requested;
    }

    @Annotations.RuntimeInterface(clazz = "buildcraft.api.mj.IBatteryObject", modID = "")
    public double addEnergy(final double mj) {
        final float convertedEnergy = (float)mj * EnergyConfigHandler.BC3_RATIO;
        final float used = this.receiveElectricity(ForgeDirection.UNKNOWN, convertedEnergy, 1, true);
        return used / EnergyConfigHandler.BC3_RATIO;
    }

    @Annotations.RuntimeInterface(clazz = "buildcraft.api.mj.IBatteryObject", modID = "")
    public double addEnergy(final double mj, final boolean ignoreCycleLimit) {
        final float convertedEnergy = (float)mj * EnergyConfigHandler.BC3_RATIO;
        final float used = this.receiveElectricity(ForgeDirection.UNKNOWN, convertedEnergy, 1, true);
        return used / EnergyConfigHandler.BC3_RATIO;
    }

    @Annotations.RuntimeInterface(clazz = "buildcraft.api.mj.IBatteryObject", modID = "")
    public double getEnergyStored() {
        return this.getEnergyStoredGC() / EnergyConfigHandler.BC3_RATIO;
    }

    @Annotations.RuntimeInterface(clazz = "buildcraft.api.mj.IBatteryObject", modID = "")
    public void setEnergyStored(final double mj) {
    }

    @Annotations.RuntimeInterface(clazz = "buildcraft.api.mj.IBatteryObject", modID = "")
    public double maxCapacity() {
        return this.getMaxEnergyStoredGC() / EnergyConfigHandler.BC3_RATIO;
    }

    @Annotations.RuntimeInterface(clazz = "buildcraft.api.mj.IBatteryObject", modID = "")
    public double minimumConsumption() {
        return this.storage.getMaxReceive() / EnergyConfigHandler.BC3_RATIO;
    }

    @Annotations.RuntimeInterface(clazz = "buildcraft.api.mj.IBatteryObject", modID = "")
    public double maxReceivedPerCycle() {
        return (this.getMaxEnergyStoredGC() - this.getEnergyStoredGC()) / EnergyConfigHandler.BC3_RATIO;
    }

    @Annotations.RuntimeInterface(clazz = "buildcraft.api.mj.IBatteryObject", modID = "")
    public IBatteryObject reconfigure(final double maxCapacity, final double maxReceivedPerCycle, final double minimumConsumption) {
        return (IBatteryObject)this;
    }

    @Annotations.RuntimeInterface(clazz = "buildcraft.api.mj.IBatteryObject", modID = "")
    public String kind() {
        return "buildcraft.kinesis";
    }

    @Annotations.RuntimeInterface(clazz = "cofh.api.energy.IEnergyReceiver", modID = "")
    public int receiveEnergy(final ForgeDirection from, final int maxReceive, final boolean simulate) {
        if (EnergyConfigHandler.disableRFInput) {
            return 0;
        }
        if (!this.getElectricalInputDirections().contains(from)) {
            return 0;
        }
        return MathHelper.floor_float(super.receiveElectricity(from, maxReceive * EnergyConfigHandler.RF_RATIO, 1, !simulate) / EnergyConfigHandler.RF_RATIO);
    }

    @Annotations.RuntimeInterface(clazz = "cofh.api.energy.IEnergyHandler", modID = "")
    public boolean canConnectEnergy(final ForgeDirection from) {
        return this.getElectricalInputDirections().contains(from) || this.getElectricalOutputDirections().contains(from);
    }

    @Annotations.RuntimeInterface(clazz = "cofh.api.energy.IEnergyHandler", modID = "")
    public int getEnergyStored(final ForgeDirection from) {
        return MathHelper.floor_float(this.getEnergyStoredGC() / EnergyConfigHandler.RF_RATIO);
    }

    @Annotations.RuntimeInterface(clazz = "cofh.api.energy.IEnergyHandler", modID = "")
    public int getMaxEnergyStored(final ForgeDirection from) {
        return MathHelper.floor_float(this.getMaxEnergyStoredGC() / EnergyConfigHandler.RF_RATIO);
    }

    @Annotations.RuntimeInterface(clazz = "cofh.api.energy.IEnergyHandler", modID = "")
    public int extractEnergy(final ForgeDirection from, final int maxExtract, final boolean simulate) {
        return 0;
    }

    @Annotations.RuntimeInterface(clazz = "mekanism.api.energy.IStrictEnergyAcceptor", modID = "Mekanism")
    public double transferEnergyToAcceptor(final ForgeDirection from, final double amount) {
        if (EnergyConfigHandler.disableMekanismInput) {
            return 0.0;
        }
        if (!this.getElectricalInputDirections().contains(from)) {
            return 0.0;
        }
        return this.receiveElectricity(from, (float)amount * EnergyConfigHandler.MEKANISM_RATIO, 1, true) / EnergyConfigHandler.MEKANISM_RATIO;
    }

    @Annotations.RuntimeInterface(clazz = "mekanism.api.energy.IStrictEnergyAcceptor", modID = "Mekanism")
    public boolean canReceiveEnergy(final ForgeDirection side) {
        return this.getElectricalInputDirections().contains(side);
    }

    @Annotations.RuntimeInterface(clazz = "mekanism.api.energy.IStrictEnergyAcceptor", modID = "Mekanism")
    public double getEnergy() {
        if (EnergyConfigHandler.disableMekanismInput) {
            return 0.0;
        }
        return this.getEnergyStoredGC() / EnergyConfigHandler.MEKANISM_RATIO;
    }

    @Annotations.RuntimeInterface(clazz = "mekanism.api.energy.IStrictEnergyAcceptor", modID = "Mekanism")
    public void setEnergy(final double energy) {
        if (EnergyConfigHandler.disableMekanismInput) {
            return;
        }
        this.storage.setEnergyStored((float)energy * EnergyConfigHandler.MEKANISM_RATIO);
    }

    @Annotations.RuntimeInterface(clazz = "mekanism.api.energy.IStrictEnergyAcceptor", modID = "Mekanism")
    public double getMaxEnergy() {
        if (EnergyConfigHandler.disableMekanismInput) {
            return 0.0;
        }
        return this.getMaxEnergyStoredGC() / EnergyConfigHandler.MEKANISM_RATIO;
    }

    @Annotations.RuntimeInterface(clazz = "mekanism.api.energy.ICableOutputter", modID = "Mekanism")
    public boolean canOutputTo(final ForgeDirection side) {
        return false;
    }

    public ReceiverMode getModeFromDirection(final ForgeDirection direction) {
        if (this.getElectricalInputDirections().contains(direction)) {
            return ReceiverMode.RECEIVE;
        }
        if (this.getElectricalOutputDirections().contains(direction)) {
            return ReceiverMode.EXTRACT;
        }
        return null;
    }

    public void updateFacing() {
        if (EnergyConfigHandler.isIndustrialCraft2Loaded() && !this.worldObj.isRemote) {
            this.unloadTileIC2();
        }
    }
}
