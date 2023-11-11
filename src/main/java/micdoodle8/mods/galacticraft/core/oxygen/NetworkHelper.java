package micdoodle8.mods.galacticraft.core.oxygen;

import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.api.transmission.*;
import net.minecraftforge.common.util.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import micdoodle8.mods.galacticraft.api.transmission.grid.*;

public class NetworkHelper
{
    public static EnumSet<ForgeDirection> getDirections(final TileEntity tileEntity, final NetworkType type) {
        final EnumSet<ForgeDirection> possibleSides = EnumSet.noneOf(ForgeDirection.class);
        if (tileEntity instanceof IConnector) {
            for (int i = 0; i < 6; ++i) {
                final ForgeDirection direction = ForgeDirection.getOrientation(i);
                if (((IConnector)tileEntity).canConnect(direction, type)) {
                    possibleSides.add(direction);
                }
            }
        }
        return possibleSides;
    }
    
    public static Set<IElectricityNetwork> getNetworksFromMultipleSides(final TileEntity tileEntity, final EnumSet<ForgeDirection> approachingDirection) {
        final Set<IElectricityNetwork> connectedNetworks = new HashSet<IElectricityNetwork>();
        final BlockVec3 tileVec = new BlockVec3(tileEntity);
        for (final ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (approachingDirection.contains(side)) {
                final TileEntity outputConductor = tileVec.getTileEntityOnSide(tileEntity.getWorldObj(), side);
                final IElectricityNetwork electricityNetwork = getElectricalNetworkFromTileEntity(outputConductor, side);
                if (electricityNetwork != null) {
                    connectedNetworks.add(electricityNetwork);
                }
            }
        }
        return connectedNetworks;
    }
    
    public static IElectricityNetwork getElectricalNetworkFromTileEntity(final TileEntity tileEntity, final ForgeDirection approachDirection) {
        if (tileEntity != null && tileEntity instanceof INetworkProvider) {
            if (tileEntity instanceof IConnector) {
                if (((IConnector)tileEntity).canConnect(approachDirection.getOpposite(), NetworkType.POWER) && ((INetworkProvider)tileEntity).getNetwork() instanceof IElectricityNetwork) {
                    return (IElectricityNetwork)((INetworkProvider)tileEntity).getNetwork();
                }
            }
            else if (((INetworkProvider)tileEntity).getNetwork() instanceof IElectricityNetwork) {
                return (IElectricityNetwork)((INetworkProvider)tileEntity).getNetwork();
            }
        }
        return null;
    }
    
    public static IOxygenNetwork getOxygenNetworkFromTileEntity(final TileEntity tileEntity, final ForgeDirection approachDirection) {
        if (tileEntity != null && tileEntity instanceof INetworkProvider) {
            if (tileEntity instanceof IConnector) {
                if (((IConnector)tileEntity).canConnect(approachDirection.getOpposite(), NetworkType.OXYGEN) && ((INetworkProvider)tileEntity).getNetwork() instanceof IOxygenNetwork) {
                    return (IOxygenNetwork)((INetworkProvider)tileEntity).getNetwork();
                }
            }
            else if (((INetworkProvider)tileEntity).getNetwork() instanceof IOxygenNetwork) {
                return (IOxygenNetwork)((INetworkProvider)tileEntity).getNetwork();
            }
        }
        return null;
    }
    
    public static IHydrogenNetwork getHydrogenNetworkFromTileEntity(final TileEntity tileEntity, final ForgeDirection approachDirection) {
        if (tileEntity != null && tileEntity instanceof INetworkProvider) {
            if (tileEntity instanceof IConnector) {
                if (((IConnector)tileEntity).canConnect(approachDirection.getOpposite(), NetworkType.HYDROGEN) && ((INetworkProvider)tileEntity).getNetwork() instanceof IHydrogenNetwork) {
                    return (IHydrogenNetwork)((INetworkProvider)tileEntity).getNetwork();
                }
            }
            else if (((INetworkProvider)tileEntity).getNetwork() instanceof IHydrogenNetwork) {
                return (IHydrogenNetwork)((INetworkProvider)tileEntity).getNetwork();
            }
        }
        return null;
    }
}
