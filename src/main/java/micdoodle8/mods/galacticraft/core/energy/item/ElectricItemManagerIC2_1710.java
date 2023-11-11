package micdoodle8.mods.galacticraft.core.energy.item;

import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.item.*;
import ic2.api.item.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import net.minecraft.entity.*;

public class ElectricItemManagerIC2_1710 implements IElectricItemManager
{
    public double charge(final ItemStack itemStack, double amount, final int tier, final boolean ignoreTransferLimit, final boolean simulate) {
        if (itemStack.getItem() instanceof IItemElectricBase) {
            final IItemElectricBase item = (IItemElectricBase)itemStack.getItem();
            if (amount > ((ISpecialElectricItem)item).getMaxCharge(itemStack)) {
                amount = ((ISpecialElectricItem)item).getMaxCharge(itemStack);
            }
            final float energy = (float)amount * EnergyConfigHandler.IC2_RATIO;
            final float rejectedElectricity = Math.max(item.getElectricityStored(itemStack) + energy - item.getMaxElectricityStored(itemStack), 0.0f);
            float energyToReceive = energy - rejectedElectricity;
            if (ignoreTransferLimit || energyToReceive > item.getMaxTransferGC(itemStack)) {}
            energyToReceive = item.getMaxTransferGC(itemStack);
            if (!simulate) {
                item.setElectricity(itemStack, item.getElectricityStored(itemStack) + energyToReceive);
            }
            return energyToReceive / EnergyConfigHandler.IC2_RATIO;
        }
        return 0.0;
    }
    
    public double discharge(final ItemStack itemStack, final double amount, final int tier, final boolean ignoreTransferLimit, final boolean externally, final boolean simulate) {
        if (itemStack.getItem() instanceof IItemElectricBase) {
            final IItemElectricBase item = (IItemElectricBase)itemStack.getItem();
            final float energy = (float)amount / EnergyConfigHandler.TO_IC2_RATIO;
            float energyToTransfer = Math.min(item.getElectricityStored(itemStack), energy);
            if (!ignoreTransferLimit) {
                energyToTransfer = Math.min(energyToTransfer, item.getMaxTransferGC(itemStack));
            }
            if (!simulate) {
                item.setElectricity(itemStack, item.getElectricityStored(itemStack) - energyToTransfer);
            }
            return energyToTransfer * EnergyConfigHandler.TO_IC2_RATIO;
        }
        return 0.0;
    }
    
    public double getCharge(final ItemStack itemStack) {
        if (itemStack.getItem() instanceof IItemElectricBase) {
            final IItemElectricBase item = (IItemElectricBase)itemStack.getItem();
            return item.getElectricityStored(itemStack) * EnergyConfigHandler.TO_IC2_RATIO;
        }
        return 0.0;
    }
    
    public boolean canUse(final ItemStack itemStack, final double amount) {
        return itemStack.getItem() instanceof IItemElectricBase && this.getCharge(itemStack) >= amount;
    }
    
    public boolean use(final ItemStack itemStack, final double amount, final EntityLivingBase entity) {
        return itemStack.getItem() instanceof IItemElectricBase && this.discharge(itemStack, amount, 1, true, false, false) >= amount - 1.0;
    }
    
    public void chargeFromArmor(final ItemStack itemStack, final EntityLivingBase entity) {
    }
    
    public String getToolTip(final ItemStack itemStack) {
        return null;
    }
}
