package micdoodle8.mods.galacticraft.planets.mars.tile;

import net.minecraft.tileentity.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.transmission.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import mekanism.api.gas.*;
import java.util.*;
import cpw.mods.fml.common.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import micdoodle8.mods.galacticraft.api.transmission.grid.*;

public class HydrogenNetwork implements IHydrogenNetwork
{
    public Map<TileEntity, ForgeDirection> hydrogenTiles;
    private final Set<ITransmitter> pipes;

    public HydrogenNetwork() {
        this.hydrogenTiles = new HashMap<TileEntity, ForgeDirection>();
        this.pipes = new HashSet<ITransmitter>();
    }

    public float produce(final float totalHydrogen, final TileEntity... ignoreTiles) {
        float remainingUsableHydrogen = totalHydrogen;
        if (this.hydrogenTiles.isEmpty()) {
            this.refreshHydrogenTiles();
        }
        if (!this.hydrogenTiles.isEmpty()) {
            final float totalHydrogenRequest = this.getRequest(ignoreTiles);
            if (totalHydrogenRequest > 0.0f) {
                final List<TileEntity> ignoreTilesList = Arrays.asList(ignoreTiles);
                for (final TileEntity tileEntity : new HashSet<TileEntity>(this.hydrogenTiles.keySet())) {
                    if (!ignoreTilesList.contains(tileEntity)) {
                        if (tileEntity instanceof TileEntityMethaneSynthesizer) {
                            final TileEntityMethaneSynthesizer hydrogenTile = (TileEntityMethaneSynthesizer)tileEntity;
                            if (!hydrogenTile.shouldPullHydrogen()) {
                                continue;
                            }
                            for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                                final TileEntity tile = new BlockVec3(tileEntity).modifyPositionFromSide(direction, 1).getTileEntity((IBlockAccess)tileEntity.getWorldObj());
                                if (hydrogenTile.canConnect(direction, NetworkType.HYDROGEN) && this.pipes.contains(tile)) {
                                    final float hydrogenToSend = Math.max(totalHydrogen, totalHydrogen * (hydrogenTile.getHydrogenRequest(direction) / totalHydrogenRequest));
                                    if (hydrogenToSend > 0.0f) {
                                        remainingUsableHydrogen -= hydrogenTile.receiveHydrogen(direction, hydrogenToSend, true);
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
                                final TileEntity tile = new BlockVec3(tileEntity).getTileEntityOnSide(tileEntity.getWorldObj(), direction);
                                if (gasHandler.canReceiveGas(direction, (Gas)EnergyConfigHandler.gasHydrogen) && this.getTransmitters().contains(tile)) {
                                    final int hydrogenToSend2 = (int)Math.floor(totalHydrogen / this.hydrogenTiles.size());
                                    if (hydrogenToSend2 > 0) {
                                        try {
                                            remainingUsableHydrogen -= gasHandler.receiveGas(direction, new GasStack((Gas)EnergyConfigHandler.gasHydrogen, hydrogenToSend2));
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
        return remainingUsableHydrogen;
    }

    public float getRequest(final TileEntity... ignoreTiles) {
        final List<Float> requests = new ArrayList<Float>();
        if (this.hydrogenTiles.isEmpty()) {
            this.refreshHydrogenTiles();
        }
        final List<TileEntity> ignoreTilesList = Arrays.asList(ignoreTiles);
        for (final TileEntity tileEntity : new HashSet<TileEntity>(this.hydrogenTiles.keySet())) {
            if (ignoreTilesList.contains(tileEntity)) {
                continue;
            }
            if (!(tileEntity instanceof TileEntityMethaneSynthesizer) || !((TileEntityMethaneSynthesizer)tileEntity).shouldPullHydrogen() || tileEntity.isInvalid() || tileEntity.getWorldObj().getTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) != tileEntity) {
                continue;
            }
            for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                final BlockVec3 tileVec = new BlockVec3(tileEntity);
                final TileEntity tile = tileVec.modifyPositionFromSide(direction, 1).getTileEntity((IBlockAccess)tileEntity.getWorldObj());
                if (((TileEntityMethaneSynthesizer)tileEntity).canConnect(direction, NetworkType.HYDROGEN) && this.pipes.contains(tile)) {
                    requests.add(((TileEntityMethaneSynthesizer)tileEntity).getHydrogenRequest(direction));
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
        this.hydrogenTiles.clear();
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
            FMLLog.severe("Failed to refresh hydrogen pipe network.", new Object[0]);
            e.printStackTrace();
        }
    }

    public void refreshHydrogenTiles() {
        try {
            final Iterator<ITransmitter> it = this.pipes.iterator();
            while (it.hasNext()) {
                final ITransmitter transmitter = it.next();
                if (transmitter == null || ((TileEntity)transmitter).isInvalid() || ((TileEntity)transmitter).getWorldObj() == null) {
                    it.remove();
                }
                else {
                    for (int i = 0; i < transmitter.getAdjacentConnections().length; ++i) {
                        final TileEntity acceptor = transmitter.getAdjacentConnections()[i];
                        if (!(acceptor instanceof ITransmitter) && acceptor instanceof IConnector) {
                            this.hydrogenTiles.put(acceptor, ForgeDirection.getOrientation(i));
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            FMLLog.severe("Failed to refresh hydrogen pipe network.", new Object[0]);
            e.printStackTrace();
        }
    }

    public Set<ITransmitter> getTransmitters() {
        return this.pipes;
    }

    public IHydrogenNetwork merge(final IHydrogenNetwork network) {
        if (network != null && network != this) {
            final HydrogenNetwork newNetwork = new HydrogenNetwork();
            newNetwork.pipes.addAll(this.pipes);
            newNetwork.pipes.addAll(network.getTransmitters());
            newNetwork.refresh();
            return (IHydrogenNetwork)newNetwork;
        }
        return (IHydrogenNetwork)this;
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
                            final Pathfinder finder = (Pathfinder)new PathfinderChecker(((TileEntity)splitPoint).getWorldObj(), (INetworkConnection)connectedBlockB, NetworkType.HYDROGEN, new INetworkConnection[] { (INetworkConnection)splitPoint });
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
                                final IHydrogenNetwork newNetwork = (IHydrogenNetwork)new HydrogenNetwork();
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
        return "HydrogenNetwork[" + this.hashCode() + "|Pipes:" + this.pipes.size() + "|Acceptors:" + this.hydrogenTiles.size() + "]";
    }
}
