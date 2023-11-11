package micdoodle8.mods.galacticraft.api.item;

import net.minecraft.item.*;
import net.minecraft.entity.player.*;

public interface IBreathableArmor
{
    boolean handleGearType(final EnumGearType p0);
    
    boolean canBreathe(final ItemStack p0, final EntityPlayer p1, final EnumGearType p2);
    
    public enum EnumGearType
    {
        HELMET, 
        GEAR, 
        TANK1, 
        TANK2;
    }
}
