package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.api.transmission.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.oxygen.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import mekanism.api.gas.*;
import micdoodle8.mods.galacticraft.api.transmission.grid.*;

public abstract class TileEntityOxygen extends TileBaseElectricBlock implements IOxygenReceiver, IOxygenStorage
{
    public float maxOxygen;
    public float oxygenPerTick;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public float storedOxygen;
    public float lastStoredOxygen;
    public static int timeSinceOxygenRequest;
    
    public TileEntityOxygen(final float maxOxygen, final float oxygenPerTick) {
        this.maxOxygen = maxOxygen;
        this.oxygenPerTick = oxygenPerTick;
    }
    
    public int getScaledOxygenLevel(final int scale) {
        return (int)Math.floor(this.getOxygenStored() * scale / (this.getMaxOxygenStored() - this.oxygenPerTick));
    }
    
    public abstract boolean shouldUseOxygen();
    
    public int getCappedScaledOxygenLevel(final int scale) {
        return (int)Math.max(Math.min(Math.floor(this.storedOxygen / (double)this.maxOxygen * scale), scale), 0.0);
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            if (TileEntityOxygen.timeSinceOxygenRequest > 0) {
                --TileEntityOxygen.timeSinceOxygenRequest;
            }
            if (this.shouldUseOxygen()) {
                this.storedOxygen = Math.max(this.storedOxygen - this.oxygenPerTick, 0.0f);
            }
        }
        this.lastStoredOxygen = this.storedOxygen;
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("storedOxygen")) {
            this.storedOxygen = (float)nbt.getInteger("storedOxygen");
        }
        else {
            this.storedOxygen = nbt.getFloat("storedOxygenF");
        }
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setFloat("storedOxygenF", this.storedOxygen);
    }
    
    public void setOxygenStored(final float oxygen) {
        this.storedOxygen = Math.max(Math.min(oxygen, this.getMaxOxygenStored()), 0.0f);
    }
    
    public float getOxygenStored() {
        return this.storedOxygen;
    }
    
    public float getMaxOxygenStored() {
        return this.maxOxygen;
    }
    
    public EnumSet<ForgeDirection> getOxygenInputDirections() {
        return EnumSet.allOf(ForgeDirection.class);
    }
    
    public EnumSet<ForgeDirection> getOxygenOutputDirections() {
        return EnumSet.noneOf(ForgeDirection.class);
    }
    
    public boolean canConnect(final ForgeDirection direction, final NetworkType type) {
        if (direction == null || direction.equals((Object)ForgeDirection.UNKNOWN)) {
            return false;
        }
        if (type == NetworkType.OXYGEN) {
            return this.getOxygenInputDirections().contains(direction) || this.getOxygenOutputDirections().contains(direction);
        }
        return type == NetworkType.POWER && super.canConnect(direction, type);
    }
    
    public float receiveOxygen(final ForgeDirection from, final float receive, final boolean doReceive) {
        if (!this.getOxygenInputDirections().contains(from)) {
            return 0.0f;
        }
        if (!doReceive) {
            return this.getOxygenRequest(from);
        }
        return this.receiveOxygen(receive, doReceive);
    }
    
    public float receiveOxygen(final float receive, final boolean doReceive) {
        if (receive > 0.0f) {
            final float prevOxygenStored = this.getOxygenStored();
            final float newStoredOxygen = Math.min(prevOxygenStored + receive, this.getMaxOxygenStored());
            if (doReceive) {
                TileEntityOxygen.timeSinceOxygenRequest = 20;
                this.setOxygenStored(newStoredOxygen);
            }
            return Math.max(newStoredOxygen - prevOxygenStored, 0.0f);
        }
        return 0.0f;
    }
    
    public float provideOxygen(final ForgeDirection from, final float request, final boolean doProvide) {
        if (this.getOxygenOutputDirections().contains(from)) {
            return this.provideOxygen(request, doProvide);
        }
        return 0.0f;
    }
    
    public float provideOxygen(final float request, final boolean doProvide) {
        if (request > 0.0f) {
            final float requestedOxygen = Math.min(request, this.storedOxygen);
            if (doProvide) {
                this.setOxygenStored(this.storedOxygen - requestedOxygen);
            }
            return requestedOxygen;
        }
        return 0.0f;
    }
    
    public void produceOxygen() {
        if (!this.worldObj.isRemote) {
            for (final ForgeDirection direction : this.getOxygenOutputDirections()) {
                if (direction != ForgeDirection.UNKNOWN) {
                    this.produceOxygen(direction);
                }
            }
        }
    }
    
    public boolean produceOxygen(final ForgeDirection outputDirection) {
        final float provide = this.getOxygenProvide(outputDirection);
        if (provide > 0.0f) {
            final TileEntity outputTile = new BlockVec3((TileEntity)this).getTileEntityOnSide(this.worldObj, outputDirection);
            final IOxygenNetwork outputNetwork = NetworkHelper.getOxygenNetworkFromTileEntity(outputTile, outputDirection);
            if (outputNetwork != null) {
                final float powerRequest = outputNetwork.getRequest(new TileEntity[] { (TileEntity)this });
                if (powerRequest > 0.0f) {
                    final float rejectedPower = outputNetwork.produce(provide, new TileEntity[] { (TileEntity)this });
                    this.provideOxygen(Math.max(provide - rejectedPower, 0.0f), true);
                    return true;
                }
            }
            else if (outputTile instanceof IOxygenReceiver) {
                final float requestedOxygen = ((IOxygenReceiver)outputTile).getOxygenRequest(outputDirection.getOpposite());
                if (requestedOxygen > 0.0f) {
                    final float acceptedOxygen = ((IOxygenReceiver)outputTile).receiveOxygen(outputDirection.getOpposite(), provide, true);
                    this.provideOxygen(acceptedOxygen, true);
                    return true;
                }
            }
            else if (EnergyConfigHandler.isMekanismLoaded() && outputTile instanceof IGasHandler && ((IGasHandler)outputTile).canReceiveGas(outputDirection.getOpposite(), (Gas)EnergyConfigHandler.gasOxygen)) {
                final GasStack toSend = new GasStack((Gas)EnergyConfigHandler.gasOxygen, (int)Math.floor(provide));
                int acceptedOxygen2 = 0;
                try {
                    acceptedOxygen2 = ((IGasHandler)outputTile).receiveGas(outputDirection.getOpposite(), toSend);
                }
                catch (Exception ex) {}
                this.provideOxygen((float)acceptedOxygen2, true);
                return true;
            }
        }
        return false;
    }
    
    public float getOxygenRequest(final ForgeDirection direction) {
        if (this.shouldPullOxygen()) {
            return this.oxygenPerTick * 2.0f;
        }
        return 0.0f;
    }
    
    public boolean shouldPullOxygen() {
        return this.storedOxygen < this.maxOxygen;
    }
    
    public float getOxygenProvide(final ForgeDirection direction) {
        return 0.0f;
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public int receiveGas(final ForgeDirection side, final GasStack stack, final boolean doTransfer) {
        if (!stack.getGas().getName().equals("oxygen")) {
            return 0;
        }
        return (int)Math.floor(this.receiveOxygen((float)stack.amount, doTransfer));
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public int receiveGas(final ForgeDirection side, final GasStack stack) {
        return this.receiveGas(side, stack, true);
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public GasStack drawGas(final ForgeDirection side, final int amount, final boolean doTransfer) {
        return new GasStack((Gas)EnergyConfigHandler.gasOxygen, (int)Math.floor(this.provideOxygen((float)amount, doTransfer)));
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public GasStack drawGas(final ForgeDirection side, final int amount) {
        return this.drawGas(side, amount, true);
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public boolean canReceiveGas(final ForgeDirection side, final Gas type) {
        return type.getName().equals("oxygen") && this.getOxygenInputDirections().contains(side);
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.IGasHandler", modID = "Mekanism")
    public boolean canDrawGas(final ForgeDirection side, final Gas type) {
        return type.getName().equals("oxygen") && this.getOxygenOutputDirections().contains(side);
    }
    
    @Annotations.RuntimeInterface(clazz = "mekanism.api.gas.ITubeConnection", modID = "Mekanism")
    public boolean canTubeConnect(final ForgeDirection side) {
        return this.canConnect(side, NetworkType.OXYGEN);
    }
}
