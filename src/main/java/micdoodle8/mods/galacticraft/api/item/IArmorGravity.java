package micdoodle8.mods.galacticraft.api.item;

import net.minecraft.entity.player.*;

public interface IArmorGravity
{
    int gravityOverrideIfLow(final EntityPlayer p0);
    
    int gravityOverrideIfHigh(final EntityPlayer p0);
}
