package micdoodle8.mods.galacticraft.core.energy.tile;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.relauncher.Side;
import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine;
import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector;
import micdoodle8.mods.galacticraft.core.util.Annotations.NetworkedField;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil;

@Interface(modid = "IC2API", iface = "ic2.api.tile.IWrenchable")
public abstract class TileBaseElectricBlock extends TileBaseUniversalElectrical
    implements IDisableableMachine, IConnector {
    // public int energyPerTick = 200;
    // private final float ueMaxEnergy;

    @NetworkedField(targetSide = Side.CLIENT)
    public boolean disabled = false;

    @NetworkedField(targetSide = Side.CLIENT)
    public int disableCooldown = 0;

    @NetworkedField(targetSide = Side.CLIENT)
    public boolean hasEnoughEnergyToRun = false;

    public boolean noRedstoneControl = false;

    public boolean shouldPullEnergy() {
        return this.shouldUseEnergy() || this.getEnergyStoredGC(null) < this.getMaxEnergyStoredGC();
    }

    public abstract boolean shouldUseEnergy();

    public abstract ForgeDirection getElectricInputDirection();

    public abstract ItemStack getBatteryInSlot();

    // public TileBaseElectricBlock()
    // {
    // this.storage.setMaxReceive(ueWattsPerTick);
    // this.storage.setMaxExtract(0);
    // this.storage.setCapacity(maxEnergy);
    //// this.ueMaxEnergy = maxEnergy;
    //// this.ueWattsPerTick = ueWattsPerTick;
    //
    // /*
    // * if (PowerFramework.currentFramework != null) { this.bcPowerProvider =
    // * new GCCoreLinkedPowerProvider(this);
    // * this.bcPowerProvider.configure(20, 1, 10, 10, 1000); }
    // */
    // }

    // @Override
    // public float getMaxEnergyStored()
    // {
    // return this.ueMaxEnergy;
    // }

    public int getScaledElecticalLevel(int i) {
        return (int) Math.floor(this.getEnergyStoredGC(null) * i / this.getMaxEnergyStoredGC(null));
        // - this.ueWattsPerTick;
    }

    // @Override
    // public float getRequest(ForgeDirection direction)
    // {
    // if (this.shouldPullEnergy())
    // {
    // return this.ueWattsPerTick * 2;
    // }
    // else
    // {
    // return 0;
    // }
    // }
    //
    // @Override
    // public float getProvide(ForgeDirection direction)
    // {
    // return 0;
    // }

    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            if (this.shouldPullEnergy() && this.getEnergyStoredGC(null) < this.getMaxEnergyStoredGC(null)
                && this.getBatteryInSlot() != null
                && this.getElectricInputDirection() != null) {
                this.discharge(this.getBatteryInSlot());
            }

            if (this.getEnergyStoredGC(null) > this.storage.getMaxExtract() && (this.noRedstoneControl
                || !RedstoneUtil.isBlockReceivingRedstone(this.worldObj, this.xCoord, this.yCoord, this.zCoord))) {
                this.hasEnoughEnergyToRun = true;
                if (this.shouldUseEnergy()) {
                    this.storage.extractEnergyGC(this.storage.getMaxExtract(), false);
                } else {
                    this.slowDischarge();
                }
            } else {
                this.hasEnoughEnergyToRun = false;
                this.slowDischarge();
            }
        }

        super.updateEntity();

        if (!this.worldObj.isRemote && this.disableCooldown > 0) {
            this.disableCooldown--;
        }
    }

    public void slowDischarge() {
        if (this.ticks % 10 == 0) {
            this.storage.extractEnergyGC(5F, false);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setBoolean("isDisabled", this.getDisabled(0));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        this.setDisabled(0, nbt.getBoolean("isDisabled"));
    }

    @Override
    public void setDisabled(int index, boolean disabled) {
        if (this.disableCooldown == 0) {
            this.disabled = disabled;
            this.disableCooldown = 10;
        }
    }

    @Override
    public boolean getDisabled(int index) {
        return this.disabled;
    }

    @Override
    public EnumSet<ForgeDirection> getElectricalInputDirections() {
        if (this.getElectricInputDirection() == null) {
            return EnumSet.noneOf(ForgeDirection.class);
        }

        return EnumSet.of(this.getElectricInputDirection());
    }

    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
            && entityplayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public boolean canConnect(ForgeDirection direction, NetworkType type) {
        if (direction == null || ForgeDirection.UNKNOWN.equals(direction) || type != NetworkType.POWER) {
            return false;
        }

        return direction == this.getElectricInputDirection();
    }

    public String getGUIstatus() {
        if (!this.noRedstoneControl
            && RedstoneUtil.isBlockReceivingRedstone(this.worldObj, this.xCoord, this.yCoord, this.zCoord)) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.off.name");
        }

        if (this.getEnergyStoredGC() == 0) {
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
