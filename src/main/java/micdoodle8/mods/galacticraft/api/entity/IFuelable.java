package micdoodle8.mods.galacticraft.api.entity;

import net.minecraftforge.fluids.*;

public interface IFuelable
{
    int addFuel(final FluidStack p0, final boolean p1);
    
    FluidStack removeFuel(final int p0);
}
