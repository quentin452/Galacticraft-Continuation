package micdoodle8.mods.galacticraft.core.oxygen;

import net.minecraft.tileentity.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.api.transmission.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import mekanism.api.gas.*;
import cpw.mods.fml.common.*;
import java.util.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import micdoodle8.mods.galacticraft.api.transmission.grid.*;

public class OxygenNetwork implements IOxygenNetwork
{
    public Map<TileEntity, ForgeDirection> oxygenTiles;
    private final Set<ITransmitter> pipes;

    public OxygenNetwork() {
        this.pipes = new HashSet<ITransmitter>();
    }

    public float produce(final float totalOxygen, final TileEntity... ignoreTiles) {
        float remainingUsableOxygen = totalOxygen;
        if (this.oxygenTiles == null || this.oxygenTiles.isEmpty()) {
            this.refreshOxygenTiles();
        }
        if (!this.oxygenTiles.isEmpty()) {
            final float totalOxygenRequest = this.getRequest(ignoreTiles);
            if (totalOxygenRequest > 0.0f) {
                final List<TileEntity> ignoreTilesList = Arrays.asList(ignoreTiles);
                for (final TileEntity tileEntity : new HashSet<TileEntity>(this.oxygenTiles.keySet())) {
                    if (!ignoreTilesList.contains(tileEntity)) {
                        if (tileEntity instanceof IOxygenReceiver) {
                            final IOxygenReceiver oxygenTile = (IOxygenReceiver)tileEntity;
                            if (!oxygenTile.shouldPullOxygen()) {
                                continue;
                            }
                            for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                                if (oxygenTile.canConnect(direction, NetworkType.OXYGEN)) {
                                    final TileEntity tile = new BlockVec3(tileEntity).getTileEntityOnSide(tileEntity.getWorldObj(), direction);
                                    if (this.pipes.contains(tile)) {
                                        final float oxygenToSend = Math.min(remainingUsableOxygen, totalOxygen * (oxygenTile.getOxygenRequest(direction) / totalOxygenRequest));
                                        if (oxygenToSend > 0.0f) {
                                            remainingUsableOxygen -= oxygenTile.receiveOxygen(direction, oxygenToSend, true);
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            if (!EnergyConfigHandler.isMekanismLoaded() || !(tileEntity instanceof IGasHandler)) {
                                continue;
                            }
                            final IGasHandler gasHandler = (IGasHandler)tileEntity;
                            for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                                if (gasHandler.canReceiveGas(direction, (Gas)EnergyConfigHandler.gasOxygen)) {
                                    final TileEntity tile = new BlockVec3(tileEntity).getTileEntityOnSide(tileEntity.getWorldObj(), direction);
                                    if (this.getTransmitters().contains(tile)) {
                                        final int oxygenToSend2 = (int)Math.floor(totalOxygen / this.oxygenTiles.size());
                                        if (oxygenToSend2 > 0) {
                                            try {
                                                remainingUsableOxygen -= gasHandler.receiveGas(direction, new GasStack((Gas)EnergyConfigHandler.gasOxygen, oxygenToSend2));
                                            }
                                            catch (Exception ex) {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return remainingUsableOxygen;
    }

    public float getRequest(final TileEntity... ignoreTiles) {
        final List<Float> requests = new ArrayList<Float>();
        if (this.oxygenTiles == null || this.oxygenTiles.isEmpty()) {
            this.refreshOxygenTiles();
        }
        final List<TileEntity> ignoreTilesList = Arrays.asList(ignoreTiles);
        for (final TileEntity tileEntity : new HashSet<TileEntity>(this.oxygenTiles.keySet())) {
            if (ignoreTilesList.contains(tileEntity)) {
                continue;
            }
            if (!(tileEntity instanceof IOxygenReceiver) || tileEntity.isInvalid()) {
                continue;
            }
            final IOxygenReceiver oxygenTile = (IOxygenReceiver)tileEntity;
            if (!oxygenTile.shouldPullOxygen() || tileEntity.getWorldObj().getTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) != tileEntity) {
                continue;
            }
            for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                if (oxygenTile.canConnect(direction, NetworkType.OXYGEN)) {
                    final TileEntity tile = new BlockVec3(tileEntity).getTileEntityOnSide(tileEntity.getWorldObj(), direction);
                    if (this.pipes.contains(tile)) {
                        requests.add(((IOxygenReceiver)tileEntity).getOxygenRequest(direction));
                    }
                }
            }
        }
        float total = 0.0f;
        for (final Float f : requests) {
            total += f;
        }
        return total;
    }

    public void refresh() {
        if (this.oxygenTiles != null) {
            this.oxygenTiles.clear();
        }
        try {
            final Iterator<ITransmitter> it = this.pipes.iterator();
            while (it.hasNext()) {
                final ITransmitter transmitter = it.next();
                if (transmitter == null) {
                    it.remove();
                }
                else {
                    transmitter.onNetworkChanged();
                    if (((TileEntity)transmitter).isInvalid() || ((TileEntity)transmitter).getWorldObj() == null) {
                        it.remove();
                    }
                    else {
                        transmitter.setNetwork((IGridNetwork)this);
                    }
                }
            }
        }
        catch (Exception e) {
            FMLLog.severe("Failed to refresh oxygen pipe network.", new Object[0]);
            e.printStackTrace();
        }
    }

    public void refreshOxygenTiles() {
        if (this.oxygenTiles == null) {
            this.oxygenTiles = new HashMap<TileEntity, ForgeDirection>();
        }
        else {
            this.oxygenTiles.clear();
        }
        try {
            final Iterator<ITransmitter> it = this.pipes.iterator();
            while (it.hasNext()) {
                final ITransmitter transmitter = it.next();
                if (transmitter == null || ((TileEntity)transmitter).isInvalid() || ((TileEntity)transmitter).getWorldObj() == null) {
                    it.remove();
                }
                else {
                    int i = 0;
                    for (final TileEntity acceptor : transmitter.getAdjacentConnections()) {
                        if (!(acceptor instanceof ITransmitter) && acceptor instanceof IConnector) {
                            this.oxygenTiles.put(acceptor, ForgeDirection.getOrientation(i));
                        }
                        ++i;
                    }
                }
            }
        }
        catch (Exception e) {
            FMLLog.severe("Failed to refresh oxygen pipe network.", new Object[0]);
            e.printStackTrace();
        }
    }

    public Set<ITransmitter> getTransmitters() {
        return this.pipes;
    }

    public IOxygenNetwork merge(final IOxygenNetwork network) {
        if (network != null && network != this) {
            final OxygenNetwork newNetwork = new OxygenNetwork();
            newNetwork.pipes.addAll(this.pipes);
            newNetwork.pipes.addAll(network.getTransmitters());
            newNetwork.refresh();
            return (IOxygenNetwork)newNetwork;
        }
        return (IOxygenNetwork)this;
    }

    public void split(final ITransmitter splitPoint) {
        if (splitPoint instanceof TileEntity) {
            this.pipes.remove(splitPoint);
            final TileEntity[] adjacentConnections;
            final TileEntity[] connectedBlocks = adjacentConnections = splitPoint.getAdjacentConnections();
            for (final TileEntity connectedBlockA : adjacentConnections) {
                if (connectedBlockA instanceof INetworkConnection) {
                    for (final TileEntity connectedBlockB : connectedBlocks) {
                        if (connectedBlockA != connectedBlockB && connectedBlockB instanceof INetworkConnection) {
                            final Pathfinder finder = (Pathfinder)new PathfinderChecker(((TileEntity)splitPoint).getWorldObj(), (INetworkConnection)connectedBlockB, NetworkType.OXYGEN, new INetworkConnection[] { (INetworkConnection)splitPoint });
                            finder.init(new BlockVec3(connectedBlockA));
                            if (finder.results.size() > 0) {
                                for (final BlockVec3 node : finder.closedSet) {
                                    final TileEntity nodeTile = node.getTileEntity((IBlockAccess)((TileEntity)splitPoint).getWorldObj());
                                    if (nodeTile instanceof INetworkProvider && nodeTile != splitPoint) {
                                        ((INetworkProvider)nodeTile).setNetwork((IGridNetwork)this);
                                    }
                                }
                            }
                            else {
                                final IOxygenNetwork newNetwork = (IOxygenNetwork)new OxygenNetwork();
                                for (final BlockVec3 node2 : finder.closedSet) {
                                    final TileEntity nodeTile2 = node2.getTileEntity((IBlockAccess)((TileEntity)splitPoint).getWorldObj());
                                    if (nodeTile2 instanceof INetworkProvider && nodeTile2 != splitPoint) {
                                        newNetwork.getTransmitters().add((ITransmitter) nodeTile2);
                                    }
                                }
                                newNetwork.refresh();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "OxygenNetwork[" + this.hashCode() + "|Pipes:" + this.pipes.size() + "|Acceptors:" + ((this.oxygenTiles == null) ? 0 : this.oxygenTiles.size()) + "]";
    }
}
