package micdoodle8.mods.galacticraft.api.transmission.grid;

import java.util.*;

public interface IGridNetwork<N, C, A>
{
    void refresh();
    
    Set<C> getTransmitters();
    
    N merge(final N p0);
    
    void split(final C p0);
}
