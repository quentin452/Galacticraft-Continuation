package micdoodle8.mods.galacticraft.core.energy.item;

import cpw.mods.fml.relauncher.*;
import cpw.mods.fml.common.versioning.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import net.minecraft.world.*;
import net.minecraft.nbt.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.api.item.*;
import micdoodle8.mods.miccore.*;
import micdoodle8.mods.galacticraft.core.items.*;
import ic2.api.item.*;

public abstract class ItemElectricBase extends Item implements IItemElectricBase
{
    private static Object itemManagerIC2;
    public float transferMax;
    private DefaultArtifactVersion mcVersion;
    
    public ItemElectricBase() {
        this.mcVersion = null;
        this.setMaxStackSize(1);
        this.setMaxDamage(100);
        this.setNoRepair();
        this.setMaxTransfer();
        this.mcVersion = new DefaultArtifactVersion((String)FMLInjectionData.data()[4]);
        if (EnergyConfigHandler.isIndustrialCraft2Loaded()) {
            if (VersionParser.parseRange("[1.7.2]").containsVersion((ArtifactVersion)this.mcVersion)) {
                ItemElectricBase.itemManagerIC2 = new ElectricItemManagerIC2();
            }
            else {
                ItemElectricBase.itemManagerIC2 = new ElectricItemManagerIC2_1710();
            }
        }
    }
    
    protected void setMaxTransfer() {
        this.transferMax = 200.0f;
    }
    
    public float getMaxTransferGC(final ItemStack itemStack) {
        return this.transferMax;
    }
    
    public void addInformation(final ItemStack itemStack, final EntityPlayer entityPlayer, final List list, final boolean par4) {
        String color = "";
        final float joules = this.getElectricityStored(itemStack);
        if (joules <= this.getMaxElectricityStored(itemStack) / 3.0f) {
            color = "�4";
        }
        else if (joules > this.getMaxElectricityStored(itemStack) * 2.0f / 3.0f) {
            color = "�2";
        }
        else {
            color = "�6";
        }
        list.add(color + EnergyDisplayHelper.getEnergyDisplayS(joules) + "/" + EnergyDisplayHelper.getEnergyDisplayS(this.getMaxElectricityStored(itemStack)));
    }
    
    public void onCreated(final ItemStack itemStack, final World par2World, final EntityPlayer par3EntityPlayer) {
        this.setElectricity(itemStack, 0.0f);
    }
    
    public float recharge(final ItemStack itemStack, final float energy, final boolean doReceive) {
        float rejectedElectricity = Math.max(this.getElectricityStored(itemStack) + energy - this.getMaxElectricityStored(itemStack), 0.0f);
        float energyToReceive = energy - rejectedElectricity;
        if (energyToReceive > this.transferMax) {
            rejectedElectricity += energyToReceive - this.transferMax;
            energyToReceive = this.transferMax;
        }
        if (doReceive) {
            this.setElectricity(itemStack, this.getElectricityStored(itemStack) + energyToReceive);
        }
        return energyToReceive;
    }
    
    public float discharge(final ItemStack itemStack, final float energy, final boolean doTransfer) {
        final float energyToTransfer = Math.min(Math.min(this.getElectricityStored(itemStack), energy), this.transferMax);
        if (doTransfer) {
            this.setElectricity(itemStack, this.getElectricityStored(itemStack) - energyToTransfer);
        }
        return energyToTransfer;
    }
    
    public int getTierGC(final ItemStack itemStack) {
        return 1;
    }
    
    public void setElectricity(final ItemStack itemStack, final float joules) {
        if (itemStack.getTagCompound() == null) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        final float electricityStored = Math.max(Math.min(joules, this.getMaxElectricityStored(itemStack)), 0.0f);
        itemStack.getTagCompound().setFloat("electricity", electricityStored);
        itemStack.setItemDamage((int)(100.0f - electricityStored / this.getMaxElectricityStored(itemStack) * 100.0f));
    }
    
    public float getTransfer(final ItemStack itemStack) {
        return Math.min(this.transferMax, this.getMaxElectricityStored(itemStack) - this.getElectricityStored(itemStack));
    }
    
    public float getElectricityStored(final ItemStack itemStack) {
        if (itemStack.getTagCompound() == null) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        float energyStored = 0.0f;
        if (itemStack.getTagCompound().hasKey("electricity")) {
            final NBTBase obj = itemStack.getTagCompound().getTag("electricity");
            if (obj instanceof NBTTagDouble) {
                energyStored = ((NBTTagDouble)obj).func_150288_h();
            }
            else if (obj instanceof NBTTagFloat) {
                energyStored = ((NBTTagFloat)obj).func_150288_h();
            }
        }
        itemStack.setItemDamage((int)(100.0f - energyStored / this.getMaxElectricityStored(itemStack) * 100.0f));
        return energyStored;
    }
    
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(ElectricItemHelper.getUncharged(new ItemStack((Item)this)));
        par3List.add(ElectricItemHelper.getWithCharge(new ItemStack((Item)this), this.getMaxElectricityStored(new ItemStack((Item)this))));
    }
    
    public static boolean isElectricItem(final Item item) {
        return item instanceof IItemElectricBase || (EnergyConfigHandler.isIndustrialCraft2Loaded() && item instanceof ISpecialElectricItem);
    }
    
    public static boolean isElectricItemEmpty(final ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        }
        final Item item = itemstack.getItem();
        if (item instanceof IItemElectricBase) {
            return ((IItemElectricBase)item).getElectricityStored(itemstack) <= 0.0f;
        }
        return EnergyConfigHandler.isIndustrialCraft2Loaded() && item instanceof ISpecialElectricItem && !((ISpecialElectricItem)item).canProvideEnergy(itemstack);
    }
    
    public static boolean isElectricItemCharged(final ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        }
        final Item item = itemstack.getItem();
        if (item instanceof IItemElectricBase) {
            return ((IItemElectricBase)item).getElectricityStored(itemstack) > 0.0f;
        }
        return EnergyConfigHandler.isIndustrialCraft2Loaded() && item instanceof ISpecialElectricItem && ((ISpecialElectricItem)item).canProvideEnergy(itemstack);
    }
    
    @Annotations.RuntimeInterface(clazz = "cofh.api.energy.IEnergyContainerItem", modID = "")
    public int receiveEnergy(final ItemStack container, final int maxReceive, final boolean simulate) {
        return (int)(this.recharge(container, maxReceive * EnergyConfigHandler.RF_RATIO, !simulate) / EnergyConfigHandler.RF_RATIO);
    }
    
    @Annotations.RuntimeInterface(clazz = "cofh.api.energy.IEnergyContainerItem", modID = "")
    public int extractEnergy(final ItemStack container, final int maxExtract, final boolean simulate) {
        return (int)(this.discharge(container, maxExtract / EnergyConfigHandler.TO_RF_RATIO, !simulate) * EnergyConfigHandler.TO_RF_RATIO);
    }
    
    @Annotations.RuntimeInterface(clazz = "cofh.api.energy.IEnergyContainerItem", modID = "")
    public int getEnergyStored(final ItemStack container) {
        return (int)(this.getElectricityStored(container) * EnergyConfigHandler.TO_RF_RATIO);
    }
    
    @Annotations.RuntimeInterface(clazz = "cofh.api.energy.IEnergyContainerItem", modID = "")
    public int getMaxEnergyStored(final ItemStack container) {
        return (int)(this.getMaxElectricityStored(container) * EnergyConfigHandler.TO_RF_RATIO);
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.energy.IEnergizedItem", modID = "Mekanism")
    public double getEnergy(final ItemStack itemStack) {
        return this.getElectricityStored(itemStack) * EnergyConfigHandler.TO_MEKANISM_RATIO;
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.energy.IEnergizedItem", modID = "Mekanism")
    public void setEnergy(final ItemStack itemStack, final double amount) {
        this.setElectricity(itemStack, (float)amount * EnergyConfigHandler.MEKANISM_RATIO);
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.energy.IEnergizedItem", modID = "Mekanism")
    public double getMaxEnergy(final ItemStack itemStack) {
        return this.getMaxElectricityStored(itemStack) * EnergyConfigHandler.TO_MEKANISM_RATIO;
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.energy.IEnergizedItem", modID = "Mekanism")
    public double getMaxTransfer(final ItemStack itemStack) {
        return this.transferMax * EnergyConfigHandler.TO_MEKANISM_RATIO;
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.energy.IEnergizedItem", modID = "Mekanism")
    public boolean canReceive(final ItemStack itemStack) {
        return itemStack != null && !(itemStack.getItem() instanceof ItemBatteryInfinite);
    }
    
    public boolean canSend(final ItemStack itemStack) {
        return true;
    }
    
    public boolean isMetadataSpecific(final ItemStack itemStack) {
        return false;
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.item.ISpecialElectricItem", modID = "IC2")
    public IElectricItemManager getManager(final ItemStack itemstack) {
        return (IElectricItemManager)ItemElectricBase.itemManagerIC2;
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.item.ISpecialElectricItem", modID = "IC2")
    public boolean canProvideEnergy(final ItemStack itemStack) {
        return true;
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.item.ISpecialElectricItem", modID = "IC2")
    public Item getChargedItem(final ItemStack itemStack) {
        return itemStack.getItem();
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.item.ISpecialElectricItem", modID = "IC2")
    public Item getEmptyItem(final ItemStack itemStack) {
        return itemStack.getItem();
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.item.ISpecialElectricItem", modID = "IC2")
    public int getTier(final ItemStack itemStack) {
        return 1;
    }
    
    @Annotations.VersionSpecific(version = "[1.7.10]")
    @Annotations.RuntimeInterface(clazz = "ic2.api.item.ISpecialElectricItem", modID = "IC2")
    public double getMaxCharge(final ItemStack itemStack) {
        return this.getMaxElectricityStored(itemStack) / EnergyConfigHandler.IC2_RATIO;
    }
    
    @Annotations.AltForVersion(version = "[1.7.2]")
    @Annotations.RuntimeInterface(clazz = "ic2.api.item.ISpecialElectricItem", modID = "IC2")
    public int getMaxChargeB(final ItemStack itemStack) {
        return (int)(this.getMaxElectricityStored(itemStack) / EnergyConfigHandler.IC2_RATIO);
    }
    
    @Annotations.VersionSpecific(version = "[1.7.10]")
    @Annotations.RuntimeInterface(clazz = "ic2.api.item.ISpecialElectricItem", modID = "IC2")
    public double getTransferLimit(final ItemStack itemStack) {
        return this.transferMax * EnergyConfigHandler.TO_IC2_RATIO;
    }
    
    @Annotations.VersionSpecific(version = "[1.7.2]")
    @Annotations.RuntimeInterface(clazz = "ic2.api.item.ISpecialElectricItem", modID = "IC2")
    public int getTransferLimitB(final ItemStack itemStack) {
        return (int)(this.transferMax * EnergyConfigHandler.TO_IC2_RATIO);
    }
}
