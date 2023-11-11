package micdoodle8.mods.galacticraft.core.energy.item;

import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.item.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import micdoodle8.mods.miccore.*;
import net.minecraft.entity.*;

public class ElectricItemManagerIC2
{
    @Annotations.RuntimeInterface(clazz = "ic2.api.item.IElectricItemManager", modID = "IC2")
    public int charge(final ItemStack itemStack, final int amount, final int tier, final boolean ignoreTransferLimit, final boolean simulate) {
        if (itemStack.getItem() instanceof IItemElectricBase) {
            final IItemElectricBase item = (IItemElectricBase)itemStack.getItem();
            final float energy = amount * EnergyConfigHandler.IC2_RATIO;
            float rejectedElectricity = Math.max(item.getElectricityStored(itemStack) + energy - item.getMaxElectricityStored(itemStack), 0.0f);
            float energyToReceive = energy - rejectedElectricity;
            if (!ignoreTransferLimit && energyToReceive > item.getMaxTransferGC(itemStack)) {
                rejectedElectricity += energyToReceive - item.getMaxTransferGC(itemStack);
                energyToReceive = item.getMaxTransferGC(itemStack);
            }
            if (!simulate) {
                item.setElectricity(itemStack, item.getElectricityStored(itemStack) + energyToReceive);
            }
            return (int)(energyToReceive / EnergyConfigHandler.IC2_RATIO);
        }
        return 0;
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.item.IElectricItemManager", modID = "IC2")
    public int discharge(final ItemStack itemStack, final int amount, final int tier, final boolean ignoreTransferLimit, final boolean simulate) {
        if (itemStack.getItem() instanceof IItemElectricBase) {
            final IItemElectricBase item = (IItemElectricBase)itemStack.getItem();
            final float energy = amount / EnergyConfigHandler.TO_IC2_RATIO;
            float energyToTransfer = Math.min(item.getElectricityStored(itemStack), energy);
            if (!ignoreTransferLimit) {
                energyToTransfer = Math.min(energyToTransfer, item.getMaxTransferGC(itemStack));
            }
            if (!simulate) {
                item.setElectricity(itemStack, item.getElectricityStored(itemStack) - energyToTransfer);
            }
            return (int)(energyToTransfer * EnergyConfigHandler.TO_IC2_RATIO);
        }
        return 0;
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.item.IElectricItemManager", modID = "IC2")
    public int getCharge(final ItemStack itemStack) {
        if (itemStack.getItem() instanceof IItemElectricBase) {
            final IItemElectricBase item = (IItemElectricBase)itemStack.getItem();
            return (int)(item.getElectricityStored(itemStack) * EnergyConfigHandler.TO_IC2_RATIO);
        }
        return 0;
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.item.IElectricItemManager", modID = "IC2")
    public boolean canUse(final ItemStack itemStack, final int amount) {
        return itemStack.getItem() instanceof IItemElectricBase && this.getCharge(itemStack) >= amount;
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.item.IElectricItemManager", modID = "IC2")
    public boolean use(final ItemStack itemStack, final int amount, final EntityLivingBase entity) {
        return itemStack.getItem() instanceof IItemElectricBase && this.discharge(itemStack, amount, 1, true, false) >= amount - 1;
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.item.IElectricItemManager", modID = "IC2")
    public void chargeFromArmor(final ItemStack itemStack, final EntityLivingBase entity) {
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.item.IElectricItemManager", modID = "IC2")
    public String getToolTip(final ItemStack itemStack) {
        return null;
    }
}
