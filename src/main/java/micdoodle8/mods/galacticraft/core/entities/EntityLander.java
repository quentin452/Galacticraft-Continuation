package micdoodle8.mods.galacticraft.core.entities;

import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.world.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import java.util.*;
import net.minecraft.client.particle.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.client.fx.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;

public class EntityLander extends EntityLanderBase implements IIgnoreShift, ICameraZoomEntity
{
    private double lastMotionY;
    
    public EntityLander(final World world) {
        super(world, 0.0f);
        this.setSize(3.0f, 4.25f);
    }
    
    public EntityLander(final EntityPlayerMP player) {
        super(player, 0.0f);
    }
    
    public double getMountedYOffset() {
        return this.height - 2.0;
    }
    
    public float getRotateOffset() {
        return 0.0f;
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        this.lastMotionY = this.motionY;
    }
    
    @Override
    protected void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.lastMotionY = this.motionY;
    }
    
    @Override
    protected void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
    }
    
    public String getInventoryName() {
        return GCCoreUtil.translate("container.lander.name");
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public boolean interactFirst(final EntityPlayer var1) {
        if (this.worldObj.isRemote) {
            if (!this.onGround) {
                return false;
            }
            if (this.riddenByEntity != null) {
                this.riddenByEntity.mountEntity((Entity)this);
            }
            return true;
        }
        else {
            if (this.riddenByEntity == null && var1 instanceof EntityPlayerMP) {
                GCCoreUtil.openParachestInv((EntityPlayerMP)var1, this);
                return true;
            }
            if (!(var1 instanceof EntityPlayerMP)) {
                return true;
            }
            if (!this.onGround) {
                return false;
            }
            var1.mountEntity((Entity)null);
            return true;
        }
    }
    
    public boolean pressKey(final int key) {
        if (this.onGround) {
            return false;
        }
        final float turnFactor = 2.0f;
        final float angle = 45.0f;
        switch (key) {
            case 0: {
                this.rotationPitch = Math.min(Math.max(this.rotationPitch - 0.5f * turnFactor, -angle), angle);
                return true;
            }
            case 1: {
                this.rotationPitch = Math.min(Math.max(this.rotationPitch + 0.5f * turnFactor, -angle), angle);
                return true;
            }
            case 2: {
                this.rotationYaw -= 0.5f * turnFactor;
                return true;
            }
            case 3: {
                this.rotationYaw += 0.5f * turnFactor;
                return true;
            }
            case 4: {
                this.motionY = Math.min(this.motionY + 0.029999999329447746, (this.posY < 90.0) ? -0.15 : -1.0);
                return true;
            }
            case 5: {
                this.motionY = Math.min(this.motionY - 0.02199999988079071, -1.0);
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean shouldSpawnParticles() {
        return this.ticks > 40L && this.rotationPitch != 1.0E-7f;
    }
    
    public Map<Vector3, Vector3> getParticleMap() {
        final double x1 = 4.0 * Math.cos(this.rotationYaw * 3.141592653589793 / 180.0) * Math.sin(this.rotationPitch * 3.141592653589793 / 180.0);
        final double z1 = 4.0 * Math.sin(this.rotationYaw * 3.141592653589793 / 180.0) * Math.sin(this.rotationPitch * 3.141592653589793 / 180.0);
        final double y1 = -4.0 * Math.abs(Math.cos(this.rotationPitch * 3.141592653589793 / 180.0));
        new Vector3((Entity)this);
        final Map<Vector3, Vector3> particleMap = new HashMap<Vector3, Vector3>();
        particleMap.put(new Vector3(this.posX, this.posY + 1.0 + this.motionY / 2.0, this.posZ), new Vector3(x1, y1 + this.motionY / 2.0, z1));
        return particleMap;
    }
    
    @SideOnly(Side.CLIENT)
    public EntityFX getParticle(final Random rand, final double x, final double y, final double z, final double motX, final double motY, final double motZ) {
        return (EntityFX)new EntityFXLanderFlame(this.worldObj, x, y, z, motX, motY, motZ, (EntityLivingBase)((this.riddenByEntity instanceof EntityLivingBase) ? this.riddenByEntity : null));
    }
    
    @Override
    public void tickInAir() {
        super.tickInAir();
        if (this.worldObj.isRemote) {
            if (!this.onGround) {
                this.motionY -= 0.008;
            }
            final double motY = -1.0 * Math.sin(this.rotationPitch * 3.141592653589793 / 180.0);
            final double motX = Math.cos(this.rotationYaw * 3.141592653589793 / 180.0) * motY;
            final double motZ = Math.sin(this.rotationYaw * 3.141592653589793 / 180.0) * motY;
            this.motionX = motX / 2.0;
            this.motionZ = motZ / 2.0;
        }
    }
    
    public void tickOnGround() {
        this.rotationPitch = 1.0E-7f;
    }
    
    public void onGroundHit() {
        if (!this.worldObj.isRemote && Math.abs(this.lastMotionY) > 2.0) {
            if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayerMP) {
                this.riddenByEntity.mountEntity((Entity)this);
            }
            this.worldObj.createExplosion((Entity)this, this.posX, this.posY, this.posZ, 12.0f, true);
            this.setDead();
        }
    }
    
    public Vector3 getMotionVec() {
        if (this.onGround) {
            return new Vector3(0.0, 0.0, 0.0);
        }
        if (this.ticks >= 40L && this.ticks < 45L) {
            this.motionY = this.getInitialMotionY();
        }
        return new Vector3(this.motionX, (this.ticks < 40L) ? 0.0 : this.motionY, this.motionZ);
    }
    
    public float getCameraZoom() {
        return 15.0f;
    }
    
    public boolean defaultThirdPerson() {
        return true;
    }
    
    public boolean shouldIgnoreShiftExit() {
        return !this.onGround;
    }
    
    @Override
    public double getInitialMotionY() {
        return -2.5;
    }
    
    protected boolean canTriggerWalking() {
        return false;
    }
    
    public AxisAlignedBB getBoundingBox() {
        return null;
    }
    
    public AxisAlignedBB getCollisionBox(final Entity par1Entity) {
        return null;
    }
    
    public boolean canBePushed() {
        return false;
    }
    
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }
}
