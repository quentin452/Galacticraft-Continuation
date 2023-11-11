package micdoodle8.mods.galacticraft.planets.mars.tile;

import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.pathfinding.*;
import net.minecraft.entity.*;
import net.minecraft.nbt.*;

public class TileEntitySlimelingEgg extends TileEntity
{
    public int timeToHatch;
    public String lastTouchedPlayerUUID;
    public String lastTouchedPlayerName;
    
    public TileEntitySlimelingEgg() {
        this.timeToHatch = -1;
        this.lastTouchedPlayerUUID = "";
        this.lastTouchedPlayerName = "";
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            if (this.timeToHatch > 0) {
                --this.timeToHatch;
            }
            else if (this.timeToHatch == 0 && this.lastTouchedPlayerUUID != null && this.lastTouchedPlayerUUID.length() > 0) {
                final int metadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) % 3;
                float colorRed = 0.0f;
                float colorGreen = 0.0f;
                float colorBlue = 0.0f;
                switch (metadata) {
                    case 0: {
                        colorRed = 1.0f;
                        break;
                    }
                    case 1: {
                        colorBlue = 1.0f;
                        break;
                    }
                    case 2: {
                        colorRed = 1.0f;
                        colorGreen = 1.0f;
                        break;
                    }
                }
                final EntitySlimeling slimeling = new EntitySlimeling(this.worldObj, colorRed, colorGreen, colorBlue);
                slimeling.setPosition(this.xCoord + 0.5, this.yCoord + 1.0, this.zCoord + 0.5);
                VersionUtil.setSlimelingOwner(slimeling, this.lastTouchedPlayerUUID);
                slimeling.setOwnerUsername(this.lastTouchedPlayerName);
                if (!this.worldObj.isRemote) {
                    this.worldObj.spawnEntityInWorld((Entity)slimeling);
                }
                slimeling.setTamed(true);
                slimeling.setPathToEntity((PathEntity)null);
                slimeling.setAttackTarget((EntityLivingBase)null);
                slimeling.setHealth(20.0f);
                this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
            }
        }
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.timeToHatch = nbt.getInteger("TimeToHatch");
        VersionUtil.readSlimelingEggFromNBT(this, nbt);
        this.lastTouchedPlayerName = nbt.getString("OwnerUsername");
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("TimeToHatch", this.timeToHatch);
        nbt.setString("OwnerUUID", this.lastTouchedPlayerUUID);
        nbt.setString("OwnerUsername", this.lastTouchedPlayerName);
    }
}
