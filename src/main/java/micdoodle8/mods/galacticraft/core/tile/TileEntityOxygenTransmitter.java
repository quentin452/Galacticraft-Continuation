package micdoodle8.mods.galacticraft.core.tile;

import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.tick.*;
import micdoodle8.mods.galacticraft.core.oxygen.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.api.transmission.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.api.transmission.grid.*;
import micdoodle8.mods.miccore.*;
import mekanism.api.gas.*;

public abstract class TileEntityOxygenTransmitter extends TileEntityAdvanced implements ITransmitter
{
    private IGridNetwork network;
    public TileEntity[] adjacentConnections;
    
    public TileEntityOxygenTransmitter() {
        this.adjacentConnections = null;
    }
    
    public void validate() {
        super.validate();
        if (!this.worldObj.isRemote) {
            TickHandlerServer.oxygenTransmitterUpdates.add(this);
        }
    }
    
    public void invalidate() {
        if (!this.worldObj.isRemote) {
            this.getNetwork().split((Object)this);
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
    
    public IGridNetwork getNetwork() {
        if (this.network == null) {
            this.resetNetwork();
        }
        return this.network;
    }
    
    public void onNetworkChanged() {
    }
    
    protected void resetNetwork() {
        final OxygenNetwork network = new OxygenNetwork();
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
            this.adjacentConnections = OxygenUtil.getAdjacentOxygenConnections((TileEntity)this);
        }
        return this.adjacentConnections;
    }
    
    public boolean canConnect(final ForgeDirection direction, final NetworkType type) {
        return type == NetworkType.OXYGEN;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)(this.xCoord + 1), (double)(this.yCoord + 1), (double)(this.zCoord + 1));
    }
    
    public NetworkType getNetworkType() {
        return NetworkType.OXYGEN;
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public int receiveGas(final ForgeDirection side, final GasStack stack, final boolean doTransfer) {
        if (!stack.getGas().getName().equals("oxygen")) {
            return 0;
        }
        return stack.amount - (int)Math.floor(((IOxygenNetwork)this.getNetwork()).produce((float)stack.amount, new TileEntity[] { (TileEntity)this }));
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
        return type.getName().equals("oxygen");
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.ITubeConnection", modID = "Mekanism")
    public boolean canTubeConnect(final ForgeDirection side) {
        return this.canConnect(side, NetworkType.OXYGEN);
    }
}
