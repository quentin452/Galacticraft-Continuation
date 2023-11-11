package micdoodle8.mods.galacticraft.core.energy.tile;

import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.power.*;
import micdoodle8.mods.galacticraft.api.transmission.*;

public abstract class EnergyStorageTile extends TileEntityAdvanced implements IEnergyHandlerGC, IElectrical
{
    public static final float STANDARD_CAPACITY = 16000.0f;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public EnergyStorage storage;
    public int tierGC;
    public int poweredByTierGC;
    
    public EnergyStorageTile() {
        this.storage = new EnergyStorage(16000.0f, 10.0f);
        this.tierGC = 1;
        this.poweredByTierGC = 1;
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.storage.readFromNBT(nbt);
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        this.storage.writeToNBT(nbt);
    }
    
    public abstract ReceiverMode getModeFromDirection(final ForgeDirection p0);
    
    public float receiveEnergyGC(final EnergySource from, final float amount, final boolean simulate) {
        return this.storage.receiveEnergyGC(amount, simulate);
    }
    
    public float extractEnergyGC(final EnergySource from, final float amount, final boolean simulate) {
        return this.storage.extractEnergyGC(amount, simulate);
    }
    
    public boolean nodeAvailable(final EnergySource from) {
        return from instanceof EnergySource.EnergySourceAdjacent && this.getModeFromDirection(((EnergySource.EnergySourceAdjacent)from).direction) != ReceiverMode.UNDEFINED;
    }
    
    public float getEnergyStoredGC(final EnergySource from) {
        return this.storage.getEnergyStoredGC();
    }
    
    public float getEnergyStoredGC() {
        return this.storage.getEnergyStoredGC();
    }
    
    public float getMaxEnergyStoredGC(final EnergySource from) {
        return this.storage.getCapacityGC();
    }
    
    public float getMaxEnergyStoredGC() {
        return this.storage.getCapacityGC();
    }
    
    public boolean canConnect(final ForgeDirection direction, final NetworkType type) {
        return false;
    }
    
    public float receiveElectricity(final ForgeDirection from, final float receive, final int tier, final boolean doReceive) {
        this.poweredByTierGC = tier;
        return this.storage.receiveEnergyGC(receive, !doReceive);
    }
    
    public float provideElectricity(final ForgeDirection from, final float request, final boolean doProvide) {
        return this.storage.extractEnergyGC(request, !doProvide);
    }
    
    public float getRequest(final ForgeDirection direction) {
        return Math.min(this.storage.getCapacityGC() - this.storage.getEnergyStoredGC(), this.storage.getMaxReceive());
    }
    
    public float getProvide(final ForgeDirection direction) {
        return 0.0f;
    }
    
    public int getTierGC() {
        return this.tierGC;
    }
    
    public void setTierGC(final int newTier) {
        this.tierGC = newTier;
    }
}
