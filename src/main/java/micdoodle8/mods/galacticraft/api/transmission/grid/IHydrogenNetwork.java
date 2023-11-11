package micdoodle8.mods.galacticraft.api.transmission.grid;

import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import net.minecraft.tileentity.*;

public interface IHydrogenNetwork extends IGridNetwork<IHydrogenNetwork, ITransmitter, TileEntity>
{
    float produce(final float p0, final TileEntity... p1);
    
    float getRequest(final TileEntity... p0);
}
