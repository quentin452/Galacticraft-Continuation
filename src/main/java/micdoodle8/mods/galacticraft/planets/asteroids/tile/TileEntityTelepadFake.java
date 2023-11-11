package micdoodle8.mods.galacticraft.planets.asteroids.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import java.lang.ref.*;
import net.minecraft.tileentity.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.power.*;
import net.minecraft.world.*;
import net.minecraft.nbt.*;
import java.util.*;
import net.minecraftforge.common.util.*;
import net.minecraft.item.*;

public class TileEntityTelepadFake extends TileBaseElectricBlock
{
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public BlockVec3 mainBlockPosition;
    private WeakReference<TileEntityShortRangeTelepad> mainTelepad;
    
    public TileEntityTelepadFake() {
        this.mainTelepad = null;
    }
    
    public void setMainBlock(final BlockVec3 mainBlock) {
        this.mainBlockPosition = mainBlock.clone();
        if (!this.worldObj.isRemote) {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }
    
    public void onBlockRemoval() {
        final TileEntityShortRangeTelepad telepad = this.getBaseTelepad();
        if (telepad != null) {
            telepad.onDestroy((TileEntity)this);
        }
    }
    
    public boolean onActivated(final EntityPlayer par5EntityPlayer) {
        final TileEntityShortRangeTelepad telepad = this.getBaseTelepad();
        return telepad != null && telepad.onActivated(par5EntityPlayer);
    }
    
    public void updateEntity() {
        super.updateEntity();
        final TileEntityShortRangeTelepad telepad = this.getBaseTelepad();
        if (telepad != null) {
            this.storage.setCapacity(telepad.storage.getCapacityGC());
            this.storage.setMaxExtract(telepad.storage.getMaxExtract());
            this.storage.setMaxReceive(telepad.storage.getMaxReceive());
            this.extractEnergyGC((EnergySource)null, telepad.receiveEnergyGC((EnergySource)null, this.getEnergyStoredGC(), false), false);
        }
    }
    
    private TileEntityShortRangeTelepad getBaseTelepad() {
        if (this.mainBlockPosition == null) {
            return null;
        }
        if (this.mainTelepad == null) {
            final TileEntity tileEntity = this.mainBlockPosition.getTileEntity((IBlockAccess)this.worldObj);
            if (tileEntity != null && tileEntity instanceof TileEntityShortRangeTelepad) {
                this.mainTelepad = new WeakReference<TileEntityShortRangeTelepad>((TileEntityShortRangeTelepad)tileEntity);
            }
        }
        if (this.mainTelepad == null) {
            this.worldObj.setBlockToAir(this.mainBlockPosition.x, this.mainBlockPosition.y, this.mainBlockPosition.z);
        }
        else {
            final TileEntityShortRangeTelepad telepad = this.mainTelepad.get();
            if (telepad != null) {
                return telepad;
            }
            this.worldObj.removeTileEntity(this.xCoord, this.yCoord, this.zCoord);
        }
        return null;
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.mainBlockPosition = new BlockVec3(nbt.getCompoundTag("mainBlockPosition"));
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (this.mainBlockPosition != null) {
            nbt.setTag("mainBlockPosition", (NBTBase)this.mainBlockPosition.writeToNBT(new NBTTagCompound()));
        }
    }
    
    public double getPacketRange() {
        return 30.0;
    }
    
    public int getPacketCooldown() {
        return 50;
    }
    
    public boolean isNetworkedTile() {
        return true;
    }
    
    public void getNetworkedData(final ArrayList<Object> sendData) {
        if (this.mainBlockPosition == null && (this.worldObj.isRemote || !this.resetMainBlockPosition())) {
            return;
        }
        super.getNetworkedData((ArrayList)sendData);
    }
    
    private boolean resetMainBlockPosition() {
        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                for (int y = -2; y < 1; y += 2) {
                    final BlockVec3 vecToCheck = new BlockVec3(this.xCoord + x, this.yCoord + y, this.zCoord + z);
                    if (vecToCheck.getTileEntity((IBlockAccess)this.worldObj) instanceof TileEntityShortRangeTelepad) {
                        this.setMainBlock(vecToCheck);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean shouldUseEnergy() {
        return false;
    }
    
    public ForgeDirection getElectricInputDirection() {
        if (this.getBlockMetadata() != 0) {
            return null;
        }
        return ForgeDirection.UP;
    }
    
    public ItemStack getBatteryInSlot() {
        return null;
    }
}
