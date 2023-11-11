package micdoodle8.mods.galacticraft.api.transmission.grid;

import micdoodle8.mods.galacticraft.api.vector.*;

public interface IReflectorNode
{
    Vector3 getInputPoint();
    
    Vector3 getOutputPoint(final boolean p0);
}
