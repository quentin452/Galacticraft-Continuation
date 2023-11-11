package micdoodle8.mods.galacticraft.core.energy.tile;

import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.tick.*;
import micdoodle8.mods.galacticraft.api.transmission.grid.*;
import micdoodle8.mods.galacticraft.core.energy.grid.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import micdoodle8.mods.galacticraft.api.transmission.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;

public abstract class TileBaseConductor extends TileEntityAdvanced implements IConductor
{
    private IGridNetwork network;
    public TileEntity[] adjacentConnections;

    public TileBaseConductor() {
        this.adjacentConnections = null;
    }

    public void validate() {
        super.validate();
        if (!this.worldObj.isRemote) {
            TickHandlerServer.energyTransmitterUpdates.add(this);
        }
    }

    public void invalidate() {
        if (!this.worldObj.isRemote) {
            this.getNetwork().split(this);
        }
        super.invalidate();
    }

    public void onChunkUnload() {
        super.invalidate();
        super.onChunkUnload();
    }

    public boolean canUpdate() {
        return false;
    }

    public IElectricityNetwork getNetwork() {
        if (this.network == null) {
            final EnergyNetwork network = new EnergyNetwork();
            network.getTransmitters().add(this);
            this.setNetwork((IGridNetwork)network);
        }
        return (IElectricityNetwork)this.network;
    }

    public void setNetwork(final IGridNetwork network) {
        this.network = network;
    }

    public void refresh() {
        if (!this.worldObj.isRemote) {
            this.adjacentConnections = null;
            this.getNetwork().refresh();
            final BlockVec3 thisVec = new BlockVec3((TileEntity)this);
            for (final ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                final TileEntity tileEntity = thisVec.getTileEntityOnSide(this.worldObj, side);
                if (tileEntity != null && tileEntity.getClass() == this.getClass() && tileEntity instanceof INetworkProvider && !this.getNetwork().equals(((INetworkProvider)tileEntity).getNetwork())) {
                    ((INetworkProvider)tileEntity).getNetwork().merge((Object)this.getNetwork());
                }
            }
        }
    }

    public TileEntity[] getAdjacentConnections() {
        if (this.adjacentConnections == null) {
            this.adjacentConnections = new TileEntity[6];
            final BlockVec3 thisVec = new BlockVec3((TileEntity)this);
            for (int i = 0; i < 6; ++i) {
                final TileEntity tileEntity = thisVec.getTileEntityOnSide(this.worldObj, i);
                if (tileEntity instanceof IConnector && ((IConnector)tileEntity).canConnect(ForgeDirection.getOrientation(i ^ 0x1), NetworkType.POWER)) {
                    this.adjacentConnections[i] = tileEntity;
                }
            }
        }
        return this.adjacentConnections;
    }

    public boolean canConnect(final ForgeDirection direction, final NetworkType type) {
        return type == NetworkType.POWER;
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)(this.xCoord + 1), (double)(this.yCoord + 1), (double)(this.zCoord + 1));
    }

    public NetworkType getNetworkType() {
        return NetworkType.POWER;
    }
}
