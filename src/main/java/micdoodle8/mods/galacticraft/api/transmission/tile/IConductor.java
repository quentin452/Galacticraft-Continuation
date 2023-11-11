package micdoodle8.mods.galacticraft.api.transmission.tile;

import micdoodle8.mods.galacticraft.api.transmission.grid.*;

public interface IConductor extends ITransmitter
{
    int getTierGC();
    
    IElectricityNetwork getNetwork();
}
