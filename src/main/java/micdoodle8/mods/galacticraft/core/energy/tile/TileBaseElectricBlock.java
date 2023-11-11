package micdoodle8.mods.galacticraft.core.energy.tile;

import micdoodle8.mods.galacticraft.api.tile.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.api.power.*;
import net.minecraftforge.common.util.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.transmission.*;
import micdoodle8.mods.galacticraft.core.util.*;

public abstract class TileBaseElectricBlock extends TileBaseUniversalElectrical implements IDisableableMachine, IConnector
{
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean disabled;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int disableCooldown;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean hasEnoughEnergyToRun;
    public boolean noRedstoneControl;
    
    public TileBaseElectricBlock() {
        this.disabled = false;
        this.disableCooldown = 0;
        this.hasEnoughEnergyToRun = false;
        this.noRedstoneControl = false;
    }
    
    public boolean shouldPullEnergy() {
        return this.shouldUseEnergy() || this.getEnergyStoredGC((EnergySource)null) < this.getMaxEnergyStoredGC();
    }
    
    public abstract boolean shouldUseEnergy();
    
    public abstract ForgeDirection getElectricInputDirection();
    
    public abstract ItemStack getBatteryInSlot();
    
    public int getScaledElecticalLevel(final int i) {
        return (int)Math.floor(this.getEnergyStoredGC((EnergySource)null) * i / this.getMaxEnergyStoredGC((EnergySource)null));
    }
    
    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            if (this.shouldPullEnergy() && this.getEnergyStoredGC((EnergySource)null) < this.getMaxEnergyStoredGC((EnergySource)null) && this.getBatteryInSlot() != null && this.getElectricInputDirection() != null) {
                this.discharge(this.getBatteryInSlot());
            }
            if (this.getEnergyStoredGC((EnergySource)null) > this.storage.getMaxExtract() && (this.noRedstoneControl || !RedstoneUtil.isBlockReceivingRedstone(this.worldObj, this.xCoord, this.yCoord, this.zCoord))) {
                this.hasEnoughEnergyToRun = true;
                if (this.shouldUseEnergy()) {
                    this.storage.extractEnergyGC(this.storage.getMaxExtract(), false);
                }
                else {
                    this.slowDischarge();
                }
            }
            else {
                this.hasEnoughEnergyToRun = false;
                this.slowDischarge();
            }
        }
        super.updateEntity();
        if (!this.worldObj.isRemote && this.disableCooldown > 0) {
            --this.disableCooldown;
        }
    }
    
    public void slowDischarge() {
        if (this.ticks % 10 == 0) {
            this.storage.extractEnergyGC(5.0f, false);
        }
    }
    
    @Override
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("isDisabled", this.getDisabled(0));
    }
    
    @Override
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.setDisabled(0, nbt.getBoolean("isDisabled"));
    }
    
    public void setDisabled(final int index, final boolean disabled) {
        if (this.disableCooldown == 0) {
            this.disabled = disabled;
            this.disableCooldown = 10;
        }
    }
    
    public boolean getDisabled(final int index) {
        return this.disabled;
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.tile.IWrenchable", modID = "IC2")
    public boolean wrenchCanSetFacing(final EntityPlayer entityPlayer, final int side) {
        return false;
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.tile.IWrenchable", modID = "IC2")
    public short getFacing() {
        return (short)this.worldObj.getBlockMetadata(MathHelper.floor_double((double)this.xCoord), MathHelper.floor_double((double)this.yCoord), MathHelper.floor_double((double)this.zCoord));
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.tile.IWrenchable", modID = "IC2")
    public void setFacing(final short facing) {
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.tile.IWrenchable", modID = "IC2")
    public boolean wrenchCanRemove(final EntityPlayer entityPlayer) {
        return false;
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.tile.IWrenchable", modID = "IC2")
    public float getWrenchDropRate() {
        return 1.0f;
    }
    
    @Annotations.RuntimeInterface(clazz = "ic2.api.tile.IWrenchable", modID = "IC2")
    public ItemStack getWrenchDrop(final EntityPlayer entityPlayer) {
        return this.getBlockType().getPickBlock((MovingObjectPosition)null, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
    }
    
    @Override
    public EnumSet<ForgeDirection> getElectricalInputDirections() {
        if (this.getElectricInputDirection() == null) {
            return EnumSet.noneOf(ForgeDirection.class);
        }
        return EnumSet.of(this.getElectricInputDirection());
    }
    
    public boolean isUseableByPlayer(final EntityPlayer entityplayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && entityplayer.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }
    
    public boolean canConnect(final ForgeDirection direction, final NetworkType type) {
        return direction != null && !direction.equals((Object)ForgeDirection.UNKNOWN) && type == NetworkType.POWER && direction == this.getElectricInputDirection();
    }
    
    public String getGUIstatus() {
        if (!this.noRedstoneControl && RedstoneUtil.isBlockReceivingRedstone(this.worldObj, this.xCoord, this.yCoord, this.zCoord)) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.off.name");
        }
        if (this.getEnergyStoredGC() == 0.0f) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.missingpower.name");
        }
        if (this.getDisabled(0)) {
            return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.ready.name");
        }
        if (this.getEnergyStoredGC() < this.storage.getMaxExtract()) {
            return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.missingpower.name");
        }
        return EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.status.active.name");
    }
}
