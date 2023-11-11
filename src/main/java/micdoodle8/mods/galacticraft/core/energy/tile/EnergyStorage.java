package micdoodle8.mods.galacticraft.core.energy.tile;

import micdoodle8.mods.galacticraft.api.power.*;
import net.minecraft.nbt.*;

public class EnergyStorage implements IEnergyStorageGC
{
    protected float energy;
    protected float capacity;
    protected float maxReceive;
    protected float maxExtract;
    protected float maxExtractRemaining;
    
    public EnergyStorage(final float capacity) {
        this(capacity, 60.0f, 30.0f);
    }
    
    public EnergyStorage(final float capacity, final float maxTransfer) {
        this(capacity, 2.5f * maxTransfer, maxTransfer);
    }
    
    public EnergyStorage(final float capacity, final float maxReceive, final float maxExtract) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.maxExtractRemaining = maxExtract;
    }
    
    public EnergyStorage readFromNBT(final NBTTagCompound nbt) {
        this.energy = nbt.getFloat("EnergyF");
        return this;
    }
    
    public NBTTagCompound writeToNBT(final NBTTagCompound nbt) {
        if (this.energy < 0.0f) {
            this.energy = 0.0f;
        }
        nbt.setFloat("EnergyF", Math.min(this.energy, this.capacity));
        return nbt;
    }
    
    public void setCapacity(final float capacity) {
        this.capacity = capacity;
        if (this.energy > capacity) {
            this.energy = capacity;
        }
    }
    
    public void setMaxReceive(final float maxReceive) {
        this.maxReceive = maxReceive;
    }
    
    public void setMaxExtract(final float maxExtract) {
        this.maxExtract = maxExtract;
        this.maxExtractRemaining = maxExtract;
        this.maxReceive = 2.5f * maxExtract;
    }
    
    public void setEnergyStored(final float energy) {
        this.energy = Math.max(0.0f, Math.min(energy, this.capacity));
    }
    
    public float getMaxReceive() {
        return this.maxReceive;
    }
    
    public float getMaxExtract() {
        return this.maxExtract;
    }
    
    public float receiveEnergyGC(final float maxReceive, final boolean simulate) {
        final float energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            this.energy += energyReceived;
        }
        return energyReceived;
    }
    
    public float receiveEnergyGC(final float amount) {
        final float energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, amount));
        this.energy += energyReceived;
        return energyReceived;
    }
    
    public float extractEnergyGC(final float amount, final boolean simulate) {
        final float energyExtracted = Math.min(this.energy, Math.min(this.maxExtractRemaining, amount));
        if (!simulate) {
            this.energy -= energyExtracted;
        }
        return energyExtracted;
    }
    
    public float extractEnergyGCnoMax(final float amount, final boolean simulate) {
        final float energyExtracted = Math.min(this.energy, amount);
        if (!simulate) {
            this.energy -= energyExtracted;
        }
        return energyExtracted;
    }
    
    public float getEnergyStoredGC() {
        return this.energy;
    }
    
    public float getCapacityGC() {
        return this.capacity;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof EnergyStorage) {
            final EnergyStorage storage = (EnergyStorage)obj;
            return storage.getEnergyStoredGC() == this.energy && storage.getCapacityGC() == this.capacity && storage.getMaxReceive() == this.maxReceive && storage.getMaxExtract() == this.maxExtract;
        }
        return false;
    }
}
