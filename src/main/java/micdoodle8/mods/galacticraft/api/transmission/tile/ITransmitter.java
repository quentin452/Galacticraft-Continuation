package micdoodle8.mods.galacticraft.api.transmission.tile;

import micdoodle8.mods.galacticraft.api.transmission.*;

public interface ITransmitter extends INetworkProvider, INetworkConnection
{
    NetworkType getNetworkType();
}
