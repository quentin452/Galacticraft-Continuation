package micdoodle8.mods.galacticraft.core.energy.tile;

import buildcraft.api.power.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraftforge.common.*;
import java.lang.reflect.*;
import micdoodle8.mods.galacticraft.core.energy.grid.*;
import micdoodle8.mods.miccore.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import ic2.api.energy.tile.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import net.minecraft.world.*;
import net.minecraft.util.*;

public abstract class TileBaseUniversalConductor extends TileBaseConductor
{
    protected boolean isAddedToEnergyNet;
    protected Object powerHandlerBC;
    private float IC2surplusJoules;
    
    public TileBaseUniversalConductor() {
        this.IC2surplusJoules = 0.0f;
        this.initBC();
    }
    
    public void onNetworkChanged() {
    }
    
    private void initBC() {
        if (EnergyConfigHandler.isBuildcraftLoaded() && this instanceof IPowerReceptor) {
            this.powerHandlerBC = new PowerHandler((IPowerReceptor)this, PowerHandler.Type.PIPE);
            ((PowerHandler)this.powerHandlerBC).configurePowerPerdition(0, 0);
            ((PowerHandler)this.powerHandlerBC).configure(0.0, 0.0, 0.0, 0.0);
        }
    }
    
    public TileEntity[] getAdjacentConnections() {
        return EnergyUtil.getAdjacentPowerConnections((TileEntity)this);
    }
    
    public boolean canUpdate() {
        return EnergyConfigHandler.isIndustrialCraft2Loaded();
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (!this.isAddedToEnergyNet) {
            if (!this.worldObj.isRemote) {
                this.initIC();
            }
            this.isAddedToEnergyNet = true;
        }
    }
    
    public void invalidate() {
        this.IC2surplusJoules = 0.0f;
        this.unloadTileIC2();
        super.invalidate();
    }
    
    public void onChunkUnload() {
        this.unloadTileIC2();
        super.onChunkUnload();
    }
    
    protected void initIC() {
        if (EnergyConfigHandler.isIndustrialCraft2Loaded() && !this.worldObj.isRemote) {
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
    }
    
    private void unloadTileIC2() {
        if (this.isAddedToEnergyNet && this.worldObj != null) {
            if (!this.worldObj.isRemote && EnergyConfigHandler.isIndustrialCraft2Loaded()) {
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
    
    @Annotations.VersionSpecific(version = "[1.7.2]")
    @Annotations.RuntimeInterface(clazz = "ic2.api.energy.tile.IEnergySink", modID = "IC2")
    public double demandedEnergyUnits() {
        if (this.getNetwork() == null) {
            return 0.0;
        }
        if (this.IC2surplusJoules < 0.001f) {
            this.IC2surplusJoules = 0.0f;
            float result = this.getNetwork().getRequest(new TileEntity[] { (TileEntity)this }) / EnergyConfigHandler.IC2_RATIO;
            result = Math.max((((EnergyNetwork)this.getNetwork()).networkTierGC == 2) ? 256.0f : 128.0f, result);
            return result;
        }
        this.IC2surplusJoules = this.getNetwork().produce(this.IC2surplusJoules, true, 1, new TileEntity[] { (TileEntity)this });
        if (this.IC2surplusJoules < 0.001f) {
            this.IC2surplusJoules = 0.0f;
            float result = this.getNetwork().getRequest(new TileEntity[] { (TileEntity)this }) / EnergyConfigHandler.IC2_RATIO;
            result = Math.max((((EnergyNetwork)this.getNetwork()).networkTierGC == 2) ? 256.0f : 128.0f, result);
            return result;
        }
        return 0.0;
    }
    
    @Annotations.VersionSpecific(version = "[1.7.10]")
    @Annotations.RuntimeInterface(clazz = "ic2.api.energy.tile.IEnergySink", modID = "IC2")
    public double getDemandedEnergy() {
        if (this.getNetwork() == null) {
            return 0.0;
        }
        if (this.IC2surplusJoules < 0.001f) {
            this.IC2surplusJoules = 0.0f;
            return this.getNetwork().getRequest(new TileEntity[] { (TileEntity)this }) / EnergyConfigHandler.IC2_RATIO;
        }
        this.IC2surplusJoules = this.getNetwork().produce(this.IC2surplusJoules, true, 1, new TileEntity[] { (TileEntity)this });
        if (this.IC2surplusJoules < 0.001f) {
            this.IC2surplusJoules = 0.0f;
            return this.getNetwork().getRequest(new TileEntity[] { (TileEntity)this }) / EnergyConfigHandler.IC2_RATIO;
        }
        return 0.0;
    }
    
    @Annotations.VersionSpecific(version = "[1.7.2]")
    @Annotations.RuntimeInterface(clazz = "ic2.api.energy.tile.IEnergySink", modID = "IC2")
    public double injectEnergyUnits(final ForgeDirection directionFrom, final double amount) {
        final TileEntity tile = new BlockVec3((TileEntity)this).getTileEntityOnSide(this.worldObj, directionFrom);
        int tier = 1;
        if (tile instanceof IEnergySource && ((IEnergySource)tile).getOfferedEnergy() >= 128.0) {
            tier = 2;
        }
        final float convertedEnergy = (float)amount * EnergyConfigHandler.IC2_RATIO;
        final float surplus = this.getNetwork().produce(convertedEnergy, true, tier, new TileEntity[] { (TileEntity)this, tile });
        if (surplus >= 0.001f) {
            this.IC2surplusJoules = surplus;
        }
        else {
            this.IC2surplusJoules = 0.0f;
        }
        return 0.0;
    }
    
    @Annotations.VersionSpecific(version = "[1.7.10]")
    @Annotations.RuntimeInterface(clazz = "ic2.api.energy.tile.IEnergySink", modID = "IC2")
    public double injectEnergy(final ForgeDirection directionFrom, final double amount, final double voltage) {
        final TileEntity tile = new BlockVec3((TileEntity)this).getTileEntityOnSide(this.worldObj, directionFrom);
        int tier = ((int)voltage > 120) ? 2 : 1;
        if (tile instanceof IEnergySource && ((IEnergySource)tile).getOfferedEnergy() >= 128.0) {
            tier = 2;
        }
        final float convertedEnergy = (float)amount * EnergyConfigHandler.IC2_RATIO;
        final float surplus = this.getNetwork().produce(convertedEnergy, true, tier, new TileEntity[] { (TileEntity)this, tile });
        if (surplus >= 0.001f) {
            this.IC2surplusJoules = surplus;
        }
        else {
            this.IC2surplusJoules = 0.0f;
        }
        return 0.0;
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
            final Class<?> conductorIC2 = Class.forName("ic2.api.energy.tile.IEnergyConductor");
            if (conductorIC2.isInstance(emitter)) {
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.energy.tile.IEnergyEmitter", modID = "IC2")
    public boolean emitsEnergyTo(final TileEntity receiver, final ForgeDirection direction) {
        if (receiver instanceof IElectrical || receiver instanceof IConductor) {
            return false;
        }
        try {
            final Class<?> conductorIC2 = Class.forName("ic2.api.energy.tile.IEnergyConductor");
            if (conductorIC2.isInstance(receiver)) {
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
    @Annotations.RuntimeInterface(clazz = "buildcraft.api.power.IPowerReceptor", modID = "")
    public PowerHandler.PowerReceiver getPowerReceiver(final ForgeDirection side) {
        if (this.getNetwork() == null) {
            return null;
        }
        double requiredEnergy = this.getNetwork().getRequest(new TileEntity[] { (TileEntity)this }) / EnergyConfigHandler.BC3_RATIO;
        if (requiredEnergy <= 0.1) {
            requiredEnergy = 0.0;
        }
        ((PowerHandler)this.powerHandlerBC).configure(0.0, requiredEnergy, 0.0, requiredEnergy);
        return ((PowerHandler)this.powerHandlerBC).getPowerReceiver();
    }
    
    public void reconfigureBC() {
        double requiredEnergy = this.getNetwork().getRequest(new TileEntity[] { (TileEntity)this }) / EnergyConfigHandler.BC3_RATIO;
        if (requiredEnergy <= 0.1) {
            requiredEnergy = 0.0;
        }
        ((PowerHandler)this.powerHandlerBC).configure(0.0, requiredEnergy, 0.0, requiredEnergy);
    }
    
    @Annotations.RuntimeInterface(clazz = "buildcraft.api.power.IPowerReceptor", modID = "")
    public void doWork(final PowerHandler workProvider) {
        final PowerHandler handler = (PowerHandler)this.powerHandlerBC;
        double energyBC = handler.getEnergyStored();
        if (energyBC > 0.0) {
            energyBC = this.getNetwork().produce((float)energyBC * EnergyConfigHandler.BC3_RATIO, true, 1, new TileEntity[] { (TileEntity)this }) / EnergyConfigHandler.BC3_RATIO;
            if (energyBC < 0.0) {
                energyBC = 0.0;
            }
            handler.setEnergy(energyBC);
        }
        this.reconfigureBC();
    }
    
    @Annotations.RuntimeInterface(clazz = "buildcraft.api.power.IPowerReceptor", modID = "")
    public World getWorld() {
        return this.getWorldObj();
    }
    
    @Annotations.RuntimeInterface(clazz = "cofh.api.energy.IEnergyReceiver", modID = "")
    public int receiveEnergy(final ForgeDirection from, final int maxReceive, final boolean simulate) {
        if (this.getNetwork() == null) {
            return 0;
        }
        final float receiveGC = maxReceive * EnergyConfigHandler.RF_RATIO;
        final float sentGC = receiveGC - this.getNetwork().produce(receiveGC, !simulate, 1, new TileEntity[0]);
        return MathHelper.floor_float(sentGC / EnergyConfigHandler.RF_RATIO);
    }
    
    @Annotations.RuntimeInterface(clazz = "cofh.api.energy.IEnergyProvider", modID = "")
    public int extractEnergy(final ForgeDirection from, final int maxExtract, final boolean simulate) {
        return 0;
    }
    
    @Annotations.RuntimeInterface(clazz = "cofh.api.energy.IEnergyHandler", modID = "")
    public boolean canConnectEnergy(final ForgeDirection from) {
        final TileEntity tile = new BlockVec3((TileEntity)this).getTileEntityOnSide(this.worldObj, from);
        try {
            if (EnergyUtil.clazzEnderIOCable != null && EnergyUtil.clazzEnderIOCable.isInstance(tile)) {
                return false;
            }
        }
        catch (Exception ex) {}
        return true;
    }
    
    @Annotations.RuntimeInterface(clazz = "cofh.api.energy.IEnergyHandler", modID = "")
    public int getEnergyStored(final ForgeDirection from) {
        return 0;
    }
    
    @Annotations.RuntimeInterface(clazz = "cofh.api.energy.IEnergyHandler", modID = "")
    public int getMaxEnergyStored(final ForgeDirection from) {
        if (this.getNetwork() == null) {
            return 0;
        }
        return MathHelper.floor_float(this.getNetwork().getRequest(new TileEntity[] { (TileEntity)this }) / EnergyConfigHandler.RF_RATIO);
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.energy.IStrictEnergyAcceptor", modID = "Mekanism")
    public double transferEnergyToAcceptor(final ForgeDirection side, final double amount) {
        if (!this.canReceiveEnergy(side)) {
            return 0.0;
        }
        return amount - this.getNetwork().produce((float)amount * EnergyConfigHandler.MEKANISM_RATIO, true, 1, new TileEntity[] { (TileEntity)this }) / EnergyConfigHandler.MEKANISM_RATIO;
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.energy.IStrictEnergyAcceptor", modID = "Mekanism")
    public boolean canReceiveEnergy(final ForgeDirection side) {
        if (this.getNetwork() == null) {
            return false;
        }
        final TileEntity te = new BlockVec3((TileEntity)this).getTileEntityOnSide(this.worldObj, side);
        try {
            if (Class.forName("codechicken.multipart.TileMultipart").isInstance(te)) {
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.energy.IStrictEnergyAcceptor", modID = "Mekanism")
    public double getEnergy() {
        return 0.0;
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.energy.IStrictEnergyAcceptor", modID = "Mekanism")
    public void setEnergy(final double energy) {
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.energy.IStrictEnergyAcceptor", modID = "Mekanism")
    public double getMaxEnergy() {
        if (this.getNetwork() == null) {
            return 0.0;
        }
        return this.getNetwork().getRequest(new TileEntity[] { (TileEntity)this }) / EnergyConfigHandler.MEKANISM_RATIO;
    }
}
