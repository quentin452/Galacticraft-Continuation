package micdoodle8.mods.galacticraft.core.tile;

import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.tileentity.*;

public interface IMultiBlock
{
    boolean onActivated(final EntityPlayer p0);
    
    void onCreate(final BlockVec3 p0);
    
    void onDestroy(final TileEntity p0);
}
