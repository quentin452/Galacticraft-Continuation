package micdoodle8.mods.galacticraft.api.block;

import net.minecraft.world.*;

public interface IOxygenReliantBlock
{
    void onOxygenRemoved(final World p0, final int p1, final int p2, final int p3);
    
    void onOxygenAdded(final World p0, final int p1, final int p2, final int p3);
}
