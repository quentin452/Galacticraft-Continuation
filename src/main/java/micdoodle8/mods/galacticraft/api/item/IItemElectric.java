package micdoodle8.mods.galacticraft.api.item;

import net.minecraft.item.*;

public interface IItemElectric
{
    float recharge(final ItemStack p0, final float p1, final boolean p2);
    
    float discharge(final ItemStack p0, final float p1, final boolean p2);
    
    float getElectricityStored(final ItemStack p0);
    
    float getMaxElectricityStored(final ItemStack p0);
    
    void setElectricity(final ItemStack p0, final float p1);
    
    float getTransfer(final ItemStack p0);
    
    int getTierGC(final ItemStack p0);
}
