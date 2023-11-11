package micdoodle8.mods.galacticraft.api.item;

import net.minecraft.item.*;

public interface IItemThermal
{
    int getThermalStrength();
    
    boolean isValidForSlot(final ItemStack p0, final int p1);
}
