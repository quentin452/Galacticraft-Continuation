package micdoodle8.mods.galacticraft.api.transmission.tile;

import micdoodle8.mods.galacticraft.api.transmission.grid.*;

public interface INetworkProvider
{
    IGridNetwork getNetwork();
    
    void setNetwork(final IGridNetwork p0);
}
