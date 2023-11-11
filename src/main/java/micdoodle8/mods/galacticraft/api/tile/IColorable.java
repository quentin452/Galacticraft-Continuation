package micdoodle8.mods.galacticraft.api.tile;

import net.minecraftforge.common.util.*;

public interface IColorable
{
    void setColor(final byte p0);
    
    byte getColor();
    
    void onAdjacentColorChanged(final ForgeDirection p0);
}
