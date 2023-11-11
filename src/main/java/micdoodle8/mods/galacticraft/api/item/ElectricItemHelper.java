package micdoodle8.mods.galacticraft.api.item;

import net.minecraft.item.*;

public class ElectricItemHelper
{
    public static float chargeItem(final ItemStack itemStack, final float joules) {
        if (itemStack != null && itemStack.getItem() instanceof IItemElectric) {
            return ((IItemElectric)itemStack.getItem()).recharge(itemStack, Math.min(((IItemElectric)itemStack.getItem()).getTransfer(itemStack), joules), true);
        }
        return 0.0f;
    }
    
    public static float dischargeItem(final ItemStack itemStack, final float joules) {
        if (itemStack != null && itemStack.getItem() instanceof IItemElectric) {
            return ((IItemElectric)itemStack.getItem()).discharge(itemStack, Math.min(((IItemElectric)itemStack.getItem()).getMaxElectricityStored(itemStack), joules), true);
        }
        return 0.0f;
    }
    
    public static ItemStack getWithCharge(final ItemStack itemStack, final float joules) {
        if (itemStack != null && itemStack.getItem() instanceof IItemElectric) {
            ((IItemElectric)itemStack.getItem()).setElectricity(itemStack, joules);
            return itemStack;
        }
        return itemStack;
    }
    
    public static ItemStack getWithCharge(final Item item, final float joules) {
        return getWithCharge(new ItemStack(item), joules);
    }
    
    public static ItemStack getCloneWithCharge(final ItemStack itemStack, final float joules) {
        return getWithCharge(itemStack.copy(), joules);
    }
    
    public static ItemStack getUncharged(final ItemStack itemStack) {
        return getWithCharge(itemStack, 0.0f);
    }
    
    public static ItemStack getUncharged(final Item item) {
        return getUncharged(new ItemStack(item));
    }
}
