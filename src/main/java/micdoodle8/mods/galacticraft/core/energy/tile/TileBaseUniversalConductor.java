package micdoodle8.mods.galacticraft.core.energy.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import mekanism.api.energy.IStrictEnergyAcceptor;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler;
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil;

@InterfaceList({ @Interface(modid = "CoFHAPI|energy", iface = "cofh.api.energy.IEnergyHandler"),
    @Interface(modid = "MekanismAPI|energy", iface = "mekanism.api.energy.IStrictEnergyAcceptor"), })
public abstract class TileBaseUniversalConductor extends TileBaseConductor
    implements IEnergyHandler, IStrictEnergyAcceptor {

    protected boolean isAddedToEnergyNet;
    protected Object powerHandlerBC;

    // public float buildcraftBuffer = EnergyConfigHandler.BC3_RATIO * 50;
    private float IC2surplusJoules = 0F;

    public TileBaseUniversalConductor() {
        this.initBC();
    }

    @Override
    public void onNetworkChanged() {}

    private void initBC() {}

    @Override
    public TileEntity[] getAdjacentConnections() {
        return EnergyUtil.getAdjacentPowerConnections(this);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!this.isAddedToEnergyNet) {
            if (!this.worldObj.isRemote) {}

            this.isAddedToEnergyNet = true;
        }
    }

    @Override
    public void invalidate() {
        this.IC2surplusJoules = 0F;
        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if (this.getNetwork() == null) {
            return 0;
        }
        final float receiveGC = maxReceive * EnergyConfigHandler.RF_RATIO;
        final float sentGC = receiveGC - this.getNetwork()
            .produce(receiveGC, !simulate, 1);
        return MathHelper.floor_float(sentGC / EnergyConfigHandler.RF_RATIO);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        // Do not form wire-to-wire connections with EnderIO conduits
        final TileEntity tile = new BlockVec3(this).getTileEntityOnSide(this.worldObj, from);
        try {
            if (EnergyUtil.clazzEnderIOCable != null && EnergyUtil.clazzEnderIOCable.isInstance(tile)) {
                return false;
            }
        } catch (final Exception e) {}
        return true;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        if (this.getNetwork() == null) {
            return 0;
        }

        return MathHelper.floor_float(
            this.getNetwork()
                .getRequest(this) / EnergyConfigHandler.RF_RATIO);
    }

    @Override
    public double transferEnergyToAcceptor(ForgeDirection side, double amount) {
        if (!this.canReceiveEnergy(side)) {
            return 0;
        }

        return amount - this.getNetwork()
            .produce((float) amount * EnergyConfigHandler.MEKANISM_RATIO, true, 1, this)
            / EnergyConfigHandler.MEKANISM_RATIO;
    }

    @Override
    public boolean canReceiveEnergy(ForgeDirection side) {
        if (this.getNetwork() == null) {
            return false;
        }

        final TileEntity te = new BlockVec3(this).getTileEntityOnSide(this.worldObj, side);
        try {
            if (Class.forName("codechicken.multipart.TileMultipart")
                .isInstance(te)) {
                return false;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public double getEnergy() {
        return 0;
    }

    @Override
    public void setEnergy(double energy) {}

    @Override
    public double getMaxEnergy() {
        if (this.getNetwork() == null) {
            return 0;
        }
        return this.getNetwork()
            .getRequest(this) / EnergyConfigHandler.MEKANISM_RATIO;
    }
}
