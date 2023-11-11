package micdoodle8.mods.galacticraft.planets.asteroids.entities;

import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import java.util.*;
import net.minecraft.client.particle.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;

public class EntityEntryPod extends EntityLanderBase implements IScaleableFuelLevel, ICameraZoomEntity, IIgnoreShift
{
    public EntityEntryPod(final World var1) {
        super(var1, 0.0f);
        this.setSize(1.5f, 3.0f);
    }
    
    public EntityEntryPod(final EntityPlayerMP player) {
        super(player, 0.0f);
        this.setSize(1.5f, 3.0f);
    }
    
    public double getInitialMotionY() {
        return -0.5;
    }
    
    public double getMountedYOffset() {
        return this.height - 2.0;
    }
    
    public float getRotateOffset() {
        return -20.0f;
    }
    
    public boolean shouldSpawnParticles() {
        return false;
    }
    
    public Map<Vector3, Vector3> getParticleMap() {
        return null;
    }
    
    public EntityFX getParticle(final Random rand, final double x, final double y, final double z, final double motX, final double motY, final double motZ) {
        return null;
    }
    
    public void tickOnGround() {
    }
    
    public void tickInAir() {
        super.tickInAir();
        if (this.worldObj.isRemote && !this.onGround) {
            this.motionY -= 0.002;
        }
    }
    
    public void onGroundHit() {
    }
    
    public Vector3 getMotionVec() {
        if (this.onGround) {
            return new Vector3(0.0, 0.0, 0.0);
        }
        if (this.ticks >= 40L && this.ticks < 45L) {
            this.motionY = this.getInitialMotionY();
        }
        return new Vector3(this.motionX, this.motionY, this.motionZ);
    }
    
    public float getCameraZoom() {
        return 15.0f;
    }
    
    public boolean defaultThirdPerson() {
        return true;
    }
    
    public boolean pressKey(final int key) {
        return false;
    }
    
    public String getInventoryName() {
        return GCCoreUtil.translate("container.entryPod.name");
    }
    
    public boolean hasCustomInventoryName() {
        return true;
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
    
    public boolean shouldIgnoreShiftExit() {
        return !this.onGround;
    }
}
