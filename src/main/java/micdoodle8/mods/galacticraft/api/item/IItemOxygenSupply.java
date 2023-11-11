package micdoodle8.mods.galacticraft.api.item;

import net.minecraft.item.*;

public interface IItemOxygenSupply
{
    float discharge(final ItemStack p0, final float p1);
    
    int getOxygenStored(final ItemStack p0);
}
