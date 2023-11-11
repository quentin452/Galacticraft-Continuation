package micdoodle8.mods.galacticraft.core.energy.tile;

import micdoodle8.mods.galacticraft.api.power.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.tileentity.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.transmission.grid.*;
import micdoodle8.mods.galacticraft.api.item.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import cofh.api.energy.*;
import mekanism.api.energy.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.item.*;
import java.lang.reflect.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import micdoodle8.mods.miccore.*;
import net.minecraft.util.*;

public class TileBaseUniversalElectricalSource extends TileBaseUniversalElectrical
{
    public float produce() {
        this.storage.maxExtractRemaining = this.storage.maxExtract;
        final float produced = this.extractEnergyGC((EnergySource)null, this.produce(false), false);
        final EnergyStorage storage = this.storage;
        storage.maxExtractRemaining -= produced;
        if (this.storage.maxExtractRemaining < 0.0f) {
            this.storage.maxExtractRemaining = 0.0f;
        }
        return produced;
    }
    
    public float produce(final boolean simulate) {
        float amountProduced = 0.0f;
        if (!this.worldObj.isRemote) {
            final EnumSet<ForgeDirection> outputDirections = (EnumSet<ForgeDirection>)this.getElectricalOutputDirections();
            outputDirections.remove(ForgeDirection.UNKNOWN);
            final BlockVec3 thisVec = new BlockVec3((TileEntity)this);
            for (final ForgeDirection direction : outputDirections) {
                final TileEntity tileAdj = thisVec.getTileEntityOnSide(this.worldObj, direction);
                if (tileAdj != null) {
                    final float toSend = this.extractEnergyGC((EnergySource)null, Math.min(this.getEnergyStoredGC() - amountProduced, this.getEnergyStoredGC() / outputDirections.size()), true);
                    if (toSend <= 0.0f) {
                        continue;
                    }
                    if (tileAdj instanceof TileBaseConductor) {
                        final IElectricityNetwork network = ((IConductor)tileAdj).getNetwork();
                        if (network == null) {
                            continue;
                        }
                        amountProduced += toSend - network.produce(toSend, !simulate, this.tierGC, new TileEntity[] { (TileEntity)this });
                    }
                    else if (tileAdj instanceof TileBaseUniversalElectrical) {
                        amountProduced += ((TileBaseUniversalElectrical)tileAdj).receiveElectricity(direction.getOpposite(), toSend, this.tierGC, !simulate);
                    }
                    else {
                        amountProduced += EnergyUtil.otherModsEnergyTransfer(tileAdj, direction.getOpposite(), toSend, simulate);
                    }
                }
            }
        }
        return amountProduced;
    }
    
    public void recharge(final ItemStack itemStack) {
        if (itemStack != null) {
            final Item item = itemStack.getItem();
            final float maxExtractSave = this.storage.getMaxExtract();
            if (this.tierGC > 1) {
                this.storage.setMaxExtract(maxExtractSave * 2.5f);
            }
            final float energyToCharge = this.storage.extractEnergyGC(this.storage.getMaxExtract(), true);
            if (item instanceof IItemElectric) {
                this.storage.extractEnergyGC(ElectricItemHelper.chargeItem(itemStack, energyToCharge), false);
            }
            else if (EnergyConfigHandler.isRFAPILoaded() && item instanceof IEnergyContainerItem) {
                this.storage.extractEnergyGC(((IEnergyContainerItem)item).receiveEnergy(itemStack, (int)(energyToCharge * EnergyConfigHandler.TO_RF_RATIO), false) / EnergyConfigHandler.TO_RF_RATIO, false);
            }
            else if (EnergyConfigHandler.isMekanismLoaded() && item instanceof IEnergizedItem && ((IEnergizedItem)item).canReceive(itemStack)) {
                this.storage.extractEnergyGC((float)EnergizedItemManager.charge(itemStack, (double)(energyToCharge * EnergyConfigHandler.TO_MEKANISM_RATIO)) / EnergyConfigHandler.TO_MEKANISM_RATIO, false);
            }
            else if (EnergyConfigHandler.isIndustrialCraft2Loaded()) {
                try {
                    final Class<?> itemElectricIC2 = Class.forName("ic2.api.item.ISpecialElectricItem");
                    final Class<?> itemElectricIC2B = Class.forName("ic2.api.item.IElectricItem");
                    final Class<?> itemManagerIC2 = Class.forName("ic2.api.item.IElectricItemManager");
                    if (itemElectricIC2.isInstance(item)) {
                        final Object IC2item = itemElectricIC2.cast(item);
                        final Method getMan = itemElectricIC2.getMethod("getManager", ItemStack.class);
                        final Object IC2manager = getMan.invoke(IC2item, itemStack);
                        double result;
                        if (VersionUtil.mcVersion1_7_2) {
                            final Method methodCharge = itemManagerIC2.getMethod("charge", ItemStack.class, Integer.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE);
                            result = (int)methodCharge.invoke(IC2manager, itemStack, (int)(energyToCharge * EnergyConfigHandler.TO_IC2_RATIO), this.tierGC + 1, false, false);
                        }
                        else {
                            final Method methodCharge = itemManagerIC2.getMethod("charge", ItemStack.class, Double.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE);
                            result = (double)methodCharge.invoke(IC2manager, itemStack, energyToCharge * EnergyConfigHandler.TO_IC2_RATIO, this.tierGC + 1, false, false);
                        }
                        final float energy = (float)result / EnergyConfigHandler.TO_IC2_RATIO;
                        this.storage.extractEnergyGC(energy, false);
                    }
                    else if (itemElectricIC2B.isInstance(item)) {
                        final Class<?> electricItemIC2 = Class.forName("ic2.api.item.ElectricItem");
                        final Object IC2manager2 = electricItemIC2.getField("manager").get(null);
                        double result2;
                        if (VersionUtil.mcVersion1_7_2) {
                            final Method methodCharge2 = itemManagerIC2.getMethod("charge", ItemStack.class, Integer.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE);
                            result2 = (int)methodCharge2.invoke(IC2manager2, itemStack, (int)(energyToCharge * EnergyConfigHandler.TO_IC2_RATIO), this.tierGC + 1, false, false);
                        }
                        else {
                            final Method methodCharge2 = itemManagerIC2.getMethod("charge", ItemStack.class, Double.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE);
                            result2 = (double)methodCharge2.invoke(IC2manager2, itemStack, energyToCharge * EnergyConfigHandler.TO_IC2_RATIO, this.tierGC + 1, false, false);
                        }
                        final float energy2 = (float)result2 / EnergyConfigHandler.TO_IC2_RATIO;
                        this.storage.extractEnergyGC(energy2, false);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (this.tierGC > 1) {
                this.storage.setMaxExtract(maxExtractSave);
            }
        }
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.energy.tile.IEnergyEmitter", modID = "IC2")
    public boolean emitsEnergyTo(final TileEntity receiver, final ForgeDirection direction) {
        if (receiver instanceof IElectrical || receiver instanceof IConductor) {
            return false;
        }
        try {
            final Class<?> energyTile = Class.forName("ic2.api.energy.tile.IEnergyTile");
            if (!energyTile.isInstance(receiver)) {
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return this.getElectricalOutputDirections().contains(direction);
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.energy.tile.IEnergySource", modID = "IC2")
    public double getOfferedEnergy() {
        if (EnergyConfigHandler.disableIC2Output) {
            return 0.0;
        }
        return this.getProvide(ForgeDirection.UNKNOWN) * EnergyConfigHandler.TO_IC2_RATIO;
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.energy.tile.IEnergySource", modID = "IC2")
    public void drawEnergy(final double amount) {
        if (EnergyConfigHandler.disableIC2Output) {
            return;
        }
        this.storage.extractEnergyGC((float)amount / EnergyConfigHandler.TO_IC2_RATIO, false);
    }
    
    @Annotations.VersionSpecific(version = "[1.7.10]")
    @Annotations.RuntimeInterface(clazz = "ic2.api.energy.tile.IEnergySource", modID = "IC2")
    public int getSourceTier() {
        return this.tierGC + 1;
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.energy.ICableOutputter", modID = "Mekanism")
    public boolean canOutputTo(final ForgeDirection side) {
        return this.getElectricalOutputDirections().contains(side);
    }
    
    public float getProvide(final ForgeDirection direction) {
        if (direction == ForgeDirection.UNKNOWN && EnergyConfigHandler.isIndustrialCraft2Loaded()) {
            final TileEntity tile = new BlockVec3((TileEntity)this).getTileEntityOnSide(this.worldObj, this.getElectricalOutputDirectionMain());
            if (tile instanceof IConductor) {
                return 0.0f;
            }
        }
        if (this.getElectricalOutputDirections().contains(direction)) {
            return this.storage.extractEnergyGC(Float.MAX_VALUE, true);
        }
        return 0.0f;
    }
    
    public ForgeDirection getElectricalOutputDirectionMain() {
        return ForgeDirection.UNKNOWN;
    }
    
    @Annotations.RuntimeInterface(clazz = "buildcraft.api.power.IPowerEmitter", modID = "")
    public boolean canEmitPowerFrom(final ForgeDirection side) {
        return this.getElectricalOutputDirections().contains(side);
    }
    
    @Annotations.RuntimeInterface(clazz = "cofh.api.energy.IEnergyProvider", modID = "")
    public int extractEnergy(final ForgeDirection from, final int maxExtract, final boolean simulate) {
        if (EnergyConfigHandler.disableRFOutput) {
            return 0;
        }
        if (!this.getElectricalOutputDirections().contains(from)) {
            return 0;
        }
        return MathHelper.floor_float(this.storage.extractEnergyGC(maxExtract / EnergyConfigHandler.TO_RF_RATIO, !simulate) * EnergyConfigHandler.TO_RF_RATIO);
    }
}
