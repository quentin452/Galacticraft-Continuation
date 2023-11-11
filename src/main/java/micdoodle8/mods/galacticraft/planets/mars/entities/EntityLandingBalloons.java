package micdoodle8.mods.galacticraft.planets.mars.entities;

import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.world.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.planets.mars.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.client.particle.*;
import cpw.mods.fml.relauncher.*;
import java.util.*;
import io.netty.buffer.*;
import net.minecraft.util.*;

public class EntityLandingBalloons extends EntityLanderBase implements IIgnoreShift, ICameraZoomEntity
{
    private int groundHitCount;
    private float rotationPitchSpeed;
    private float rotationYawSpeed;
    
    public EntityLandingBalloons(final World world) {
        super(world, 0.0f);
        this.setSize(2.0f, 2.0f);
        this.rotationPitchSpeed = this.rand.nextFloat();
        this.rotationYawSpeed = this.rand.nextFloat();
    }
    
    public EntityLandingBalloons(final EntityPlayerMP player) {
        super(player, 0.0f);
        this.setSize(2.0f, 2.0f);
    }
    
    public double getMountedYOffset() {
        return super.getMountedYOffset() - 0.9;
    }
    
    public float getRotateOffset() {
        return -20.0f;
    }
    
    public void onUpdate() {
        if (this.riddenByEntity != null) {
            this.riddenByEntity.onGround = false;
        }
        super.onUpdate();
        if (this.riddenByEntity != null) {
            this.riddenByEntity.onGround = false;
        }
        if (!this.onGround) {
            this.rotationPitch += this.rotationPitchSpeed;
            this.rotationYaw += this.rotationYawSpeed;
        }
    }
    
    protected void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.groundHitCount = nbt.getInteger("GroundHitCount");
    }
    
    protected void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("GroundHitCount", this.groundHitCount);
    }
    
    public String getInventoryName() {
        return GCCoreUtil.translate("container.marsLander.name");
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
            if (this.riddenByEntity == null && this.groundHitCount >= 14 && var1 instanceof EntityPlayerMP) {
                MarsUtil.openParachestInventory((EntityPlayerMP)var1, this);
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
        return this.onGround && false;
    }
    
    public boolean shouldMove() {
        return this.ticks >= 40L && this.hasReceivedPacket && ((this.riddenByEntity != null && this.groundHitCount < 14) || !this.onGround);
    }
    
    public boolean shouldSpawnParticles() {
        return false;
    }
    
    public Map<Vector3, Vector3> getParticleMap() {
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    public EntityFX getParticle(final Random rand, final double x, final double y, final double z, final double motX, final double motY, final double motZ) {
        return null;
    }
    
    public void tickInAir() {
        if (this.worldObj.isRemote) {
            if (this.groundHitCount == 0) {
                this.motionY = -this.posY / 50.0;
            }
            else if (this.groundHitCount < 14 || this.shouldMove()) {
                this.motionY *= 0.95;
                this.motionY -= 0.08;
            }
            else if (!this.shouldMove()) {
                final float n = 0.0f;
                this.rotationYawSpeed = n;
                this.rotationPitchSpeed = n;
                final double motionY = n;
                this.motionZ = motionY;
                this.motionX = motionY;
                this.motionY = motionY;
            }
        }
    }
    
    public void tickOnGround() {
    }
    
    public void onGroundHit() {
    }
    
    public Vector3 getMotionVec() {
        if (this.onGround && this.groundHitCount < 14) {
            ++this.groundHitCount;
            final double mag = 1.0 / this.groundHitCount * 4.0;
            double mX = this.rand.nextDouble() - 0.5;
            double mY = 1.0;
            double mZ = this.rand.nextDouble() - 0.5;
            mX *= mag / 3.0;
            mY *= mag;
            mZ *= mag / 3.0;
            return new Vector3(mX, mY, mZ);
        }
        if (this.ticks >= 40L && this.ticks < 45L) {
            this.motionY = this.getInitialMotionY();
        }
        if (!this.shouldMove()) {
            return new Vector3(0.0, 0.0, 0.0);
        }
        return new Vector3(this.motionX, (this.ticks < 40L) ? 0.0 : this.motionY, this.motionZ);
    }
    
    public ArrayList<Object> getNetworkedData() {
        final ArrayList<Object> objList = new ArrayList<Object>();
        objList.addAll(super.getNetworkedData());
        if ((this.worldObj.isRemote && this.hasReceivedPacket && this.groundHitCount <= 14) || (!this.worldObj.isRemote && this.groundHitCount == 14)) {
            objList.add(this.groundHitCount);
        }
        return objList;
    }
    
    public int getPacketTickSpacing() {
        return 5;
    }
    
    public double getPacketSendDistance() {
        return 50.0;
    }
    
    public void readNetworkedData(final ByteBuf buffer) {
        try {
            super.readNetworkedData(buffer);
            if (buffer.readableBytes() > 0) {
                this.groundHitCount = buffer.readInt();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean allowDamageSource(final DamageSource damageSource) {
        return this.groundHitCount > 0 && super.allowDamageSource(damageSource);
    }
    
    public double getInitialMotionY() {
        return 0.0;
    }
    
    public float getCameraZoom() {
        return 15.0f;
    }
    
    public boolean defaultThirdPerson() {
        return true;
    }
    
    public boolean shouldIgnoreShiftExit() {
        return this.groundHitCount < 14 || !this.onGround;
    }
}
