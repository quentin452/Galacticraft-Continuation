package micdoodle8.mods.galacticraft.api.transmission.tile;

import net.minecraftforge.common.util.*;

public interface IElectrical extends IConnector
{
    float receiveElectricity(final ForgeDirection p0, final float p1, final int p2, final boolean p3);
    
    float provideElectricity(final ForgeDirection p0, final float p1, final boolean p2);
    
    float getRequest(final ForgeDirection p0);
    
    float getProvide(final ForgeDirection p0);
    
    int getTierGC();
}
