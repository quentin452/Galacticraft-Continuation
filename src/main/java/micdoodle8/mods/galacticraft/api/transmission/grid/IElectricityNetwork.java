package micdoodle8.mods.galacticraft.api.transmission.grid;

import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import net.minecraft.tileentity.*;

public interface IElectricityNetwork extends IGridNetwork<IElectricityNetwork, IConductor, TileEntity>
{
    float produce(final float p0, final boolean p1, final int p2, final TileEntity... p3);
    
    float getRequest(final TileEntity... p0);
}
