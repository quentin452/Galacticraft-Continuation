package micdoodle8.mods.galacticraft.planets.mars.entities;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity;
import micdoodle8.mods.galacticraft.api.entity.IIgnoreShift;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.planets.mars.util.MarsUtil;

public class EntityLandingBalloons extends EntityLanderBase implements IIgnoreShift, ICameraZoomEntity {

    private int groundHitCount;
    private float rotationPitchSpeed;
    private float rotationYawSpeed;

    public EntityLandingBalloons(World world) {
        super(world, 0F);
        this.setSize(2.0F, 2.0F);
        this.rotationPitchSpeed = this.rand.nextFloat();
        this.rotationYawSpeed = this.rand.nextFloat();
    }

    public EntityLandingBalloons(EntityPlayerMP player) {
        super(player, 0F);
        this.setSize(2.0F, 2.0F);
    }

    @Override
    public double getMountedYOffset() {
        return super.getMountedYOffset() - 0.9;
    }

    @Override
    public float getRotateOffset() {
        // Signal no rotate
        return -20.0F;
    }

    @Override
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

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.groundHitCount = nbt.getInteger("GroundHitCount");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("GroundHitCount", this.groundHitCount);
    }

    @Override
    public String getInventoryName() {
        return GCCoreUtil.translate("container.marsLander.name");
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public boolean interactFirst(EntityPlayer var1) {
        if (this.worldObj.isRemote) {
            if (!this.onGround) {
                return false;
            }

            if (this.riddenByEntity != null) {
                this.riddenByEntity.mountEntity(this);
            }

            return true;
        }
        if (this.riddenByEntity == null && this.onGround && var1 instanceof EntityPlayerMP) {
            MarsUtil.openParachestInventory((EntityPlayerMP) var1, this);
        } else if (var1 instanceof EntityPlayerMP) {
            if (!this.onGround) {
                return false;
            }

            var1.mountEntity(null);
        }
        return true;
    }

    @Override
    public boolean pressKey(int key) {
        return false;
    }

    @Override
    public boolean shouldMove() {
        if (this.ticks < 40 || !this.hasReceivedPacket) {
            return false;
        }

        return this.riddenByEntity != null && this.groundHitCount < 14 || !this.onGround;
    }

    @Override
    public boolean shouldSpawnParticles() {
        return false;
    }

    @Override
    public Map<Vector3, Vector3> getParticleMap() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public EntityFX getParticle(Random rand, double x, double y, double z, double motX, double motY, double motZ) {
        return null;
    }

    @Override
    public void tickInAir() {
        if (this.worldObj.isRemote) {
            if (this.groundHitCount == 0) {
                this.motionY = -this.posY / 50.0D;
            } else if (this.groundHitCount < 14 || this.shouldMove()) {
                this.motionY *= 0.95D;
                this.motionY -= 0.08D;
            } else if (!this.shouldMove()) {
                this.motionY = this.motionX = this.motionZ = this.rotationPitchSpeed = this.rotationYawSpeed = 0.0F;
            }
        }
    }

    @Override
    public void tickOnGround() {}

    @Override
    public void onGroundHit() {}

    @Override
    public Vector3 getMotionVec() {
        if (this.onGround && this.groundHitCount < 14) {
            this.groundHitCount++;
            final double mag = 1.0D / this.groundHitCount * 4.0D;
            double mX = this.rand.nextDouble() - 0.5;
            double mY = 1.0D;
            double mZ = this.rand.nextDouble() - 0.5;
            mX *= mag / 3.0D;
            mY *= mag;
            mZ *= mag / 3.0D;
            return new Vector3(mX, mY, mZ);
        }

        if (this.ticks >= 40 && this.ticks < 45) {
            this.motionY = this.getInitialMotionY();
        }

        if (!this.shouldMove()) {
            return new Vector3(0, 0, 0);
        }

        return new Vector3(this.motionX, this.ticks < 40 ? 0 : this.motionY, this.motionZ);
    }

    @Override
    public ArrayList<Object> getNetworkedData() {
        final ArrayList<Object> objList = new ArrayList<>(super.getNetworkedData());
        if (this.worldObj.isRemote && this.hasReceivedPacket && this.groundHitCount <= 14
            || !this.worldObj.isRemote && this.groundHitCount == 14) {
            objList.add(this.groundHitCount);
        }
        return objList;
    }

    @Override
    public int getPacketTickSpacing() {
        return 5;
    }

    @Override
    public double getPacketSendDistance() {
        return 50.0D;
    }

    @Override
    public void readNetworkedData(ByteBuf buffer) {
        try {
            super.readNetworkedData(buffer);

            if (buffer.readableBytes() > 0) {
                this.groundHitCount = buffer.readInt();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean allowDamageSource(DamageSource damageSource) {
        return this.groundHitCount > 0 && super.allowDamageSource(damageSource);
    }

    @Override
    public double getInitialMotionY() {
        return 0;
    }

    @Override
    public float getCameraZoom() {
        return 15.0F;
    }

    @Override
    public boolean defaultThirdPerson() {
        return true;
    }

    @Override
    public boolean shouldIgnoreShiftExit() {
        return this.groundHitCount < 14 || !this.onGround;
    }
}
