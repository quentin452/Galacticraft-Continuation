package micdoodle8.mods.galacticraft.api.transmission.grid;

import micdoodle8.mods.galacticraft.api.vector.*;
import java.util.*;

public interface IPathCallBack
{
    Set<BlockVec3> getConnectedNodes(final Pathfinder p0, final BlockVec3 p1);
    
    boolean onSearch(final Pathfinder p0, final BlockVec3 p1);
}
