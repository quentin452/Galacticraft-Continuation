package micdoodle8.mods.galacticraft.planets.mars.tile;

import micdoodle8.mods.galacticraft.api.transmission.tile.ITransmitter;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.tick.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import micdoodle8.mods.galacticraft.api.transmission.*;
import mekanism.api.transmitters.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.api.transmission.grid.*;
import micdoodle8.mods.miccore.*;
import mekanism.api.gas.*;

public class TileEntityHydrogenPipe extends TileEntity implements ITransmitter
{
    private IGridNetwork network;
    public TileEntity[] adjacentConnections;

    public TileEntityHydrogenPipe() {
        this.adjacentConnections = null;
    }

    public boolean canUpdate() {
        return this.worldObj == null || !this.worldObj.isRemote;
    }

    public void validate() {
        super.validate();
        if (!this.worldObj.isRemote) {
            TickHandlerServer.hydrogenTransmitterUpdates.add(this);
        }
        if (this.worldObj != null && this.worldObj.isRemote) {
            this.worldObj.func_147479_m(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    public void invalidate() {
        if (!this.worldObj.isRemote) {
            this.getNetwork().split((Object)this);
        }
        super.invalidate();
    }

    public IGridNetwork getNetwork() {
        if (this.network == null) {
            this.resetNetwork();
        }
        return this.network;
    }

    public void onNetworkChanged() {
    }

    protected void resetNetwork() {
        final HydrogenNetwork network = new HydrogenNetwork();
        network.getTransmitters().add(this);
        this.setNetwork((IGridNetwork)network);
    }

    public void setNetwork(final IGridNetwork network) {
        this.network = network;
    }

    public void refresh() {
        if (!this.worldObj.isRemote) {
            this.adjacentConnections = null;
            for (final ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                final TileEntity tileEntity = new BlockVec3((TileEntity)this).getTileEntityOnSide(this.worldObj, side);
                if (tileEntity != null && tileEntity.getClass() == this.getClass() && tileEntity instanceof INetworkProvider && !this.getNetwork().equals(((INetworkProvider)tileEntity).getNetwork())) {
                    this.setNetwork((IGridNetwork)this.getNetwork().merge((Object)((INetworkProvider)tileEntity).getNetwork()));
                }
            }
            this.getNetwork().refresh();
        }
    }

    public TileEntity[] getAdjacentConnections() {
        if (this.adjacentConnections == null) {
            this.adjacentConnections = getAdjacentHydrogenConnections(this);
        }
        return this.adjacentConnections;
    }

    public static TileEntity[] getAdjacentHydrogenConnections(final TileEntity tile) {
        final TileEntity[] adjacentConnections = new TileEntity[ForgeDirection.VALID_DIRECTIONS.length];
        final boolean isMekLoaded = EnergyConfigHandler.isMekanismLoaded();
        final BlockVec3 thisVec = new BlockVec3(tile);
        for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            final TileEntity tileEntity = thisVec.getTileEntityOnSide(tile.getWorldObj(), direction);
            if (tileEntity instanceof IConnector) {
                if (((IConnector)tileEntity).canConnect(direction.getOpposite(), NetworkType.HYDROGEN)) {
                    adjacentConnections[direction.ordinal()] = tileEntity;
                }
            }
            else if (isMekLoaded && tileEntity instanceof ITubeConnection && (!(tileEntity instanceof IGasTransmitter) || TransmissionType.checkTransmissionType(tileEntity, TransmissionType.GAS, tileEntity)) && ((ITubeConnection)tileEntity).canTubeConnect(direction)) {
                adjacentConnections[direction.ordinal()] = tileEntity;
            }
        }
        return adjacentConnections;
    }

    public boolean canConnect(final ForgeDirection direction, final NetworkType type) {
        return type == NetworkType.HYDROGEN;
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)(this.xCoord + 1), (double)(this.yCoord + 1), (double)(this.zCoord + 1));
    }

    public NetworkType getNetworkType() {
        return NetworkType.HYDROGEN;
    }

    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public int receiveGas(final ForgeDirection side, final GasStack stack, final boolean doTransfer) {
        if (!stack.getGas().getName().equals("hydrogen")) {
            return 0;
        }
        return stack.amount - (int)Math.floor(((IHydrogenNetwork)this.getNetwork()).produce((float)stack.amount, new TileEntity[] { this }));
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
    public boolean canDrawGas(final ForgeDirection side, final Gas type) {
        return false;
    }

    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public boolean canReceiveGas(final ForgeDirection side, final Gas type) {
        return type.getName().equals("hydrogen");
    }

    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.ITubeConnection", modID = "Mekanism")
    public boolean canTubeConnect(final ForgeDirection side) {
        return this.canConnect(side, NetworkType.HYDROGEN);
    }
}
