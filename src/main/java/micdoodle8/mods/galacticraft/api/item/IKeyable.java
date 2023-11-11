package micdoodle8.mods.galacticraft.api.item;

import net.minecraft.entity.player.*;
import net.minecraft.item.*;

public interface IKeyable
{
    int getTierOfKeyRequired();
    
    boolean onValidKeyActivated(final EntityPlayer p0, final ItemStack p1, final int p2);
    
    boolean onActivatedWithoutKey(final EntityPlayer p0, final int p1);
    
    boolean canBreak();
}
