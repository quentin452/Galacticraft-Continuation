package micdoodle8.mods.galacticraft.api.transmission.tile;

import net.minecraftforge.common.util.*;

public interface IOxygenReceiver extends IConnector
{
    boolean shouldPullOxygen();
    
    float receiveOxygen(final ForgeDirection p0, final float p1, final boolean p2);
    
    float provideOxygen(final ForgeDirection p0, final float p1, final boolean p2);
    
    float getOxygenRequest(final ForgeDirection p0);
    
    float getOxygenProvide(final ForgeDirection p0);
}
