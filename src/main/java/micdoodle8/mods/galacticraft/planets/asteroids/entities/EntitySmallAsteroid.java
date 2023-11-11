package micdoodle8.mods.galacticraft.planets.asteroids.entities;

import net.minecraft.entity.*;
import net.minecraft.world.*;
import net.minecraft.nbt.*;

public class EntitySmallAsteroid extends Entity
{
    public float spinPitch;
    public float spinYaw;
    public int type;
    private boolean firstUpdate;
    
    public EntitySmallAsteroid(final World world) {
        super(world);
        this.firstUpdate = true;
        this.setSize(1.0f, 1.0f);
        this.isImmuneToFire = true;
    }
    
    public void onEntityUpdate() {
        if (!this.firstUpdate) {
            if (Math.abs(this.posX - this.prevPosX) + Math.abs(this.posZ - this.prevPosZ) <= 0.0) {
                this.setDead();
            }
            else if (this.posY > 288.0 || this.posY < -32.0 || this.ticksExisted > 3000) {
                this.setDead();
            }
        }
        super.onEntityUpdate();
        if (!this.worldObj.isRemote) {
            this.setSpinPitch(this.spinPitch);
            this.setSpinYaw(this.spinYaw);
            this.setAsteroidType(this.type);
            this.rotationPitch += this.spinPitch;
            this.rotationYaw += this.spinYaw;
        }
        else {
            this.rotationPitch += this.getSpinPitch();
            this.rotationYaw += this.getSpinYaw();
        }
        final double sqrdMotion = this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;
        if (sqrdMotion < 0.05) {
            this.motionX *= 1.001;
            this.motionY *= 1.001;
            this.motionZ *= 1.001;
        }
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.firstUpdate = false;
    }
    
    protected void entityInit() {
        this.dataWatcher.addObject(10, (Object)0.0f);
        this.dataWatcher.addObject(11, (Object)0.0f);
        this.dataWatcher.addObject(12, (Object)0);
    }
    
    protected void readEntityFromNBT(final NBTTagCompound nbt) {
        this.spinPitch = nbt.getFloat("spinPitch");
        this.spinYaw = nbt.getFloat("spinYaw");
        this.ticksExisted = nbt.getInteger("ageTicks");
    }
    
    protected void writeEntityToNBT(final NBTTagCompound nbt) {
        nbt.setFloat("spinPitch", this.spinPitch);
        nbt.setFloat("spinYaw", this.spinYaw);
        nbt.setInteger("ageTicks", this.ticksExisted);
    }
    
    public float getSpinPitch() {
        return this.dataWatcher.getWatchableObjectFloat(10);
    }
    
    public float getSpinYaw() {
        return this.dataWatcher.getWatchableObjectFloat(11);
    }
    
    public void setSpinPitch(final float pitch) {
        this.dataWatcher.updateObject(10, (Object)pitch);
    }
    
    public void setSpinYaw(final float yaw) {
        this.dataWatcher.updateObject(11, (Object)yaw);
    }
    
    public int getAsteroidType() {
        return this.dataWatcher.getWatchableObjectInt(12);
    }
    
    public void setAsteroidType(final int type) {
        this.dataWatcher.updateObject(12, (Object)type);
    }
}
