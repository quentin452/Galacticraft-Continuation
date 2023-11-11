package micdoodle8.mods.galacticraft.api.tile;

import java.util.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.entity.*;

public interface IFuelDock
{
    HashSet<ILandingPadAttachable> getConnectedTiles();
    
    boolean isBlockAttachable(final IBlockAccess p0, final int p1, final int p2, final int p3);
    
    IDockable getDockedEntity();
    
    void dockEntity(final IDockable p0);
}
